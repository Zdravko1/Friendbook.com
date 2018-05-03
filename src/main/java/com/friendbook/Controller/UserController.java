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
import com.google.gson.Gson;

@Controller
public class UserController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PostDao postDao;

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String getRegisterPage() {
		return "register";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model)
			throws Exception {
		try {
			// get username and password from jsp
			// try to login if not exception will fire
			userDao.loginCheck(username, password);
			// if successfull, get user
			User user = userDao.getUserByUsername(username);
			// get user's posts and put them in request
			List<Post> posts = postDao.getPostsByUserID(user.getId());
			session.setAttribute("user", user);
			model.addAttribute("posts", posts);

			return "index";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(HttpServletRequest request) throws Exception {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String password2 = request.getParameter("confirm password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");

		if (password.compareTo(password2) != 0) {
			System.out.println("Passwords don't match");
			throw new WrongCredentialsException("Passwords don't match.");
		}

		try {
			User u = new User(username, password, email, firstName, lastName);
			userDao.saveUser(u);

			return "login";
		} catch (WrongCredentialsException | SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		session.invalidate();
		return "login";
	}

	@RequestMapping(value = "/getPic", method = RequestMethod.GET)
	public void getPic(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long postId = Long.parseLong(request.getParameter("postId"));
		try {
			String imagePath = postDao.getPostImageById(postId);
			if (imagePath != null) {
				File f = new File(imagePath);
				InputStream is = new FileInputStream(f);
				OutputStream os = response.getOutputStream();
				int b = is.read();
				while (b != -1) {
					os.write(b);
					b = is.read();
				}
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping(value = "/follow", method = RequestMethod.POST)
	@ResponseBody
	public String followUser(HttpSession session, HttpServletRequest request) throws Exception {
		User user = (User) session.getAttribute("user");
		long followedId = Long.parseLong(request.getParameter("followedId"));
		try {
			// check if the current user is following the one who is visited by him
			if (!userDao.isFollower(user.getId(), followedId)) {
				// if not then follow him and switch the button to "Followed"
				userDao.followUser(user.getId(), followedId);
				return new Gson().toJson("Followed");
			}
			// else unfollow him and switch the button name
			userDao.unfollowUser(user.getId(), followedId);
			return new Gson().toJson("Follow");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(HttpSession session, HttpServletRequest request, Model model) throws Exception {
		User user = (User) session.getAttribute("user");
		String searchUser = request.getParameter("user");
		try {
			User visitedUser = userDao.getUserByNames(searchUser);
			// if the searched user is followed already put a flag and change the follow
			// button to followed in jsp
			// or if this is the same user
			if (userDao.isFollower(user.getId(), visitedUser.getId()) || user.getId() == visitedUser.getId()) {
				visitedUser.setFollowed(true);
			}
			model.addAttribute("visit", true);
			model.addAttribute("visitedUser", visitedUser);
			model.addAttribute("posts", postDao.getPostsByUserID(visitedUser.getId()));
			return "index";
		} catch (SQLException | WrongCredentialsException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@RequestMapping("*")
	public String index(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (session.isNew() || user == null) {
			return "login";
		}
		try {
			List<Post> posts = postDao.getPostsByUserID(user.getId());
			model.addAttribute("posts", posts);
			return "index";
		} catch (SQLException | WrongCredentialsException e) {
			System.out.println("Exception: " + e.getMessage());
			return "error";
		}
	}
	
	@RequestMapping(value = "/searchAutoComplete", method = RequestMethod.GET)
	@ResponseBody
	public String searchAutoComplete(HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("application/json");
		try {
			String term = request.getParameter("term");
			System.out.println("Data from ajax call " + term);

			List<String> list = userDao.getUsersNamesStartingWith(term);

			return new Gson().toJson(list);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
