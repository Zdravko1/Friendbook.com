package com.friendbook.model.comment;

import java.sql.SQLException;

import com.friendbook.model.post.Post;

public interface ICommentDao {

	void addComment(Comment comment) throws SQLException;

	void getAndSetAllCommentsOfGivenPost(Post post) throws SQLException;

	void getCommentsOfParentComment(Comment comment) throws SQLException;

	int getLikesByID(long id) throws Exception;

	void removeLike(long userId, long commentId) throws SQLException;

	boolean checkIfAlreadyLiked(long userId, long commentId) throws SQLException;

	void likeComment(long userId, long commentId) throws SQLException;

}
