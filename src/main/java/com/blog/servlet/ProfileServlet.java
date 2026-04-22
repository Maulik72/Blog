package com.blog.servlet;

import com.blog.dao.BookmarkDAO;
import com.blog.dao.FollowDAO;
import com.blog.dao.PostDAO;
import com.blog.dao.UserDAO;
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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        int targetUserId = 0;

        if (idParam != null && !idParam.isEmpty()) {
            try { targetUserId = Integer.parseInt(idParam); }
            catch (NumberFormatException e) { response.sendRedirect("home"); return; }
        } else {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("loggedUser") != null) {
                User loggedUser = (User) session.getAttribute("loggedUser");
                targetUserId = loggedUser.getId();
            } else {
                response.sendRedirect("login.jsp");
                return;
            }
        }

        UserDAO userDAO = new UserDAO();
        User profileUser = userDAO.getUserById(targetUserId);

        if (profileUser != null) {
            PostDAO postDAO = new PostDAO();
            List<Post> userPosts = postDAO.getPostsByUserId(targetUserId);

            HttpSession session = request.getSession(false);
            boolean isFollowing = false;

            if (session != null && session.getAttribute("loggedUser") != null) {
                User loggedUser = (User) session.getAttribute("loggedUser");
                if (loggedUser.getId() == targetUserId) {
                    BookmarkDAO bookmarkDAO = new BookmarkDAO();
                    List<Post> savedPosts = bookmarkDAO.getSavedPostsByUserId(targetUserId);
                    request.setAttribute("savedPosts", savedPosts);
                } else {
                    FollowDAO followDAO = new FollowDAO();
                    isFollowing = followDAO.isFollowing(loggedUser.getId(), targetUserId);
                }
            }

            // --- FETCH STATS AND USER LISTS ---
            FollowDAO followDAO = new FollowDAO();
            request.setAttribute("followersCount", followDAO.getFollowerCount(targetUserId));
            request.setAttribute("followingCount", followDAO.getFollowingCount(targetUserId));
            request.setAttribute("isFollowing", isFollowing);

            // NEW: Fetch the actual lists for the Modals
            request.setAttribute("followersList", followDAO.getFollowers(targetUserId));
            request.setAttribute("followingList", followDAO.getFollowing(targetUserId));
            // ----------------------------------

            request.setAttribute("profileUser", profileUser);
            request.setAttribute("userPosts", userPosts);
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        } else {
            response.sendRedirect("home");
        }
    }
}