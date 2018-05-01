package com.friendbook.model.post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.friendbook.exceptions.WrongCredentialsException;
import com.friendbook.model.comment.CommentDao;
import com.friendbook.model.user.DBManager;
import com.friendbook.model.user.User;
import com.friendbook.model.user.UserDao;

@Component
public class PostDao implements IPostDao {
	
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommentDao commentDao;

	private Connection connection;

	private PostDao() {
		this.connection = DBManager.getInstance().getConnection();
	}
	
	@Override
	public void addPost(Post post) throws SQLException {
		String imagePath = post.getImagePath();
		String query = "INSERT INTO posts(image_video_path, description, user_id) VALUES(?,?,?)";

		try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, imagePath);
			ps.setString(2, post.getText());
			ps.setLong(3, post.getUser().getId());

			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			post.setId(rs.getLong(1));
		}
	}

	@Override
	public void deletePost(long postId) throws SQLException {
		try (PreparedStatement ps = connection.prepareStatement("DELETE FROM posts WHERE id = ?")) {
			ps.setLong(1, postId);
			ps.executeUpdate();
		}
	}

	@Override
	public int getLikesByID(long id) throws SQLException {
		int likes = 0;
		String query = "SELECT COUNT(user_id) AS likes FROM users_likes_posts WHERE post_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			likes = rs.getInt("likes");
		}
		return likes;
	}
	
	public String getPostImageById(long postId) throws SQLException {
		String query = "SELECT image_video_path FROM posts WHERE id = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, postId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getString("image_video_path");
		}
	}

	@Override
	public void getAllPostsOfGivenUser(User user) throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("SELECT id, image_video_path, description, date FROM posts WHERE user_id = ?")) {
			ps.setLong(1, user.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Post p = new Post(rs.getLong("id"), rs.getString("image_video_path"), rs.getString("desctription"),
						user);
			}
		}
	}
	
	@Override
	public List<Post> getPostsByUserID(long id) throws SQLException, WrongCredentialsException {
		ArrayList<Post> posts = new ArrayList<>();
		String query = "SELECT id, description, date, image_video_path FROM posts WHERE user_id = ? ORDER BY date DESC";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			User u = userDao.getByID(id);
			while (rs.next()) {
				Post p = new Post(rs.getInt("id"), rs.getString("image_video_path"), rs.getString("description"), u);
				p.setLikes(getLikesByID(p.getId()));
				p.setDate(rs.getTimestamp("date").toLocalDateTime());
				commentDao.getAndSetAllCommentsOfGivenPost(p);
				posts.add(p);
			}
		}
		System.out.println(posts);
		return posts;
	}

	@Override
	public void increasePostLikes(long userId, long postId) throws SQLException {
		String query = "INSERT INTO users_likes_posts() VALUES(?,?)";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, postId);
			ps.setLong(2, userId);
			ps.executeUpdate();
		}
	}

	@Override
	public void decreasePostLikes(long userId, long postId) throws SQLException {
		String query = "DELETE FROM users_likes_posts WHERE user_id = ? AND post_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, userId);
			ps.setLong(2, postId);
			ps.executeUpdate();
		}
	}
}
