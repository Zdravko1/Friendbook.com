package com.friendbook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.friendbook.model.post.PostDao;
import com.friendbook.model.user.UserDao;

@Controller
public class PostController {

	@Autowired
	private PostDao postDao;
	@Autowired
	private UserDao userDao;

}
