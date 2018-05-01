package com.friendbook.model.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.friendbook.exceptions.WrongCredentialsException;
import com.friendbook.model.post.Post;

public interface IUserDao {

	User getUserByNames(String name) throws SQLException, WrongCredentialsException;
	User getByID(long id) throws SQLException, WrongCredentialsException;
	void saveUser(User u) throws SQLException;
	void existingUserNameCheck(String username) throws WrongCredentialsException, SQLException;
	void existingUserCheck(String username, String email) throws WrongCredentialsException, SQLException;
	void loginCheck(String username, String password) throws WrongCredentialsException, SQLException;
	//TODO Delete, ima go i v postdao, i mai tam e po dobre
	void followUser(long followerId, long followedId) throws SQLException;
	ArrayList<Post> getUserFeedById(long id) throws SQLException, WrongCredentialsException;
	User getUserByUsername(String username) throws SQLException, WrongCredentialsException;
	List<String> getUsersNamesStartingWith(String term) throws SQLException;
	boolean isPostLiked(long userId, long postId) throws SQLException;
	boolean isFollower(long followerId, long userId) throws SQLException;
	
	void unfollowUser(long followerId, long followedId) throws SQLException;
}
