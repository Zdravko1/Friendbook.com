package com.friendbook.model.dto;


public class SearchUserDTO {

	private String name;
	private long id;
	private boolean isFollowed;
	
	public SearchUserDTO(long id, String name) {
		this.name = name;
		this.id = id;
	}
	
	public void setFollowed(boolean followed) {
		this.isFollowed = followed;
	}
	
	public boolean isFollowed() {
		return isFollowed;
	}
	
	public String getName() {
		return name;
	}
	public long getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
