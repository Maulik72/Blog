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
        .card-hover { transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out; }
        .card-hover:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important; }
        .content-truncate {
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
    </style>
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-5 shadow-sm sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold" href="home"><i class="bi bi-journal-code"></i> Advanced Blog</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle ${not empty activeCategoryId ? 'active fw-bold text-white' : ''}" href="#" id="categoryDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="bi bi-funnel"></i>
                        <c:choose>
                            <c:when test="${not empty activeCategoryId}">
                                <c:forEach var="cat" items="${categories}">
                                    <c:if test="${cat.id == activeCategoryId}">${cat.name}</c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>Categories</c:otherwise>
                        </c:choose>
                    </a>
                    <ul class="dropdown-menu shadow-sm border-0 mt-2" aria-labelledby="categoryDropdown">
                        <li>
                            <a class="dropdown-item ${empty activeCategoryId ? 'active fw-bold' : ''}" href="home">
                                <i class="bi bi-collection"></i> All Posts
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <c:forEach var="cat" items="${categories}">
                            <li>
                                <a class="dropdown-item ${activeCategoryId == cat.id ? 'active fw-bold' : ''}" href="home?categoryId=${cat.id}">
                                    ${cat.name}
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </li>
            </ul>

            <div class="d-flex align-items-center mt-3 mt-lg-0">
                <c:choose>
                    <c:when test="${not empty sessionScope.loggedUser}">
                        <span class="navbar-text me-3 text-light d-none d-xl-block">
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
    </div>
</nav>

<div class="container mb-5">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold m-0">
            <c:choose>
                <c:when test="${not empty activeCategoryId}">
                    <i class="bi bi-funnel-fill text-primary"></i> Filtered Results
                </c:when>
                <c:otherwise><i class="bi bi-stars text-warning"></i> Latest Posts</c:otherwise>
            </c:choose>
        </h2>
        <c:if test="${not empty activeCategoryId}">
            <a href="home" class="btn btn-outline-secondary btn-sm rounded-pill"><i class="bi bi-x-circle"></i> Clear Filter</a>
        </c:if>
    </div>

    <div class="row g-4">
        <c:choose>
            <c:when test="${empty postList}">
                <div class="col-12">
                    <div class="alert alert-info shadow-sm border-0 py-5 text-center rounded-4">
                        <i class="bi bi-inbox fs-1 d-block mb-3 text-secondary"></i>
                        <h4 class="fw-bold">No posts found</h4>
                        <p class="mb-0">There are no posts in this category yet. Be the first to write one!</p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="post" items="${postList}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100 border-0 shadow-sm card-hover rounded-4 overflow-hidden">
                            <div class="card-body d-flex flex-column p-4">

                                <div class="mb-3">
                                    <span class="badge bg-primary bg-opacity-10 text-primary rounded-pill px-3 py-2 border border-primary border-opacity-25">
                                        <i class="bi bi-folder2"></i> ${not empty post.categoryName ? post.categoryName : 'Uncategorized'}
                                    </span>
                                </div>

                                <h4 class="card-title fw-bold text-dark mb-3">${post.title}</h4>

                                <div class="text-muted small mb-3 fw-medium">
                                    <i class="bi bi-person-circle"></i> <a href="profile?id=${post.userId}" class="text-decoration-none text-muted hover-primary">${post.authorName}</a>
                                    <span class="mx-2 opacity-50">•</span>
                                    <i class="bi bi-calendar3"></i> ${post.createdAt}
                                </div>

                                <p class="card-text text-secondary content-truncate mb-4" style="line-height: 1.6;">${post.content}</p>

                                <a href="view-post?id=${post.id}" class="btn btn-light btn-sm mt-auto w-100 fw-bold border py-2">
                                    Read Article <i class="bi bi-arrow-right ms-1"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <c:set var="catParam" value="${not empty activeCategoryId ? '&categoryId=' += activeCategoryId : ''}" />

    <c:if test="${noOfPages > 1}">
        <nav aria-label="Blog post navigation" class="mt-5 pt-3">
            <ul class="pagination justify-content-center shadow-sm" style="border-radius: 0.5rem; overflow: hidden; display: inline-flex; margin: 0 auto;">
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link px-4 py-2" href="home?page=${currentPage - 1}${catParam}" tabindex="-1">Previous</a>
                </li>
                <c:forEach begin="1" end="${noOfPages}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link px-3 py-2 ${currentPage == i ? 'fw-bold' : ''}" href="home?page=${i}${catParam}">${i}</a>
                    </li>
                </c:forEach>
                <li class="page-item ${currentPage == noOfPages ? 'disabled' : ''}">
                    <a class="page-link px-4 py-2" href="home?page=${currentPage + 1}${catParam}">Next</a>
                </li>
            </ul>
        </nav>
    </c:if>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>