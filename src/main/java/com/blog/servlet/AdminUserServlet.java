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
import java.util.List;

@WebServlet("/admin-users")
public class AdminUserServlet extends HttpServlet {

    // Helper method for strict security
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            User user = (User) session.getAttribute("loggedUser");
            return "ADMIN".equals(user.getRole());
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) { response.sendRedirect("home"); return; }

        UserDAO userDAO = new UserDAO();
        List<User> allUsers = userDAO.getAllUsers();

        request.setAttribute("userList", allUsers);
        request.getRequestDispatcher("/admin-users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) { response.sendRedirect("home"); return; }

        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();

        // Safety feature: Identify the admin who is currently making the changes
        User currentAdmin = (User) request.getSession().getAttribute("loggedUser");

        try {
            int targetUserId = Integer.parseInt(request.getParameter("userId"));

            // Prevent the admin from deleting or demoting themselves!
            if (targetUserId == currentAdmin.getId()) {
                response.sendRedirect("admin-users?error=self_action");
                return;
            }

            if ("updateRole".equals(action)) {
                String newRole = request.getParameter("role");
                if ("ADMIN".equals(newRole) || "USER".equals(newRole)) {
                    userDAO.updateUserRole(targetUserId, newRole);
                }
            } else if ("delete".equals(action)) {
                userDAO.deleteUser(targetUserId);
            }

        } catch (NumberFormatException e) {
            // Ignore bad IDs
        }

        response.sendRedirect("admin-users");
    }
}