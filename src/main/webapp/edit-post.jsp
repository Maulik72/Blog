<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Post - Advanced Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="home">Back to Home</a>
    </div>
</nav>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm p-4">
                <h3 class="mb-4">Edit Your Post</h3>

                <form action="edit-post" method="POST">
                    <input type="hidden" name="id" value="${post.id}">

                    <div class="mb-3">
                        <label for="title" class="form-label fw-bold">Post Title</label>
                        <input type="text" class="form-control" id="title" name="title" value="${post.title}" required>
                    </div>

                    <div class="mb-3">
                        <label for="content" class="form-label fw-bold">Content</label>
                        <textarea class="form-control" id="content" name="content" rows="8" required>${post.content}</textarea>
                    </div>

                    <div class="d-flex justify-content-between">
                        <a href="view-post?id=${post.id}" class="btn btn-secondary">Cancel</a>
                        <button type="submit" class="btn btn-success">Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>