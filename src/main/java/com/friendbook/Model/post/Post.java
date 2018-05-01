package com.friendbook.model.post;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.friendbook.model.comment.Comment;
import com.friendbook.model.user.User;

public class Post implements Serializable{

	private long id;
	private String text;
	private User user;
	private String imagePath;
	private int likes;
	private List<Comment> comments = new ArrayList<>();
	private LocalDateTime date;
		
	public Post(User user, String text, String imagePath) {
		this.user = user;
		setText(text);
		this.imagePath = imagePath;
	}

	public Post(long id, String imagePath, String text, User user) {
		this.id = id;
		this.imagePath = imagePath;
		this.text = text;
		this.user = user;
	}

	public Post(int id, String imagePath, String desctription, User u, LocalDateTime localDateTime) {
		this(id, imagePath, desctription, u);
		setDate(localDateTime);
	}

	public LocalDateTime getDate() {
		return date;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setText(String text) {
		if (text == null || text.trim().isEmpty()) {
			throw new IllegalArgumentException("Invalid text");
		}
		this.text = text;
	}
	
	public void addComment(Comment comment) {
		this.comments.add(comment);
	}
	
	public User getUser() {
		return user;
	}
	
	public long getId() {
		return id;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public int getLikes() {
		return likes;
	}
	
	public List<Comment> getComments() {
		return Collections.unmodifiableList(this.comments);
	}
	
	public void setLikes(int likes) {
		if(likes > 0) {
			this.likes = likes;
		}
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return id + " " + text;
	}
}
