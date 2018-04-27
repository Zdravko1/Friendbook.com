package com.friendbook.model.comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.springframework.stereotype.Component;

import com.friendbook.controller.UserManager;
import com.friendbook.model.post.Post;
import com.friendbook.model.user.DBManager;
import com.friendbook.model.user.User;
import com.friendbook.model.user.UserDao;

@Component
public class CommentDao implements ICommentDao {

	private Connection connection;
	private static CommentDao instance;

	private CommentDao() {
		connection = DBManager.getInstance().getConnection();
	}
	
	public static CommentDao getInstance() {
		if (instance == null) {
			synchronized (CommentDao.class) {
				if (instance == null) {
					instance = new CommentDao();
				}
			}
		}
		return instance;
	}

	@Override
	public void likeComment(long userId, long commentId) throws SQLException {
		// TODO check if working
		try (PreparedStatement ps = connection
				.prepareStatement("INSERT INTO users_likes_comments (user_id, comment_id) VALUES (?,?)")) {
			ps.setLong(1, userId);
			ps.setLong(2, commentId);
			ps.executeUpdate();
		}
	}

	@Override
	public void removeLike(long userId, long commentId) throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("DELETE FROM users_likes_comments WHERE user_id = ? AND comment_id = ?")) {
			ps.setLong(1, userId);
			ps.setLong(2, commentId);
			ps.executeUpdate();
		}
	}

	@Override
	public void getCommentsOfParentComment(Comment comment) throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("SELECT id, text, user_id, date FROM comments WHERE parent_id = ?")) {
			ps.setLong(1, comment.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Comment com = new Comment(rs.getLong("id"), rs.getLong("user_id"), comment.getPost(), comment.getId(),
						rs.getString("text"));
				com.setDate(rs.getTimestamp("date").toLocalDateTime());
				getCommentsOfParentComment(com);
				comment.addComment(com);
			}
		}
	}

	@Override
	public void addComment(Comment comment) throws SQLException {
		String query = "INSERT INTO comments (text, post_id, parent_id, user_id) VALUES (?,?,?,?)";

		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, comment.getText());
			statement.setLong(2, comment.getPost());
			statement.setLong(3, comment.getParentComment() == null ? Types.NULL : comment.getParentComment());
			statement.setLong(4, comment.getUserId());

			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			comment.setId(rs.getLong(1));
		}
		System.out.println("Added comment");
	}

	@Override
	public void getAndSetAllCommentsOfGivenPost(Post post) throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("SELECT id, text, date, parent_id, user_id FROM comments WHERE post_id = ?")) {
			ps.setLong(1, post.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getLong("parent_id") == 0) {
					Comment comment = new Comment(rs.getLong("id"), rs.getLong("user_id"), post.getId(),
							rs.getLong("parent_id"), rs.getString("text"));
					comment.setLikes(getLikesByID(comment.getId()));
					comment.setDate(rs.getTimestamp("date").toLocalDateTime());
					getCommentsOfParentComment(comment);
					post.addComment(comment);
				}
			}
		}
	}

	@Override
	public boolean checkIfAlreadyLiked(long userId, long commentId) throws SQLException {
		// TODO check if working
		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT user_id, comment_id FROM users_likes_comments WHERE user_id = ? AND comment_id = ?")) {
			ps.setLong(1, userId);
			ps.setLong(2, commentId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getLikesByID(long id) throws SQLException {
		int likes = 0;
		String query = "SELECT COUNT(user_id) AS likes FROM users_likes_comments WHERE comment_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			likes = rs.getInt("likes");
		}
		return likes;
	}

	public Comment getLastCommentByUserId(long id) throws SQLException {
		String query = "SELECT id, text, date, post_id, user_id FROM comments WHERE user_id = ? ORDER BY date DESC LIMIT 1 ";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			User u = UserManager.getInstance().getUser(rs.getInt("user_id"));
			Comment c = new Comment(rs.getLong("id"), rs.getLong("user_id"), rs.getInt("post_id"), null,
					rs.getString("text"), u);
			c.setDate(rs.getTimestamp("date").toLocalDateTime());
			return c;
		}
	}
}
