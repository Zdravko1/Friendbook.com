package com.friendbook.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.friendbook.model.post.Post;
import com.friendbook.model.post.PostDao;
import com.friendbook.model.user.User;
import com.friendbook.model.user.UserDao;

@Controller
public class PostController {

	@Autowired
	private PostDao postDao;
	@Autowired
	private UserDao userDao;
	
	@RequestMapping(value="/reloadPosts", method = RequestMethod.GET)
	public String reloadPosts(HttpSession session, Model model) {
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

	@RequestMapping(value="/reloadFeed", method = RequestMethod.GET)
	public String reloadFeed(HttpSession session, Model model) {
		try {
			User u = (User) session.getAttribute("user");
			ArrayList<Post> feed = userDao.getUserFeedById(u.getId());
			model.addAttribute("feed", feed);
			return "index";
		} catch (SQLException e) {
			System.out.println("SQL Bug: " + e.getMessage());
			return "error";
		}
	}
}
