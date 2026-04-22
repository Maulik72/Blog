package com.blog.servlet;

import com.blog.dao.BookmarkDAO;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/toggle-bookmark")
public class BookmarkServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("loggedUser");
        String postIdParam = request.getParameter("postId");

        if (postIdParam != null && !postIdParam.isEmpty()) {
            try {
                int postId = Integer.parseInt(postIdParam);
                BookmarkDAO bookmarkDAO = new BookmarkDAO();
                bookmarkDAO.toggleBookmark(user.getId(), postId);

                // Redirect back to the post
                response.sendRedirect("view-post?id=" + postId);
            } catch (NumberFormatException e) {
                response.sendRedirect("home");
            }
        } else {
            response.sendRedirect("home");
        }
    }
}