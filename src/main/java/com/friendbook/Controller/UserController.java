package com.friendbook.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
		User user = (User) session.getAttribute("user");
		if(session.isNew() || user == null) {
			return "login";
		}
		try {	
			List<Post> posts = userDao.getPostsByUserID(user.getId());
			model.addAttribute("posts", posts);
			return "index";
		} catch (SQLException e) {
			System.out.println("SQL bug: " + e.getMessage());
			return "error";
		}
	}
	
	@RequestMapping(value="/login", method = RequestMethod.POST)
	public String login(@RequestParam String username,
			 			@RequestParam String password,
			 			HttpSession session, Model model) {
		try {
			//get username and password from jsp
			//try to login if not exception will fire
			userDao.loginCheck(username, password);
			//if successfull, get user
			User user = userDao.getUserByUsername(username);
			//get user's posts and put them in request
			List<Post> posts = userDao.getPostsByUserID(user.getId());
			session.setAttribute("user", user);
			model.addAttribute("posts", posts);
			
			return "index";
		} catch (WrongCredentialsException e) {
			System.out.println("Exception: "+ e.getMessage());
			return "error";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Some error occured: " + e.getMessage());
			return "error";
		}
	}
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public String register(HttpServletRequest request) throws WrongCredentialsException {
		String username = request.getParameter("username");
		System.out.println(username);
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String password2 = request.getParameter("confirm password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		
		if(password.compareTo(password2) !=0 ) {
			System.out.println("Passwords don't match");
			throw new WrongCredentialsException("Passwords don't match.");
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
		} catch (WrongCredentialsException e) {
			request.setAttribute("error", e.getMessage());
			return "error";
		} catch (SQLException e) {
			System.out.println("Bug: " + e.getMessage());
			return "error";
		}
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	@ResponseBody
	public String logout(HttpSession session) {
		session.invalidate();
		return "logout";
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
	public String search(HttpSession session, HttpServletRequest request, Model model) {
		User user = (User) session.getAttribute("user");
		String searchUser = request.getParameter("user");
		try {
			User visitedUser = userDao.getUserByNames(searchUser);
			//if the searched user is followed already put a flag and change the follow button to followed in jsp
			//or if this is the same user
			if(userDao.isFollower(user.getId(), visitedUser.getId()) || user.getId() == visitedUser.getId()) {
				visitedUser.setFollowed(true);
			}
			model.addAttribute("visit", true);
			model.addAttribute("visitedUser", visitedUser);
			model.addAttribute("posts", userDao.getPostsByUserID(visitedUser.getId()));
			return "index";
		} catch (SQLException e) {
			System.out.println("SQL Bug: " + e.getMessage());
			return "error";
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "login";
	}
	
}
