package com.friendbook.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.friendbook.exceptions.WrongCredentialsException;
import com.friendbook.model.post.Post;
import com.friendbook.model.post.PostDao;
import com.friendbook.model.user.User;
import com.friendbook.model.user.UserDao;
import com.google.gson.Gson;

@Controller
public class PostController {

	@Autowired
	private UserDao userDao;
	@Autowired
	private PostDao postDao;
	
	
	
	@RequestMapping(value="/post", method = RequestMethod.POST)
	@ResponseBody
	public String post(HttpServletRequest request) throws Exception {
		User user = (User)request.getSession().getAttribute("user");
		Post post = null;
		String path = null;
		try {
			String image = request.getParameter("file");
			if(image != null) {
				image = image.split(",")[1].replaceAll(" ", "+");
				System.out.println(image);
				String imageName = "image"+image.substring(0, 10)+".jpg";
				File file = new File("D:\\photos\\" + user.getUsername());
				if(!file.exists()) {
					file.mkdirs();
				}
				file = new File("D:\\photos\\"+user.getUsername()+"\\"+imageName);
				file.createNewFile();
				
				decoder(image, file);
				
				path = file.getAbsolutePath();
			}
			post = new Post(user, (String)request.getParameter("text"), path);

			postDao.addPost(post);
			System.out.println("Added post to database.");
			return new Gson().toJson(userDao.getLastPostByUserId(user.getId()));
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping(value="/reloadPosts", method = RequestMethod.GET)
	public String reloadPosts(HttpSession session, Model model) throws SQLException, WrongCredentialsException {
		try {
			User user = (User) session.getAttribute("user");
			List<Post> posts = postDao.getPostsByUserID(user.getId());
			model.addAttribute("posts", posts);
			return "index";
		} catch (SQLException | WrongCredentialsException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@RequestMapping(value="/likePost", method = RequestMethod.POST)
	@ResponseBody
	public Integer likePost(HttpSession session, HttpServletRequest request) throws Exception {
		int likeId = Integer.parseInt(request.getParameter("like"));
		long userId = ((User) session.getAttribute("user")).getId();
		//check if this post was liked by the user before
		//remove like if so or add a like
		try {
			if(userDao.isPostLiked(userId, likeId)) {
				postDao.decreasePostLikes(userId, likeId);
			} else {
				postDao.increasePostLikes(userId, likeId);
			}
			request.setAttribute("posts", postDao.getPostsByUserID(userId));
			return postDao.getLikesByID(likeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
	}

	@RequestMapping(value="/reloadFeed", method = RequestMethod.GET)
	public String reloadFeed(HttpSession session, Model model) throws Exception {
		try {
			User u = (User) session.getAttribute("user");
			ArrayList<Post> feed = userDao.getUserFeedById(u.getId());
			model.addAttribute("posts", feed);
			model.addAttribute("feed", true);
			return "index";
		} catch (SQLException | WrongCredentialsException e ) {
			e.printStackTrace();
			throw e;
		} 
	}
	
	@RequestMapping(value="/order", method = RequestMethod.POST)
	public String order(HttpSession session, Model model, HttpServletRequest request) throws Exception {
		String order = request.getParameter("order");
		try {
			model.addAttribute("posts", orderBy(order, request, session, model));
			return "index";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private List<Post> orderBy(String order, HttpServletRequest request, HttpSession session, Model model) throws Exception {
		List<Post> posts = null;
		if(!request.getParameter("visit").isEmpty()) {
			//if visiting user's profile get his posts and order and return
			User visited = userDao.getByID(Long.parseLong(request.getParameter("visitedUserId")));
			posts = postDao.getPostsByUserID(visited.getId());
			model.addAttribute("visit", true);
			model.addAttribute("visitedUser", visited);
		} 
		else if(!request.getParameter("feed").isEmpty()) {
			//if on feed order feed posts and return
			posts = userDao.getUserFeedById(((User)session.getAttribute("user")).getId());
			model.addAttribute("feed", true);
		} else {
			posts = postDao.getPostsByUserID(((User)session.getAttribute("user")).getId());
		}
		if(order.equals("likes")) {
			Collections.sort(posts, (p1, p2) -> (p2.getLikes()-p1.getLikes()));
		}
		return posts;
	}
	
	private static void decoder(String base64Image, File f) throws IOException {
		try (FileOutputStream imageOutFile = new FileOutputStream(f)) {
			byte[] btDataFile = new sun.misc.BASE64Decoder().decodeBuffer(base64Image);
			imageOutFile.write(btDataFile);
			imageOutFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
