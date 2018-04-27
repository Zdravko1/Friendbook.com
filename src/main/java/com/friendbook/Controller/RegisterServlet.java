package com.friendbook.Controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.friendbook.Model.user.User;
import com.friendbook.exceptions.ExistingUserException;
import com.friendbook.exceptions.ExistingUserNameException;
import com.friendbook.exceptions.IncorrectUserNameException;
import com.friendbook.exceptions.InvalidEmailException;
import com.friendbook.exceptions.InvalidPasswordException;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//bugvo e
//		Session.validateRequestIp(request, response);
		
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String password2 = request.getParameter("confirm password");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		
		if(password.compareTo(password2) !=0 ) {
			response.getWriter().write("Passwords don't match! ");
		}
		
		try {
			User u = new User();
			u.setUsername(username);
			u.setPassword(password);
			u.setEmail(email);
			u.setFirstName(firstName);
			u.setLastName(lastName);
			UserManager.getInstance().register(u);
			request.getRequestDispatcher("login.jsp").forward(request, response);
		} catch (InvalidPasswordException | IncorrectUserNameException | ExistingUserNameException
				| InvalidEmailException | ExistingUserException e) {
			request.setAttribute("error", e.getMessage());
			response.getWriter().write(e.getMessage());
		} catch (SQLException e) {
			System.out.println("Bug: " + e.getMessage());
		}
	}

}
