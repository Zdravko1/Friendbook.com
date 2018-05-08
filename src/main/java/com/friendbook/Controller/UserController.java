package com.friendbook.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
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
import com.friendbook.model.dto.SearchUserDTO;
import com.friendbook.model.post.Post;
import com.friendbook.model.post.PostDao;
import com.friendbook.model.user.BCrypt;
import com.friendbook.model.user.User;
import com.friendbook.model.user.UserDao;
import com.google.gson.Gson;

@Controller
public class UserController {

	private String mailUsername = "ittalentsfriendbook@gmail.com";
	private String mailPassword = "ittalentsfriendbook123";
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private PostDao postDao;
	
	@RequestMapping(value = "/passwordRecovery", method = RequestMethod.GET)
	public String passwordRecovery() {
		return "passwordRecovery";
	}
	
	@RequestMapping(value = "/passwordRecovery", method = RequestMethod.POST)
	public String passwordRecovery(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String receiverEmail = req.getParameter("email");
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailUsername,mailPassword);
				}
			});

		try {
			String username = userDao.getUsernameByEmail(receiverEmail);
			String rndPassword = generateRandomPassword();
			
			userDao.setNewPasswordByUserEmail(receiverEmail, rndPassword);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailUsername));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(receiverEmail));
			message.setSubject("Testing Subject");
			message.setText("Dear "+ username +"," +
					"Your new password has been set to " + rndPassword);
			System.out.println(rndPassword);
			Transport.send(message);
			return "login";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private String generateRandomPassword() {
		String upperLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerLetters = upperLetters.toLowerCase();
		String numbers = "0123456789";
		String specialSymbols = "@#$%^&+=";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			sb.append(upperLetters.charAt(rnd.nextInt(upperLetters.length())));
			sb.append(lowerLetters.charAt(rnd.nextInt(lowerLetters.length())));
			sb.append(numbers.charAt(rnd.nextInt(numbers.length())));
			sb.append(specialSymbols.charAt(rnd.nextInt(specialSymbols.length())));
		}
		return sb.toString();
	}

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
	public String register(@RequestParam("username") String username,
						   @RequestParam("password") String password,
					   	   @RequestParam("confirm_password") String confirmPassword,
						   @RequestParam("email") String email,
						   @RequestParam("first_name") String firstName,
						   @RequestParam("last_name") String lastName) throws Exception {

		if (password.compareTo(confirmPassword) != 0) {
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

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchById(Model model, HttpSession session, @RequestParam("userId") Long userId) throws Exception {
		User user = (User) session.getAttribute("user");
		try {
			User visitedUser = userDao.getByID(userId);
			//checks if there is such user
			if(visitedUser == null || user.getId() == visitedUser.getId()) {
				model.addAttribute("posts", postDao.getPostsByUserID(user.getId()));
				return "index";
			}
			//checks if the visited user is being followed or not
			if (userDao.isFollower(user.getId(), visitedUser.getId())) {
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
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(HttpSession session, HttpServletRequest request, Model model) throws Exception {
		User user = (User) session.getAttribute("user");
		String searchTerm = request.getParameter("user");
		try {
			List<SearchUserDTO> visitedUsers = userDao.getUsersByName(searchTerm);
			//if there are more than one user with the same name
			//go to a page with their profiles
			if(visitedUsers.size() > 1) {
				for (SearchUserDTO searchUserDTO : visitedUsers) {
					if (userDao.isFollower(user.getId(), searchUserDTO.getId())) {
						searchUserDTO.setFollowed(true);
					}
				}
				model.addAttribute("users", visitedUsers);
				return "search";
			}
			// if the searched user is followed already put a flag and change the follow
			// button to followed in jsp
			// or if this is the same user
			if(visitedUsers.isEmpty() || user.getId() == visitedUsers.get(0).getId()) {
				model.addAttribute("posts", postDao.getPostsByUserID(user.getId()));
				return "index";
			}
			if (userDao.isFollower(user.getId(), visitedUsers.get(0).getId())) {
				visitedUsers.get(0).setFollowed(true);
			}
			model.addAttribute("visit", true);
			model.addAttribute("visitedUser", visitedUsers.get(0));
			model.addAttribute("posts", postDao.getPostsByUserID(visitedUsers.get(0).getId()));
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
			e.printStackTrace();
			return "error";
		}
	}
	
	@RequestMapping(value = "/searchAutoComplete", method = RequestMethod.GET)
	@ResponseBody
	public String searchAutoComplete(HttpServletResponse response, HttpServletRequest request) throws Exception {
		response.setContentType("application/json");
		try {
			String term = request.getParameter("term");

			List<String> list = userDao.getUsersNamesStartingWith(term);

			return new Gson().toJson(list);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String getEditProfilePage() {
		return "settings";
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String editProfile(@RequestParam("username") String username,
							  @RequestParam("password") String password,
						   	  @RequestParam("confirm_password") String confirmPassword,
							  @RequestParam("email") String email,
							  @RequestParam("first_name") String firstName,
							  @RequestParam("last_name") String lastName,
							  HttpSession session,
							  Model model) throws SQLException {
		//if passwords don't match return same page with error message
		if(!password.equals(confirmPassword)) {
			model.addAttribute("message", "Passwords don't match.");
			return "settings";
		}
		//get user from session to check if username/email is changed or not
		User user = (User) session.getAttribute("user");
		//check if the password is correct
		try {
			//get helping user object to edit the data
			User editUser = new User(user.getId(), username, password, email, firstName, lastName);
		
			if(!username.equals(user.getUsername())) {
				userDao.existingUserNameCheck(username);
			}
			if(!email.equals(user.getEmail())) {
				userDao.existingEmailCheck(email);
			}
			userDao.editProfile(editUser);
			session.invalidate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (WrongCredentialsException e) {
			model.addAttribute("message", e.getMessage());
			return "settings";
		}
		return "login";
	}
}
