package com.blog.servlet;

import com.blog.dao.FollowDAO;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/toggle-follow")
public class FollowServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        String targetIdParam = request.getParameter("targetUserId");

        if (targetIdParam != null && !targetIdParam.isEmpty()) {
            try {
                int targetUserId = Integer.parseInt(targetIdParam);

                FollowDAO followDAO = new FollowDAO();
                followDAO.toggleFollow(loggedUser.getId(), targetUserId);

                // Redirect exactly back to the profile they were just looking at!
                response.sendRedirect("profile?id=" + targetUserId);

            } catch (NumberFormatException e) { response.sendRedirect("home"); }
        } else {
            response.sendRedirect("home");
        }
    }
}