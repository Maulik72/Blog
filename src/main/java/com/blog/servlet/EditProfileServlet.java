package com.blog.servlet;

import com.blog.dao.UserDAO;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/edit-profile")
public class EditProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        String bio = request.getParameter("bio");
        String profileImageUrl = request.getParameter("profileImageUrl");

        // Set default image if left blank
        if (profileImageUrl == null || profileImageUrl.trim().isEmpty()) {
            profileImageUrl = "default_avatar.png";
        }

        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.updateProfile(loggedUser.getId(), bio, profileImageUrl);

        if (success) {
            // Update the session object so the UI reflects the changes instantly
            loggedUser.setBio(bio);
            loggedUser.setProfileImageUrl(profileImageUrl);
            session.setAttribute("loggedUser", loggedUser);
        }

        // Redirect back to their profile
        response.sendRedirect("profile");
    }
}