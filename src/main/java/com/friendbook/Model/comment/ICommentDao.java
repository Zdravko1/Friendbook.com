package com.friendbook.Model.comment;

import java.sql.SQLException;

import com.friendbook.Model.post.Post;

public interface ICommentDao {

	void addComment(long userId, Comment comment) throws SQLException;

	void deleteComment(long commentId) throws SQLException;

	void changeComment(Comment comment) throws SQLException;

	void getAndSetAllCommentsOfGivenPost(Post post) throws SQLException;

	void getCommentsOfParentComment(Comment comment) throws SQLException;

	int getLikesByID(long id) throws Exception;

	void removeLike(long userId, long commentId) throws SQLException;

	boolean checkIfAlreadyLiked(long userId, long commentId) throws SQLException;

	void likeComment(long userId, long commentId) throws SQLException;

}
