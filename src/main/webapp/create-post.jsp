<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Write a Post - Advanced Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        .blog-container { max-width: 900px; margin: auto; }
    </style>
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-5 shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="home"><i class="bi bi-journal-code"></i> Advanced Blog</a>
        <div class="d-flex align-items-center">
            <span class="navbar-text me-3 text-light d-none d-md-block">
                Welcome back, <strong>${sessionScope.loggedUser.username}</strong>!
            </span>
            <a href="profile" class="btn btn-outline-info btn-sm me-2 fw-semibold">
                <i class="bi bi-person-circle"></i> My Profile
            </a>
            <a href="home" class="btn btn-outline-light btn-sm fw-semibold">Cancel</a>
        </div>
    </div>
</nav>

<div class="container blog-container mb-5">
    <div class="card border-0 shadow-sm">
        <div class="card-body p-4 p-md-5">
            <h2 class="fw-bold mb-4"><i class="bi bi-pencil-square text-primary"></i> Create a New Post</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-danger shadow-sm"><i class="bi bi-exclamation-triangle"></i> ${error}</div>
            </c:if>

            <form action="create-post" method="POST">

                <div class="mb-4">
                    <label class="form-label fw-bold text-dark">Post Title</label>
                    <input type="text" name="title" class="form-control form-control-lg bg-light" placeholder="Enter an engaging title..." required>
                </div>

                <div class="row mb-4">
                    <div class="col-md-6 mb-3 mb-md-0">
                        <label class="form-label fw-bold text-dark"><i class="bi bi-folder2-open"></i> Category</label>
                        <select name="categoryId" class="form-select bg-light">
                            <option value="">Uncategorized</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}">${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold text-dark"><i class="bi bi-tags"></i> Tags</label>
                        <input type="text" name="tags" class="form-control bg-light" placeholder="java, webdev, tutorial">
                        <div class="form-text">Separate tags with commas.</div>
                    </div>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-bold text-dark">Content</label>
                    <textarea name="content" class="form-control bg-light" rows="12" placeholder="Write your thoughts here..." required></textarea>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
                    <a href="home" class="btn btn-light px-4 border">Cancel</a>
                    <button type="submit" class="btn btn-primary px-5 fw-bold shadow-sm">Publish Post <i class="bi bi-send ms-1"></i></button>
                </div>

            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>