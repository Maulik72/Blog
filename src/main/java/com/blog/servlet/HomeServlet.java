package com.blog.servlet;

import com.blog.dao.PostDAO;
import com.blog.model.Post;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private PostDAO postDAO = new PostDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;           // Default to page 1
        int recordsPerPage = 6; // Set how many posts show up per page

        // 1. Check if a specific page is requested (e.g., /home?page=3)
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1; // Prevent negative or zero pages
                }
            } catch (NumberFormatException e) {
                page = 1; // Fallback to page 1 if someone types ?page=abc
            }
        }

        // 2. Calculate the SQL Offset (e.g., Page 2 skips the first 6 posts)
        int offset = (page - 1) * recordsPerPage;

        // 3. Fetch the specific chunk of posts and the total count from DB
        List<Post> posts = postDAO.getPublishedPostsByPage(offset, recordsPerPage);
        int noOfRecords = postDAO.getTotalPublishedPostsCount();

        // 4. Calculate total pages (e.g., 14 records / 6 per page = 2.33 -> rounds up to 3 pages)
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);

        // 5. Pass all the pagination data to the JSP
        request.setAttribute("postList", posts);
        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);

        // 6. Forward to the visual page
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }
}