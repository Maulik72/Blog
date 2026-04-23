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

@WebServlet("/delete-account")
public class DeleteAccountServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Security Check: Are they logged in?
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login");
            return;
        }

        // 2. Get the logged-in user's ID
        User user = (User) session.getAttribute("loggedUser");
        UserDAO userDAO = new UserDAO();

        // 3. Execute the deletion (This triggers ON DELETE CASCADE in MySQL)
        boolean isDeleted = userDAO.deleteUser(user.getId());

        if (isDeleted) {
            // 4. Destroy the session so they don't crash the app on the next click
            session.invalidate();

            // 5. Send them away with a message
            response.sendRedirect("login?success=deleted");
        } else {
            // If the database fails for some reason
            response.sendRedirect("profile?error=deleteFailed");
        }
    }
}