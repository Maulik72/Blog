<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>${profileUser.username}'s Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        .card-hover:hover { transform: translateY(-3px); box-shadow: 0 8px 15px rgba(0,0,0,0.1) !important; transition: 0.2s; }
        .content-truncate { display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
        /* Makes the follower stats look clickable */
        .stat-box { cursor: pointer; transition: all 0.2s ease-in-out; border-radius: 8px; padding: 5px 15px; }
        .stat-box:hover { background-color: #f8f9fa; transform: translateY(-2px); }
    </style>
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4 shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="home"><i class="bi bi-arrow-left"></i> Back to Home</a>
    </div>
</nav>

<div class="container mb-5" style="max-width: 900px;">

    <div class="card p-4 p-md-5 shadow-sm text-center border-0 rounded-4 mb-4">
        <img src="${profileUser.profileImageUrl == 'default_avatar.png' ? 'https://ui-avatars.com/api/?name=' += profileUser.username += '&size=128&background=random' : profileUser.profileImageUrl}"
             alt="Profile Image" class="rounded-circle mx-auto d-block mb-3 shadow-sm border border-3 border-white" style="width: 120px; height: 120px; object-fit: cover;">

        <h2 class="fw-bold">${profileUser.username} <span class="badge bg-secondary fs-6 align-middle ms-2">${profileUser.role}</span></h2>

        <p class="text-muted mt-2" style="max-width: 600px; margin: 0 auto;">
            ${empty profileUser.bio ? 'This user has no bio yet.' : profileUser.bio}
        </p>

        <div class="d-flex justify-content-center gap-2 my-3">
            <div class="stat-box" data-bs-toggle="modal" data-bs-target="#followersModal">
                <strong class="fs-5">${followersCount}</strong> <span class="text-muted">Followers</span>
            </div>
            <div class="stat-box" data-bs-toggle="modal" data-bs-target="#followingModal">
                <strong class="fs-5">${followingCount}</strong> <span class="text-muted">Following</span>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty sessionScope.loggedUser and sessionScope.loggedUser.id == profileUser.id}">
                <button class="btn btn-outline-primary btn-sm mt-2 rounded-pill px-4" data-bs-toggle="modal" data-bs-target="#editProfileModal">
                    <i class="bi bi-pencil"></i> Edit Profile
                </button>
            </c:when>
            <c:when test="${not empty sessionScope.loggedUser}">
                <form action="${pageContext.request.contextPath}/toggle-follow" method="POST" class="mt-2">
                    <input type="hidden" name="targetUserId" value="${profileUser.id}">
                    <button type="submit" class="btn ${isFollowing ? 'btn-secondary' : 'btn-primary'} btn-sm rounded-pill px-4 fw-bold">
                        <i class="bi ${isFollowing ? 'bi-person-check-fill' : 'bi-person-plus-fill'}"></i>
                        ${isFollowing ? 'Following' : 'Follow'}
                    </button>
                </form>
            </c:when>
        </c:choose>
    </div>

    <ul class="nav nav-pills mb-4 justify-content-center" id="profileTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active px-4 fw-bold" id="posts-tab" data-bs-toggle="pill" data-bs-target="#posts" type="button" role="tab">
                <i class="bi bi-journal-text"></i> Posts (${fn:length(userPosts)})
            </button>
        </li>
        <c:if test="${not empty sessionScope.loggedUser and sessionScope.loggedUser.id == profileUser.id}">
            <li class="nav-item" role="presentation">
                <button class="nav-link px-4 fw-bold" id="saved-tab" data-bs-toggle="pill" data-bs-target="#saved" type="button" role="tab">
                    <i class="bi bi-bookmark-fill"></i> Saved (${fn:length(savedPosts)})
                </button>
            </li>
        </c:if>
    </ul>

    <div class="tab-content" id="profileTabsContent">
        <div class="tab-pane fade show active" id="posts" role="tabpanel">
            <c:choose>
                <c:when test="${empty userPosts}">
                    <div class="alert alert-secondary text-center py-4 rounded-4"><i class="bi bi-inbox fs-2 d-block mb-2"></i>No posts written yet.</div>
                </c:when>
                <c:otherwise>
                    <div class="row g-4">
                        <c:forEach var="post" items="${userPosts}">
                            <div class="col-md-6">
                                <div class="card shadow-sm h-100 border-0 card-hover rounded-4">
                                    <div class="card-body p-4 d-flex flex-column">
                                        <h5 class="card-title fw-bold">${post.title}</h5>
                                        <p class="card-text text-muted small mb-3"><i class="bi bi-calendar3"></i> Published: ${post.createdAt}</p>
                                        <p class="text-secondary content-truncate small">${post.content}</p>
                                        <a href="view-post?id=${post.id}" class="btn btn-outline-primary btn-sm mt-auto w-100 fw-bold">Read Post</a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${not empty sessionScope.loggedUser and sessionScope.loggedUser.id == profileUser.id}">
            <div class="tab-pane fade" id="saved" role="tabpanel">
                <c:choose>
                    <c:when test="${empty savedPosts}">
                        <div class="alert alert-secondary text-center py-4 rounded-4"><i class="bi bi-bookmark fs-2 d-block mb-2"></i>You haven't saved any posts yet.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="row g-4">
                            <c:forEach var="post" items="${savedPosts}">
                                <div class="col-md-6">
                                    <div class="card shadow-sm h-100 border-0 card-hover rounded-4">
                                        <div class="card-body p-4 d-flex flex-column">
                                            <div class="mb-2">
                                                <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 rounded-pill px-2 py-1">
                                                    <i class="bi bi-folder2"></i> ${not empty post.categoryName ? post.categoryName : 'Uncategorized'}
                                                </span>
                                            </div>
                                            <h5 class="card-title fw-bold">${post.title}</h5>
                                            <p class="card-text text-muted small mb-3">By ${post.authorName} • <i class="bi bi-calendar3"></i> ${post.createdAt}</p>
                                            <a href="view-post?id=${post.id}" class="btn btn-outline-warning btn-sm mt-auto w-100 fw-bold text-dark">Read Saved Post</a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>

    <c:if test="${not empty sessionScope.loggedUser and sessionScope.loggedUser.id == profileUser.id}">
        <div class="mt-5 border-top pt-4 pb-3 text-center">
            <h5 class="text-danger fw-bold"><i class="bi bi-exclamation-triangle-fill"></i> Danger Zone</h5>
            <p class="text-muted small">Once you delete your account, there is no going back. Please be certain.</p>
            <button type="button" class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteAccountModal">
                Delete My Account
            </button>
        </div>
    </c:if>

</div>

<div class="modal fade" id="followersModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold">Followers</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-0">
                <div class="list-group list-group-flush">
                    <c:choose>
                        <c:when test="${empty followersList}">
                            <div class="text-center p-4 text-muted">No followers yet.</div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="user" items="${followersList}">
                                <div class="list-group-item d-flex align-items-center p-3">
                                    <img src="${user.profileImageUrl == 'default_avatar.png' ? 'https://ui-avatars.com/api/?name=' += user.username += '&size=48&background=random' : user.profileImageUrl}"
                                         class="rounded-circle me-3" style="width: 48px; height: 48px; object-fit: cover;">
                                    <div class="flex-grow-1">
                                        <h6 class="mb-0 fw-bold">${user.username}</h6>
                                    </div>
                                    <a href="profile?id=${user.id}" class="btn btn-outline-primary btn-sm rounded-pill px-3">View</a>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="followingModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold">Following</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-0">
                <div class="list-group list-group-flush">
                    <c:choose>
                        <c:when test="${empty followingList}">
                            <div class="text-center p-4 text-muted">Not following anyone yet.</div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="user" items="${followingList}">
                                <div class="list-group-item d-flex align-items-center p-3">
                                    <img src="${user.profileImageUrl == 'default_avatar.png' ? 'https://ui-avatars.com/api/?name=' += user.username += '&size=48&background=random' : user.profileImageUrl}"
                                         class="rounded-circle me-3" style="width: 48px; height: 48px; object-fit: cover;">
                                    <div class="flex-grow-1">
                                        <h6 class="mb-0 fw-bold">${user.username}</h6>
                                    </div>
                                    <a href="profile?id=${user.id}" class="btn btn-outline-primary btn-sm rounded-pill px-3">View</a>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<c:if test="${not empty sessionScope.loggedUser and sessionScope.loggedUser.id == profileUser.id}">
    <div class="modal fade" id="editProfileModal" tabindex="-1" aria-labelledby="editProfileModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content border-0 shadow">
          <form action="edit-profile" method="POST">
              <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold" id="editProfileModalLabel"><i class="bi bi-pencil-square"></i> Edit Profile</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body p-4">
                  <div class="mb-4">
                      <label class="form-label fw-bold">Profile Image URL</label>
                      <input type="url" name="profileImageUrl" class="form-control bg-light" value="${profileUser.profileImageUrl == 'default_avatar.png' ? '' : profileUser.profileImageUrl}" placeholder="https://example.com/my-picture.jpg">
                      <div class="form-text">Leave blank to use the default generated avatar.</div>
                  </div>
                  <div class="mb-3">
                      <label class="form-label fw-bold">Bio</label>
                      <textarea name="bio" class="form-control bg-light" rows="4" placeholder="Tell the world about yourself...">${profileUser.bio}</textarea>
                  </div>
              </div>
              <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary px-4 fw-bold">Save Changes</button>
              </div>
          </form>
        </div>
      </div>
    </div>

    <div class="modal fade" id="deleteAccountModal" tabindex="-1" aria-labelledby="deleteAccountModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-danger">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title fw-bold" id="deleteAccountModalLabel">Confirm Account Deletion</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p class="mb-1">Are you sure you want to permanently delete your account, <strong>${loggedUser.username}</strong>?</p>
            <p class="text-danger small mb-0"><i class="bi bi-exclamation-triangle"></i> This action cannot be undone. All your posts, comments, and interactions will be permanently erased.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <form action="delete-account" method="POST" class="m-0">
                <button type="submit" class="btn btn-danger fw-bold">Confirm Delete</button>
            </form>
          </div>
        </div>
      </div>
    </div>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>