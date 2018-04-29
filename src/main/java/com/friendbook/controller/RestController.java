package com.friendbook.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.friendbook.model.post.Post;
import com.friendbook.model.post.PostDao;
import com.friendbook.model.user.User;
import com.friendbook.model.user.UserDao;
import com.google.gson.Gson;


@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	//TODO global exception handler
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private PostDao postDao;

	@RequestMapping(value="/searchAutoComplete", method = RequestMethod.GET)
	@ResponseBody
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
	
	@RequestMapping(value="/follow", method = RequestMethod.POST)
	public String followUser(HttpSession session, HttpServletRequest request) {
		User user = (User) session.getAttribute("user");
		long followedId = Long.parseLong(request.getParameter("followedId"));
		try {
			//check if the current user is following the one who is visited by him
			if(!userDao.isFollower(user, followedId)) {
				//if not then follow him and switch the button to "Followed"
				userDao.followUser(user, followedId);
				return new Gson().toJson("Followed");
			}
			//else unfollow him and switch the button name
			userDao.unfollowUser(user, followedId);
			return new Gson().toJson("Follow");
		} catch (Exception e) {
			System.out.println("Bug: " + e.getMessage());
			return "error";
		}
	}
	
	@RequestMapping(value="/likePost", method = RequestMethod.POST)
	@ResponseBody
	public Integer likePost(HttpSession session, HttpServletRequest request) {
		int id = Integer.parseInt(request.getParameter("like"));
		System.out.println(id);
		User u = (User) session.getAttribute("user");
		//check if this post was liked by the user before
		//remove like if so or add a like
		try {
			if(userDao.isPostLiked(u, id)) {
				postDao.decreasePostLikes(u, id);
			} else {
				postDao.increasePostLikes(u, id);
			}
			request.setAttribute("posts", userDao.getPostsByUserID(u.getId()));
			return postDao.getLikesByID(id);
		} catch (SQLException e) {
			System.out.println("SQL Bug: " + e.getMessage());
			return null;
		}
	}
	
	@RequestMapping(value="/post", method = RequestMethod.POST)
	@ResponseBody
	public String post(HttpServletRequest request) {
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
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch(Exception e) {
			System.out.println("Bug2: " );
			e.printStackTrace();
			return null;
		}
	}
	
	private static void decoder(String base64Image, File f) {
		try (FileOutputStream imageOutFile = new FileOutputStream(f)) {
			byte[] btDataFile = new sun.misc.BASE64Decoder().decodeBuffer(base64Image);
			imageOutFile.write(btDataFile);
			imageOutFile.flush();
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
	}
}
