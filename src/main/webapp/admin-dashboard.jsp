<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Advanced Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .sidebar { min-height: 100vh; width: 280px; background-color: #343a40; }
        .sidebar a { color: #c2c7d0; text-decoration: none; padding: 12px 20px; display: block; transition: 0.2s; }
        .sidebar a:hover, .sidebar a.active { background-color: #007bff; color: white; border-radius: 5px; }
        .main-content { flex-grow: 1; padding: 30px; }
        .stat-card { transition: 0.3s; border-left: 5px solid; }
        .stat-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
    </style>
</head>
<body class="d-flex">

    <div class="sidebar p-3 shadow">
        <h4 class="text-white text-center fw-bold mb-4 border-bottom border-secondary pb-3">
            <i class="bi bi-shield-lock-fill text-warning"></i> Admin Panel
        </h4>
        <nav class="nav flex-column gap-2">
            <a href="admin-dashboard" class="active fw-bold"><i class="bi bi-speedometer2 me-2"></i> Dashboard</a>
            <a href="admin-categories"><i class="bi bi-tags me-2"></i> Categories</a>
            <a href="admin-users"><i class="bi bi-people me-2"></i> Manage Users</a>
            <a href="#"><i class="bi bi-journal-text me-2"></i> Moderate Posts</a>

            <hr class="border-secondary my-3">
            <a href="home"><i class="bi bi-arrow-left-circle me-2"></i> Back to Live Blog</a>
        </nav>
    </div>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold m-0 text-dark">Dashboard Overview</h2>
            <div class="text-muted">Logged in as: <strong class="text-dark">${sessionScope.loggedUser.username}</strong></div>
        </div>

        <div class="row g-4">
            <div class="col-md-6 col-lg-3">
                <div class="card border-0 shadow-sm stat-card" style="border-left-color: #007bff !important;">
                    <div class="card-body">
                        <h6 class="text-muted text-uppercase fw-bold mb-2">Total Users</h6>
                        <h2 class="fw-bold mb-0 text-primary"><i class="bi bi-people-fill float-end opacity-25"></i> ${stats.totalUsers}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-lg-3">
                <div class="card border-0 shadow-sm stat-card" style="border-left-color: #28a745 !important;">
                    <div class="card-body">
                        <h6 class="text-muted text-uppercase fw-bold mb-2">Published Posts</h6>
                        <h2 class="fw-bold mb-0 text-success"><i class="bi bi-journal-check float-end opacity-25"></i> ${stats.totalPosts}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-lg-3">
                <div class="card border-0 shadow-sm stat-card" style="border-left-color: #ffc107 !important;">
                    <div class="card-body">
                        <h6 class="text-muted text-uppercase fw-bold mb-2">Categories</h6>
                        <h2 class="fw-bold mb-0 text-warning"><i class="bi bi-folder-fill float-end opacity-25"></i> ${stats.totalCategories}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-lg-3">
                <div class="card border-0 shadow-sm stat-card" style="border-left-color: #dc3545 !important;">
                    <div class="card-body">
                        <h6 class="text-muted text-uppercase fw-bold mb-2">Total Comments</h6>
                        <h2 class="fw-bold mb-0 text-danger"><i class="bi bi-chat-dots-fill float-end opacity-25"></i> ${stats.totalComments}</h2>
                    </div>
                </div>
            </div>
        </div>

        <div class="mt-5 text-center text-muted">
            <i class="bi bi-tools fs-1 mb-3 d-block"></i>
            <h5>Admin tools are active.</h5>
            <p>Use the sidebar menu to manage categories, users, and moderate the platform.</p>
        </div>
    </div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>