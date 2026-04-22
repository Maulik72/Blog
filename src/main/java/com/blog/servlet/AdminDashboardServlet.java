package com.blog.servlet;

import com.blog.dao.AdminDAO;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 1. SECURITY LOCKDOWN: Check if logged in AND role is ADMIN
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login");
            return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (!"ADMIN".equals(loggedUser.getRole())) {
            // Kick normal users back to the home page if they try to type /admin-dashboard
            response.sendRedirect("home");
            return;
        }

        // 2. Fetch stats and load the page
        AdminDAO adminDAO = new AdminDAO();
        Map<String, Integer> stats = adminDAO.getDashboardStats();

        request.setAttribute("stats", stats);
        request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
    }
}