package com.friendbook.model.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.friendbook.exceptions.WrongCredentialsException;
import com.friendbook.model.comment.CommentDao;
import com.friendbook.model.post.Post;
import com.friendbook.model.post.PostDao;

@Component
public class UserDao implements IUserDao {

	@Autowired
	private PostDao postDao;
	@Autowired
	private CommentDao commentDao;

	private Connection connection;

	public UserDao() {
		connection = DBManager.getInstance().getConnection();
	}

	@Override
	public User getUserByNames(String name) throws SQLException, WrongCredentialsException {
		String query = "SELECT id, username, password, email, first_name, last_name FROM users WHERE CONCAT(first_name,' ', last_name) = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			rs.next();
			User u = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
					rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"));
			ps.close();
			return u;
		}
	}

	@Override
	public User getByID(long id) throws SQLException, WrongCredentialsException {
		User u = null;
		String query = "SELECT id, username, password, email, first_name, last_name FROM users WHERE id = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				u = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("email"),
						rs.getString("first_name"), rs.getString("last_name"));
				ps.close();
				return u;
			}
		}
		return u;
	}

	@Override
	public void saveUser(User u) throws SQLException {
		try (PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO users(username, password, email, first_name, last_name) VALUES(?, ?, ?, ?, ?)")) {
			ps.setString(1, u.getUsername());
			String hashedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());
			ps.setString(2, hashedPassword);
			ps.setString(3, u.getEmail());
			ps.setString(4, u.getFirstName());
			ps.setString(5, u.getLastName());
			ps.executeUpdate();
		}
	}

	@Override
	public void followUser(long followerId, long followedId) throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("INSERT INTO users_has_users (user_id_followed, user_id_follower) VALUES (?,?)")) {
			ps.setLong(1, followedId);
			ps.setLong(2, followerId);
			ps.executeUpdate();
		}
	}

	@Override
	public void unfollowUser(long followerId, long followedId) throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("DELETE FROM users_has_users WHERE user_id_followed = ? AND user_id_follower = ?")) {
			ps.setLong(1, followedId);
			ps.setLong(2, followerId);
			ps.executeUpdate();
		}
	}

	@Override
	public void loginCheck(String username, String password) throws WrongCredentialsException, SQLException {
		try (PreparedStatement ps = connection.prepareStatement("SELECT password FROM users WHERE username = ?")) {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (!rs.next() || !BCrypt.checkpw(password, rs.getString("password"))) {
				throw new WrongCredentialsException("Wrong credentials");
			}
		}
	}

	@Override
	public void existingUserCheck(String username, String email) throws SQLException, WrongCredentialsException {
		try (PreparedStatement ps = connection
				.prepareStatement("SELECT username, email FROM users WHERE username = ? AND email = ?")) {
			ps.setString(1, username);
			ps.setString(2, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				throw new WrongCredentialsException("Existing user");
			}
		}
	}

	@Override
	public void existingUserNameCheck(String username) throws SQLException, WrongCredentialsException {
		try (PreparedStatement ps = connection.prepareStatement("SELECT username FROM users WHERE username = ?")) {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				throw new WrongCredentialsException("Existing username");
			}
		}
	}

	@Override
	public boolean isPostLiked(long userId, long postId) throws SQLException {
		String query = "SELECT * FROM users_likes_posts WHERE user_id = ? AND post_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, userId);
			ps.setLong(2, postId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ps.close();
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getUsersNamesStartingWith(String term) throws SQLException {
		List<String> names = new ArrayList<>();
		String query = "SELECT concat(first_name,' ', last_name) AS name FROM users WHERE Concat(first_name, ' ', last_name) LIKE ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, term + "%");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				names.add(rs.getString("name"));
			}
		}
		return names;
	}

	@Override
	public User getUserByUsername(String username) throws SQLException, WrongCredentialsException {
		String query = "SELECT id, username, password, email, first_name, last_name FROM users WHERE username = ?";
		try(PreparedStatement ps = connection.prepareStatement(query)){
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			rs.next();
			User u = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
					rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"));
			return u;
		}
	}

	private boolean userNameCheck(String name) throws WrongCredentialsException, SQLException {
		if (name != null && name.length() >= User.MIN_NAME_LENGTH && name.length() <= User.MAX_NAME_LENGTH) {
			existingUserNameCheck(name);
			return true;
		}
		throw new WrongCredentialsException("Incorrect username");
	}

	@Override
	public ArrayList<Post> getUserFeedById(long userId) throws SQLException, WrongCredentialsException {
		ArrayList<Post> feed = new ArrayList<>();
		String query = "SELECT id, image_video_path, description, date, user_id FROM posts WHERE user_id IN (SELECT user_id_followed FROM users_has_users WHERE user_id_follower = ?) ORDER BY date DESC";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				User u = getByID(rs.getInt("user_id"));
				Post p = new Post(rs.getInt("id"), rs.getString("image_video_path"), rs.getString("description"), u);
				p.setDate(rs.getTimestamp("date").toLocalDateTime());
				// TODO get below code in constructor
				p.setLikes(postDao.getLikesByID(p.getId()));
				commentDao.getAndSetAllCommentsOfGivenPost(p);
				feed.add(p);
			}
		}
		return feed;
	}

	@Override
	public boolean isFollower(long followerId, long userId) throws SQLException {
		String query = "SELECT * FROM users_has_users WHERE user_id_follower = ? AND user_id_followed = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, followerId);
			ps.setLong(2, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ps.close();
				return true;
			}
		}
		return false;
	}

	public Post getLastPostByUserId(long id) throws SQLException, WrongCredentialsException {
		String query = "SELECT id, image_video_path, description, date, user_id FROM posts WHERE user_id = ? ORDER BY date DESC LIMIT 1 ";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			User u = getByID(rs.getInt("user_id"));
			Post p = new Post(rs.getInt("id"), rs.getString("image_video_path"), rs.getString("description"), u);
			p.setDate(rs.getTimestamp("date").toLocalDateTime());
			p.setLikes(postDao.getLikesByID(p.getId()));
			commentDao.getAndSetAllCommentsOfGivenPost(p);
			return p;
		}
	}
}
