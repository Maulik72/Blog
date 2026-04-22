<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Categories - Admin</title>
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
            <a href="admin-categories" class="active fw-bold"><i class="bi bi-tags me-2"></i> Categories</a>
            <a href="#"><i class="bi bi-people me-2"></i> Manage Users</a>
            <a href="#"><i class="bi bi-journal-text me-2"></i> Moderate Posts</a>
            <hr class="border-secondary my-3">
            <a href="home"><i class="bi bi-arrow-left-circle me-2"></i> Back to Live Blog</a>
        </nav>
    </div>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold m-0 text-dark">Category Management</h2>
                <p class="text-muted mb-0">Create new topics or remove obsolete ones.</p>
            </div>
            <button class="btn btn-primary fw-bold shadow-sm" data-bs-toggle="modal" data-bs-target="#addCategoryModal">
                <i class="bi bi-plus-lg"></i> Add Category
            </button>
        </div>

        <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="py-3 px-4">ID</th>
                        <th class="py-3 px-4">Category Name</th>
                        <th class="py-3 px-4">Description</th>
                        <th class="py-3 px-4 text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty categories}">
                            <tr><td colspan="4" class="text-center py-4 text-muted">No categories found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="cat" items="${categories}">
                                <tr>
                                    <td class="px-4 text-muted fw-bold">#${cat.id}</td>
                                    <td class="px-4 fw-bold text-dark">${cat.name}</td>
                                    <td class="px-4 text-secondary">${empty cat.description ? '<span class="text-muted fst-italic">No description</span>' : cat.description}</td>
                                    <td class="px-4 text-end">
                                        <form action="admin-categories" method="POST" class="m-0 d-inline" onsubmit="return confirm('Delete this category? Any posts using this category will become Uncategorized.');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <button type="submit" class="btn btn-outline-danger btn-sm rounded-pill px-3 fw-bold">
                                                <i class="bi bi-trash3-fill"></i> Delete
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

    <div class="modal fade" id="addCategoryModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content border-0 shadow">
                <form action="admin-categories" method="POST">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-header bg-light">
                        <h5 class="modal-title fw-bold"><i class="bi bi-tag-fill text-primary"></i> Create New Category</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body p-4">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Category Name <span class="text-danger">*</span></label>
                            <input type="text" name="name" class="form-control bg-light" required placeholder="e.g. Artificial Intelligence">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Description <span class="text-muted fw-normal">(Optional)</span></label>
                            <textarea name="description" class="form-control bg-light" rows="3" placeholder="Briefly describe what this category covers..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer bg-light">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary fw-bold px-4">Save Category</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>