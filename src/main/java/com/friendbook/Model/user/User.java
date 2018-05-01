package com.friendbook.model.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.friendbook.exceptions.WrongCredentialsException;
import com.friendbook.model.post.Post;

public class User {

	@Autowired
	private UserDao userDao;
	
	private static final String PASS_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
	private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
	private static final int MIN_NAME_LENGTH = 6;
	private static final int MAX_NAME_LENGTH = 20;
	
	private long id;
	private String username;
	private String password; //bcrypt
	private String email;
	private String firstName;
	private String lastName;
	private int followers;
	private boolean isFollowed; //using it to show/hide follow button, will remove later if i find better way to do it.
	
	//TODO change later
	public User() {
	}
	
	public User(String username, String password, String email, String firstName, String lastName) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public User(int id, String username, String password, String email, String firstName, String lastName) {
		this(username, password, email, firstName, lastName);
		this.id = id;
	}
	
	
	//getters
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public int getFollowers() {
		return followers;
	}

	public long getId() {
		return id;
	}
	
	public boolean isFollowed() {
		return isFollowed;
	}
	
	//setters
	public void setId(long id) {
		if(id > 0){
			this.id = id;
		}
	}
	
	public void setUsername(String username) throws SQLException, WrongCredentialsException {
		if(userNameCheck(username)) {
			this.username = username;
		}
	}

	public void setPassword(String password) throws WrongCredentialsException {
		if(passwordCheck(password)) {
			this.password = password;
		}
	}

	public void setEmail(String email) throws WrongCredentialsException {
		if(emailCheck(email)) {
			this.email = email;
		}
	}

	public void setFirstName(String firstName) {
		if(firstName != null && firstName.trim().length() > 1) {
			this.firstName = firstName;
		}
	}

	public void setLastName(String lastName) {
		if(lastName != null && lastName.trim().length() > 1) {
			this.lastName = lastName;
		}
	}
	
	public void setFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}
	
	//validations
	private boolean passwordCheck(String password) throws WrongCredentialsException {
		if(password.matches(User.PASS_REGEX)) {
			return true;
		}
		throw new WrongCredentialsException("Invalid password");
	}
	
	private boolean emailCheck(String email) throws WrongCredentialsException {
		if(email.matches(User.EMAIL_REGEX)) {
			return true;
		}
		throw new WrongCredentialsException("Invalid email");
	}
	
	private boolean userNameCheck(String name) throws WrongCredentialsException, SQLException {
		if(name != null && name.length() >= User.MIN_NAME_LENGTH && name.length() <= User.MAX_NAME_LENGTH){
			userDao.existingUserNameCheck(name);
			return true;
		}
		throw new WrongCredentialsException("Incorrect username");
	}
	
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
}
