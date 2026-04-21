<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Write a Post - Advanced Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="home">Advanced Blog</a>
        <div class="d-flex">
            <span class="navbar-text me-3 text-white">Hello, ${sessionScope.loggedUser.username}!</span>
            <a href="home" class="btn btn-outline-light me-2">Cancel</a>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h3 class="mb-4">Create a New Post</h3>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <form action="create-post" method="POST">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Post Title</label>
                            <input type="text" name="title" class="form-control form-control-lg" placeholder="Enter an engaging title..." required>
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-bold">Content</label>
                            <%-- 10 rows makes it look like a proper blog editor --%>
                            <textarea name="content" class="form-control" rows="10" placeholder="Write your thoughts here..." required></textarea>
                        </div>
                        <button type="submit" class="btn btn-success btn-lg px-5">Publish</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>