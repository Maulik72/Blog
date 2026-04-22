<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Moderate Posts - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .sidebar { min-height: 100vh; width: 280px; background-color: #343a40; }
        .sidebar a { color: #c2c7d0; text-decoration: none; padding: 12px 20px; display: block; transition: 0.2s; }
        .sidebar a:hover, .sidebar a.active { background-color: #007bff; color: white; border-radius: 5px; }
        .main-content { flex-grow: 1; padding: 30px; }
    </style>
</head>
<body class="d-flex">

    <div class="sidebar p-3 shadow">
        <h4 class="text-white text-center fw-bold mb-4 border-bottom border-secondary pb-3">
            <i class="bi bi-shield-lock-fill text-warning"></i> Admin Panel
        </h4>
        <nav class="nav flex-column gap-2">
            <a href="admin-dashboard"><i class="bi bi-speedometer2 me-2"></i> Dashboard</a>
            <a href="admin-categories"><i class="bi bi-tags me-2"></i> Categories</a>
            <a href="admin-users"><i class="bi bi-people me-2"></i> Manage Users</a>
            <a href="admin-posts" class="active fw-bold"><i class="bi bi-journal-text me-2"></i> Moderate Posts</a>
            <hr class="border-secondary my-3">
            <a href="home"><i class="bi bi-arrow-left-circle me-2"></i> Back to Live Blog</a>
        </nav>
    </div>

    <div class="main-content">
        <div class="mb-4">
            <h2 class="fw-bold m-0 text-dark">Content Moderation</h2>
            <p class="text-muted mb-0">Review posts, unpublish inappropriate content, or delete spam.</p>
        </div>

        <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="py-3 px-4">Title</th>
                        <th class="py-3 px-4">Author</th>
                        <th class="py-3 px-4">Category</th>
                        <th class="py-3 px-4 text-center">Status</th>
                        <th class="py-3 px-4 text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${postList}">
                        <tr>
                            <td class="px-4 fw-bold text-dark" style="max-width: 300px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                ${p.title}
                            </td>
                            <td class="px-4 text-secondary"><i class="bi bi-person-circle"></i> ${p.authorName}</td>
                            <td class="px-4 text-secondary">
                                <span class="badge bg-light text-dark border"><i class="bi bi-folder2"></i> ${not empty p.categoryName ? p.categoryName : 'None'}</span>
                            </td>

                            <td class="px-4 text-center">
                                <span class="badge rounded-pill ${p.status == 'PUBLISHED' ? 'bg-success' : 'bg-secondary'}">
                                    ${p.status}
                                </span>
                            </td>

                            <td class="px-4 text-end">
                                <div class="d-flex justify-content-end gap-2">

                                    <a href="view-post?id=${p.id}" target="_blank" class="btn btn-outline-primary btn-sm rounded-pill fw-bold" title="View Post">
                                        <i class="bi bi-eye"></i>
                                    </a>

                                    <form action="admin-posts" method="POST" class="m-0">
                                        <input type="hidden" name="action" value="toggleStatus">
                                        <input type="hidden" name="postId" value="${p.id}">
                                        <input type="hidden" name="currentStatus" value="${p.status}">
                                        <button type="submit" class="btn btn-sm rounded-pill fw-bold ${p.status == 'PUBLISHED' ? 'btn-outline-warning' : 'btn-outline-success'}" title="${p.status == 'PUBLISHED' ? 'Unpublish to Draft' : 'Publish to Live'}">
                                            <i class="bi ${p.status == 'PUBLISHED' ? 'bi-eye-slash' : 'bi-check-circle'}"></i>
                                            ${p.status == 'PUBLISHED' ? 'Hide' : 'Publish'}
                                        </button>
                                    </form>

                                    <form action="admin-posts" method="POST" class="m-0" onsubmit="return confirm('Permanently delete this post?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="postId" value="${p.id}">
                                        <button type="submit" class="btn btn-outline-danger btn-sm rounded-pill fw-bold" title="Delete Post">
                                            <i class="bi bi-trash3-fill"></i>
                                        </button>
                                    </form>

                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>