package com.friendbook.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.friendbook.exceptions.ExistingUserNameException;
import com.friendbook.exceptions.IncorrectUserNameException;
import com.friendbook.exceptions.InvalidEmailException;
import com.friendbook.exceptions.InvalidPasswordException;
import com.friendbook.exceptions.WrongCredentialsException;
import com.friendbook.model.post.Post;
import com.friendbook.model.post.PostDao;
import com.friendbook.model.user.User;
import com.friendbook.model.user.UserDao;

@Controller
public class UserController {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PostDao postDao;
	
	@RequestMapping(value="/register", method = RequestMethod.GET)
	public String getRegisterPage() {
		return "register";
	}
	
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String getLoginPage(HttpSession session, Model model) {
		if(session.isNew()) {
			return "login";
		}
		try {
			User user = (User) session.getAttribute("user");
			List<Post> posts = userDao.getPostsByUserID(user.getId());
			model.addAttribute("posts", posts);
			return "index";
		} catch (SQLException e) {
			System.out.println("SQL bug: " + e.getMessage());
			return "error";
		}
	}
	
	@RequestMapping(value="/login", method = RequestMethod.POST)
	public String login(HttpServletRequest req, HttpSession session, Model model) {
		try {
			//get username and password from jsp
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			//try to login if not exception will fire
			userDao.loginCheck(username, password);
			//if successfull, get user
			User user = userDao.getUserByUsername(username);
			//get user's posts and put them in request
			List<Post> posts = userDao.getPostsByUserID(user.getId());
			session.setAttribute("user", user);
			req.setAttribute("posts", posts);
			
			return "index";
		} catch (WrongCredentialsException e) {
			System.out.println("Exception: "+ e.getMessage());
			return "error";
		} catch (Exception e) {
			System.out.println("Some error occured: " + e.getMessage());
			e.printStackTrace();
			return "error";
		}
	}
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public String register(HttpServletRequest request) {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String password2 = request.getParameter("confirm password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		
		if(password.compareTo(password2) !=0 ) {
			System.out.println("Passwords doesn't match");
		}
		
		try {
			User u = new User();
			u.setUsername(username);
			u.setPassword(password);
			u.setEmail(email);
			u.setFirstName(firstName);
			u.setLastName(lastName);
			userDao.saveUser(u);

			return "login";
		} catch (InvalidPasswordException | IncorrectUserNameException | ExistingUserNameException
				| InvalidEmailException e) {
			request.setAttribute("error", e.getMessage());
			return "error";
		} catch (SQLException e) {
			System.out.println("Bug: " + e.getMessage());
			return "error";
		}
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		session.invalidate();
		return "login";
	}
	
	@RequestMapping(value="/getPic", method = RequestMethod.GET)
	public void getPic(HttpServletRequest request, HttpServletResponse response) {
		long postId = Long.parseLong(request.getParameter("postId"));
		try {
			String imagePath = postDao.getPostImageById(postId);
			if(imagePath != null) {
				File f = new File(imagePath);
				InputStream is = new FileInputStream(f);
				OutputStream os = response.getOutputStream();
				int b = is.read();
				while(b != -1) {
					os.write(b);
					b = is.read();
				}
			}
		} catch (SQLException | IOException e) {
			System.out.println("BUG: " + e.getMessage());
		}
	}
	
	@RequestMapping(value="/search", method = RequestMethod.POST)
	public String search(HttpSession session, HttpServletRequest request) {
		User user = (User) session.getAttribute("user");
		String name = request.getParameter("user");
		//cash visited user's object and posts in session
		try {
			User visitedUser = userDao.getUserByNames(name);
			//if the searched user is followed already put a flag and change the follow button to followed in jsp
			//or if this is the same user
			if(userDao.isFollower(user, visitedUser.getId()) || user.getId() == visitedUser.getId()) {
				visitedUser.setFollowed(true);
			}
			session.setAttribute("visitedUser", visitedUser);
			session.setAttribute("visitedUserPosts", userDao.getPostsByUserID(visitedUser.getId()));
			return "index";
		} catch (SQLException e) {
			System.out.println("SQL Bug: " + e.getMessage());
			return "error";
		}
	}
	
}
