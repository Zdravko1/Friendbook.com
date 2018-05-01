package com.friendbook.model.post;

import java.sql.SQLException;
import java.util.List;

import com.friendbook.exceptions.WrongCredentialsException;
import com.friendbook.model.user.User;


public interface IPostDao {

	void deletePost(long postId) throws SQLException;
	
	int getLikesByID(long id) throws SQLException;
	
	void increasePostLikes(long userId, long postId) throws SQLException;
	
	void decreasePostLikes(long userId, long postId) throws SQLException;
	
	void getAllPostsOfGivenUser(User user) throws SQLException;

	void addPost(Post post) throws SQLException;
	
	String getPostImageById(long postId) throws SQLException;
	
	List<Post> getPostsByUserID(long id) throws SQLException, WrongCredentialsException;
}
