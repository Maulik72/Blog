<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home - Advanced Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        /* Smooth hover effect for blog cards */
        .card-hover { transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out; }
        .card-hover:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important; }

        /* Truncates long text to exactly 3 lines with an ellipsis (...) */
        .content-truncate {
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
    </style>
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-5 shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="home"><i class="bi bi-journal-code"></i> Advanced Blog</a>
        <div class="d-flex align-items-center">
            <c:choose>
                <c:when test="${not empty sessionScope.loggedUser}">
                    <span class="navbar-text me-3 text-light d-none d-md-block">
                        Welcome back, <strong>${sessionScope.loggedUser.username}</strong>!
                    </span>

                    <a href="profile" class="btn btn-outline-info btn-sm me-2 fw-semibold">
                        <i class="bi bi-person-circle"></i> My Profile
                    </a>

                    <a href="create-post" class="btn btn-primary btn-sm me-2 fw-semibold shadow-sm">
                        <i class="bi bi-pencil-square"></i> Write
                    </a>
                    <a href="logout" class="btn btn-danger btn-sm fw-semibold shadow-sm">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="login" class="btn btn-outline-light btn-sm me-2">Login</a>
                    <a href="register" class="btn btn-primary btn-sm shadow-sm">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<div class="container mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold m-0"><i class="bi bi-collection"></i> Latest Posts</h2>
    </div>

    <div class="row g-4">
        <c:choose>
            <c:when test="${empty postList}">
                <div class="col-12">
                    <div class="alert alert-info shadow-sm border-0 py-4 text-center">
                        <i class="bi bi-info-circle fs-4 d-block mb-2"></i>
                        No posts found. Be the first to share something amazing!
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="post" items="${postList}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100 border-0 shadow-sm card-hover">
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title fw-bold text-dark mb-2">${post.title}</h5>

                                <div class="text-muted small mb-3">
                                    <i class="bi bi-person"></i> <a href="profile?id=${post.userId}" class="text-decoration-none fw-semibold">${post.authorName}</a>
                                    <br>
                                    <i class="bi bi-calendar3"></i> ${post.createdAt}
                                </div>

                                <p class="card-text text-muted content-truncate mb-4">${post.content}</p>

                                <a href="view-post?id=${post.id}" class="btn btn-outline-primary btn-sm mt-auto w-100 fw-semibold">
                                    Read Article <i class="bi bi-arrow-right"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${noOfPages > 1}">
        <nav aria-label="Blog post navigation" class="mt-5">
            <ul class="pagination justify-content-center shadow-sm" style="border-radius: 0.375rem; overflow: hidden; display: inline-flex; margin: 0 auto;">

                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link px-4" href="home?page=${currentPage - 1}" tabindex="-1">Previous</a>
                </li>

                <c:forEach begin="1" end="${noOfPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage eq i}">
                            <li class="page-item active" aria-current="page">
                                <span class="page-link px-3 fw-bold">${i}</span>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="page-item">
                                <a class="page-link px-3" href="home?page=${i}">${i}</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <li class="page-item ${currentPage == noOfPages ? 'disabled' : ''}">
                    <a class="page-link px-4" href="home?page=${currentPage + 1}">Next</a>
                </li>

            </ul>
        </nav>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>