package com.blog.servlet;

import com.blog.dao.CommentDAO;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/add-comment")
public class AddCommentServlet extends HttpServlet {
    private CommentDAO commentDAO = new CommentDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            int postId = Integer.parseInt(request.getParameter("postId"));
            String content = request.getParameter("content");
            User user = (User) session.getAttribute("loggedUser");

            commentDAO.addComment(postId, user.getId(), content);
            response.sendRedirect("view-post?id=" + postId);
        } else {
            response.sendRedirect("login");
        }
    }
}