<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Users - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f4f6f9; }
        .sidebar { min-height: 100vh; width: 280px; background-color: #343a40; }
        .sidebar a { color: #c2c7d0; text-decoration: none; padding: 12px 20px; display: block; transition: 0.2s; }
        .sidebar a:hover, .sidebar a.active { background-color: #007bff; color: white; border-radius: 5px; }
        .main-content { flex-grow: 1; padding: 30px; }
        .avatar-sm { width: 40px; height: 40px; object-fit: cover; border-radius: 50%; }
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
            <a href="admin-users" class="active fw-bold"><i class="bi bi-people me-2"></i> Manage Users</a>
            <a href="admin-posts"><i class="bi bi-journal-text me-2"></i> Moderate Posts</a>
            <hr class="border-secondary my-3">
            <a href="home"><i class="bi bi-arrow-left-circle me-2"></i> Back to Live Blog</a>
        </nav>
    </div>

    <div class="main-content">
        <div class="mb-4">
            <h2 class="fw-bold m-0 text-dark">User Management</h2>
            <p class="text-muted mb-0">Promote users to Admin or remove accounts from the platform.</p>
        </div>

        <c:if test="${param.error == 'self_action'}">
            <div class="alert alert-danger shadow-sm border-0"><i class="bi bi-exclamation-triangle-fill"></i> You cannot change the role or delete your own admin account!</div>
        </c:if>

        <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="py-3 px-4">User</th>
                        <th class="py-3 px-4">Email</th>
                        <th class="py-3 px-4">Joined Date</th>
                        <th class="py-3 px-4 text-center">Role</th>
                        <th class="py-3 px-4 text-end">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${userList}">
                        <tr>
                            <td class="px-4 py-3">
                                <div class="d-flex align-items-center">
                                    <img src="${u.profileImageUrl == 'default_avatar.png' ? 'https://ui-avatars.com/api/?name=' += u.username += '&size=40&background=random' : u.profileImageUrl}" class="avatar-sm me-3 shadow-sm border">
                                    <div class="fw-bold text-dark">${u.username}</div>
                                </div>
                            </td>
                            <td class="px-4 text-secondary">${u.email}</td>
                            <td class="px-4 text-secondary">${u.createdAt}</td>

                            <td class="px-4 text-center">
                                <form action="admin-users" method="POST" class="m-0 d-flex justify-content-center">
                                    <input type="hidden" name="action" value="updateRole">
                                    <input type="hidden" name="userId" value="${u.id}">
                                    <select name="role" class="form-select form-select-sm w-auto fw-bold ${u.role == 'ADMIN' ? 'bg-warning text-dark border-warning' : 'bg-light text-secondary'}" onchange="this.form.submit()" ${u.id == sessionScope.loggedUser.id ? 'disabled' : ''}>
                                        <option value="USER" ${u.role == 'USER' ? 'selected' : ''}>USER</option>
                                        <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                    </select>
                                </form>
                            </td>

                            <td class="px-4 text-end">
                                <c:choose>
                                    <c:when test="${u.id == sessionScope.loggedUser.id}">
                                        <button class="btn btn-sm btn-secondary rounded-pill px-3 fw-bold" disabled>It's You!</button>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="admin-users" method="POST" class="m-0 d-inline" onsubmit="return confirm('Are you absolutely sure you want to delete ${u.username}? ALL their posts and comments will be permanently erased!');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="userId" value="${u.id}">
                                            <button type="submit" class="btn btn-outline-danger btn-sm rounded-pill px-3 fw-bold">
                                                <i class="bi bi-person-x-fill"></i> Delete
                                            </button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
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