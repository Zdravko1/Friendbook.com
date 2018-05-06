package com.friendbook.model.user;

import java.sql.SQLException;
import com.friendbook.exceptions.WrongCredentialsException;

public class User {
	
	private static final String PASS_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
	private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
	static final int MIN_NAME_LENGTH = 6;
	static final int MAX_NAME_LENGTH = 20;
	
	private long id;
	private String username;
	private String password; //bcrypt
	private String email;
	private String firstName;
	private String lastName;
	private int followers;
	private boolean isFollowed; //using it to show/hide follow button, will remove later if i find better way to do it.

	public User(String username, String password, String email, String firstName, String lastName) throws SQLException, WrongCredentialsException {
		this.setUsername(username);
		this.setPassword(password);
		this.setEmail(email);
		this.setFirstName(firstName);
		this.setLastName(lastName);
	}
	
	public User(long id, String username, String password, String email, String firstName, String lastName) throws SQLException, WrongCredentialsException {
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
	
	public void setUsername(String username) throws WrongCredentialsException {
		if(username != null && username.trim().length() > MIN_NAME_LENGTH && username.trim().length() < MAX_NAME_LENGTH) {
			this.username = username;
			return;
		}
		throw new WrongCredentialsException("Invalid username.");
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
	
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
}
