package com.friendbook.Controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.friendbook.Model.comment.Comment;
import com.friendbook.Model.comment.CommentDao;
import com.friendbook.Model.user.User;
import com.google.gson.Gson;

@Controller
public class CommentController {

	@Autowired
	private CommentDao commentDao;

	@RequestMapping(value = "/comment", method = RequestMethod.POST)
	protected void comment(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("user");
		long postId = Long.parseLong(request.getParameter("currentPost"));
		Long commentId = null;
		if (!request.getParameter("currentComment").equals("null")) {
			System.out.println(request.getParameter("currentComment"));
			commentId = Long.parseLong(request.getParameter("currentComment"));
		}
		Comment comment = new Comment(user, user.getId(), postId, commentId, request.getParameter("text"));
		try {
			commentDao.addComment(comment.getUserId(), comment);

			String json = new Gson().toJson(commentDao.getLastCommentByUserId(user.getId()));
			response.getWriter().print(json);
		} catch (SQLException e) {
			System.out.println("SQLBug: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Bug: " + e.getMessage());
		}
	}

	@RequestMapping(value = "/likeComment", method = RequestMethod.POST)
	protected void likeComment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long commentId = Long.parseLong(req.getParameter("like"));
		User u = (User) req.getSession().getAttribute("user");
		System.out.println(commentId);
		// check if this post was liked by the user before
		// remove like if so or add a like
		try {
			if (commentDao.checkIfAlreadyLiked(u.getId(), commentId)) {

				commentDao.removeLike(u.getId(), commentId);
				int likes = commentDao.getLikesByID(commentId);
				System.out.println(likes);
				resp.getWriter().print(likes);

			} else {

				commentDao.likeComment(u.getId(), commentId);
				resp.getWriter().print(commentDao.getLikesByID(commentId));

			}
		} catch (SQLException e) {
			System.out.println("SQL Bug: " + e.getMessage());
		}
	}
}
