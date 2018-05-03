package com.friendbook.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.friendbook.model.comment.Comment;
import com.friendbook.model.comment.CommentDao;
import com.friendbook.model.user.User;
import com.google.gson.Gson;

@Controller
public class CommentController {

	@Autowired
	private CommentDao commentDao;

	@RequestMapping(value = "/comment", method = RequestMethod.POST)
	@ResponseBody
	public String comment(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		long postId = Long.parseLong(request.getParameter("currentPost"));
		Long commentId = null;
		if (!request.getParameter("currentComment").equals("null")) {
			commentId = Long.parseLong(request.getParameter("currentComment"));
		}
		Comment comment = new Comment(user.getId(), postId, commentId, request.getParameter("text"));
		try {
			commentDao.addComment(comment);

			return new Gson().toJson(commentDao.getLastCommentByUserId(user.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping(value = "/likeComment", method = RequestMethod.POST)
	@ResponseBody
	public String likeComment(HttpServletRequest req, HttpServletResponse resp) throws SQLException {
		long commentId = Long.parseLong(req.getParameter("like"));
		User u = (User) req.getSession().getAttribute("user");
		// check if this post was liked by the user before
		// remove like if so or add a like
		try {
			if (commentDao.checkIfAlreadyLiked(u.getId(), commentId)) {
				commentDao.removeLike(u.getId(), commentId);
				return new Gson().toJson(commentDao.getLikesByID(commentId));
			}
			commentDao.likeComment(u.getId(), commentId);
			return new Gson().toJson(commentDao.getLikesByID(commentId));
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
