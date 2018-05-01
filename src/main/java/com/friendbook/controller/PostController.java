package com.friendbook.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
			model.addAttribute("posts", feed);
			model.addAttribute("feed", true);
			return "index";
		} catch (SQLException e) {
			System.out.println("SQL Bug: " + e.getMessage());
			return "error";
		}
	}
	
	@RequestMapping(value="/order", method = RequestMethod.POST)
	public String order(HttpSession session, Model model, HttpServletRequest request) {
		String order = request.getParameter("order");
		try {
//			List<Post> posts = userDao.getPostsByUserID(user.getId());
			model.addAttribute("posts", orderBy(order, request, session, model));
			return "index";
		} catch (SQLException e) {
			System.out.println("SQL Bug: " + e.getMessage());
			return "error";
		}
	}
	
	private List<Post> orderBy(String order, HttpServletRequest request, HttpSession session, Model model) throws SQLException{
		List<Post> posts = null;
		if(!request.getParameter("visit").isEmpty()) {
			//if visiting user's profile get his posts and order and return
			User visited = userDao.getByID(Long.parseLong(request.getParameter("visitedUserId")));
			posts = userDao.getPostsByUserID(visited.getId());
			model.addAttribute("visit", true);
			model.addAttribute("visitedUser", visited);
		} 
		else if(!request.getParameter("feed").isEmpty()) {
			//if on feed order feed posts and return
			posts = userDao.getUserFeedById(((User)session.getAttribute("user")).getId());
			model.addAttribute("feed", true);
		} else {
			posts = userDao.getPostsByUserID(((User)session.getAttribute("user")).getId());
		}
		if(order.equals("likes")) {
			Collections.sort(posts, (p1, p2) -> (p2.getLikes()-p1.getLikes()));
		}
		return posts;
	}
	
}
