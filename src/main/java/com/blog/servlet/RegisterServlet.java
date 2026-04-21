package com.blog.servlet;

import com.blog.dao.UserDAO;
import com.blog.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Show the registration page
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form data
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User newUser = new User(username, email, password);

        if (userDAO.registerUser(newUser)) {
            // Registration successful, send them to login page
            response.sendRedirect("login?success=registered");
        } else {
            // Registration failed (email/username might exist), send back to form with error
            request.setAttribute("error", "Registration failed. Username or Email might already exist.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}