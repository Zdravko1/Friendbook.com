package com.friendbook.Model.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.friendbook.Model.post.Post;
import com.friendbook.exceptions.ExistingUserException;
import com.friendbook.exceptions.ExistingUserNameException;
import com.friendbook.exceptions.WrongCredentialsException;

public interface IUserDao {

	User getUserByNames(String name) throws SQLException;
	User getByID(long id) throws SQLException;
	void saveUser(User u) throws SQLException;
	void existingUserNameCheck(String username) throws ExistingUserNameException, SQLException;
	void existingUserCheck(String username, String email) throws ExistingUserException, SQLException;
	void loginCheck(String username, String password) throws WrongCredentialsException, SQLException;
	//TODO Delete, ima go i v postdao, i mai tam e po dobre
	List<Post> getPostsByUserID(long id) throws SQLException;
	void followUser(User user, long followedId) throws SQLException;
	ArrayList<Post> getUserFeedByID(long id) throws SQLException;
	User getUserByUsername(String username) throws SQLException;
	List<String> getUsersNamesStartingWith(String term) throws SQLException;
	boolean isPostLiked(User u, int id) throws SQLException;
	boolean isFollower(User follower, long userId) throws SQLException;
	
	void unfollowUser(User user, long followedId) throws SQLException;
}
