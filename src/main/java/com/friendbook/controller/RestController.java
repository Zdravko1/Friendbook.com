package com.friendbook.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.friendbook.model.post.PostDao;
import com.friendbook.model.user.User;
import com.friendbook.model.user.UserDao;


@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	//TODO global exception handler
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private PostDao postDao;

	@RequestMapping(value="/searchAutoComplete", method = RequestMethod.GET)
	public List<String> searchAutoComplete(HttpServletResponse response, HttpServletRequest request) {
		  response.setContentType("application/json");
          try {
              String term = request.getParameter("term");
              System.out.println("Data from ajax call " + term);

              List<String> list = userDao.getUsersNamesStartingWith(term);
              
              return list;
          } catch (Exception e) {
        	  System.out.println("Bug: " + e.getMessage());
  			  return null;
          }
	}
	
	@RequestMapping(value="/likePost", method = RequestMethod.POST)
	public Integer likePost(HttpSession session, HttpServletRequest request) {
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
			System.out.println("SQL Bug: " + e.getMessage());
			return null;
		} 
	}
}
