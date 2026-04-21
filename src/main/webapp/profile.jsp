<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>${profileUser.username}'s Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="home">Back to Home</a>
    </div>
</nav>

<div class="container">
    <div class="card p-4 shadow-sm text-center mb-4">
        <img src="${profileUser.profileImageUrl == 'default_avatar.png' ? 'https://ui-avatars.com/api/?name=' += profileUser.username += '&size=128&background=random' : profileUser.profileImageUrl}"
             alt="Profile Image" class="rounded-circle mx-auto d-block mb-3" style="width: 120px; height: 120px; object-fit: cover;">

        <h2>${profileUser.username} <span class="badge bg-secondary fs-6">${profileUser.role}</span></h2>

        <p class="text-muted mt-2">
            ${empty profileUser.bio ? 'This user has no bio yet.' : profileUser.bio}
        </p>

        <c:if test="${not empty sessionScope.loggedUser and sessionScope.loggedUser.id == profileUser.id}">
            <button class="btn btn-outline-primary btn-sm mt-2" data-bs-toggle="modal" data-bs-target="#editProfileModal">
                <i class="bi bi-pencil"></i> Edit Profile
            </button>
        </c:if>
    </div>

    <h4>Posts by ${profileUser.username}</h4>
    <hr>
    <div class="row">
        <c:choose>
            <c:when test="${empty userPosts}">
                <p class="text-muted">No posts written yet.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="post" items="${userPosts}">
                    <div class="col-md-6 mb-3">
                        <div class="card shadow-sm h-100">
                            <div class="card-body">
                                <h5 class="card-title">${post.title}</h5>
                                <p class="card-text text-muted" style="font-size: 0.9em;">
                                    Published: ${post.createdAt}
                                </p>
                                <a href="view-post?id=${post.id}" class="btn btn-sm btn-primary">Read Post</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<c:if test="${not empty sessionScope.loggedUser and sessionScope.loggedUser.id == profileUser.id}">
    <div class="modal fade" id="editProfileModal" tabindex="-1" aria-labelledby="editProfileModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <form action="edit-profile" method="POST">
              <div class="modal-header">
                <h5 class="modal-title" id="editProfileModalLabel">Edit Profile</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                  <div class="mb-3">
                      <label class="form-label">Profile Image URL</label>
                      <input type="url" name="profileImageUrl" class="form-control" value="${profileUser.profileImageUrl == 'default_avatar.png' ? '' : profileUser.profileImageUrl}" placeholder="https://example.com/my-picture.jpg">
                      <small class="text-muted">Leave blank for default avatar.</small>
                  </div>
                  <div class="mb-3">
                      <label class="form-label">Bio</label>
                      <textarea name="bio" class="form-control" rows="4">${profileUser.bio}</textarea>
                  </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-success">Save Changes</button>
              </div>
          </form>
        </div>
      </div>
    </div>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>