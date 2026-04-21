package com.blog.servlet;

import com.blog.dao.CategoryDAO;
import com.blog.dao.PostDAO;
import com.blog.model.Category;
import com.blog.model.Post;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/edit-post")
public class EditPostServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp"); return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int postId = Integer.parseInt(idParam);
                PostDAO postDAO = new PostDAO();
                Post post = postDAO.getPostById(postId, loggedUser.getId());

                if (post != null && post.getUserId() == loggedUser.getId()) {
                    // NEW: Fetch Categories & Tags for the Edit Form
                    CategoryDAO categoryDAO = new CategoryDAO();
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.setAttribute("tags", postDAO.getTagsForPost(postId));

                    request.setAttribute("post", post);
                    request.getRequestDispatcher("/edit-post.jsp").forward(request, response);
                } else {
                    response.sendRedirect("home?error=unauthorized");
                }
            } catch (NumberFormatException e) { response.sendRedirect("home"); }
        } else { response.sendRedirect("home"); }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp"); return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        try {
            int postId = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String categoryIdParam = request.getParameter("categoryId");
            String tagsInput = request.getParameter("tags");

            Post post = new Post();
            post.setId(postId);
            post.setUserId(loggedUser.getId());
            post.setTitle(title);
            post.setContent(content);

            if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                try { post.setCategoryId(Integer.parseInt(categoryIdParam)); }
                catch (NumberFormatException e) { post.setCategoryId(null); }
            }

            // NEW: Use the updated method
            PostDAO postDAO = new PostDAO();
            if (postDAO.updatePostWithTags(post, tagsInput)) {
                response.sendRedirect("view-post?id=" + postId + "&msg=updated");
            } else {
                response.sendRedirect("home?error=update_failed");
            }
        } catch (NumberFormatException e) { response.sendRedirect("home"); }
    }
}