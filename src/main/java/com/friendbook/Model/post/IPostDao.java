package com.friendbook.model.post;

import java.sql.SQLException;

import com.friendbook.model.user.User;


public interface IPostDao {

	void deletePost(long postId) throws SQLException;
	
	int getLikesByID(long id) throws SQLException;
	
	void increasePostLikes(User u, long id) throws SQLException;
	
	void decreasePostLikes(User u, long id) throws SQLException;
	
	void getAllPostsOfGivenUser(User user) throws SQLException;

	void addPost(Post post) throws SQLException;
	
	String getPostImageById(long postId) throws SQLException;
}
