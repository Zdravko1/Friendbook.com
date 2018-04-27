package com.friendbook.Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.friendbook.Model.post.Post;
import com.friendbook.Model.user.User;
import com.friendbook.exceptions.WrongCredentialsException;

/**
 * Servlet implementation class LoginServlet
 */
@Controller
public class UserController {
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	protected String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
//		UserManager.getInstance().sessionCheck(request, response);
		
		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			if(UserManager.getInstance().login(username, password)) {
				User user = UserManager.getInstance().getUserByUsername(username);
				//get user's posts and put them in request
				List<Post> posts = UserManager.getInstance().getPostsByUserID(user.getId());
				session.setAttribute("user", user);
				request.setAttribute("posts", posts);
				return "index2";
			}
			else {
				return "login";
			}
		
		} catch (WrongCredentialsException e) {
			System.out.println("Exception: "+ e.getMessage() );
			
		} catch (Exception e) {
			System.out.println("Some error occured: " + e.getMessage());
			
		}
		return "login";
	}
	
	@RequestMapping(value ="/login", method = RequestMethod.GET)
	protected String goToLoginPage() throws ServletException, IOException {
		return "login";
	}

}
