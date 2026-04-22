<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search Results - Advanced Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        .content-truncate { display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
        .card-hover:hover { transform: translateY(-3px); box-shadow: 0 8px 15px rgba(0,0,0,0.1) !important; transition: 0.2s; }
    </style>
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-5 shadow-sm sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold" href="home"><i class="bi bi-journal-code"></i> Advanced Blog</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle ${not empty activeCategoryId ? 'active fw-bold text-white' : ''}" href="#" id="categoryDropdown" role="button" data-bs-toggle="dropdown">
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
                    <ul class="dropdown-menu shadow-sm border-0 mt-2">
                        <li><a class="dropdown-item ${empty activeCategoryId ? 'active fw-bold' : ''}" href="search?q=${searchQuery}"><i class="bi bi-collection"></i> All Results</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <c:forEach var="cat" items="${categories}">
                            <li><a class="dropdown-item ${activeCategoryId == cat.id ? 'active fw-bold' : ''}" href="search?q=${searchQuery}&categoryId=${cat.id}">${cat.name}</a></li>
                        </c:forEach>
                    </ul>
                </li>
            </ul>

            <form class="d-flex mx-lg-3 flex-grow-1" style="max-width: 400px;" action="search" method="GET">
                <div class="input-group">
                    <input class="form-control border-0 bg-light" type="search" name="q" value="${searchQuery}" aria-label="Search" required>
                    <button class="btn btn-light bg-white border-0" type="submit"><i class="bi bi-search text-muted"></i></button>
                </div>
            </form>

            <div class="d-flex align-items-center mt-3 mt-lg-0">
                <a href="home" class="btn btn-outline-light btn-sm fw-semibold"><i class="bi bi-arrow-left"></i> Back to Home</a>
            </div>
        </div>
    </div>
</nav>

<div class="container mb-5" style="max-width: 900px;">

    <div class="mb-4 text-center">
        <h2 class="fw-bold">Search Results for "<span class="text-primary">${searchQuery}</span>"</h2>
        <p class="text-muted">Found ${totalPostCount} matching posts and ${fn:length(userResults)} people.</p>
    </div>

    <ul class="nav nav-pills mb-4 justify-content-center" id="searchTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active px-4 fw-bold" id="posts-tab" data-bs-toggle="pill" data-bs-target="#posts" type="button" role="tab">
                <i class="bi bi-journal-text"></i> Posts (${totalPostCount})
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link px-4 fw-bold" id="people-tab" data-bs-toggle="pill" data-bs-target="#people" type="button" role="tab">
                <i class="bi bi-people"></i> People (${fn:length(userResults)})
            </button>
        </li>
    </ul>

    <div class="tab-content" id="searchTabsContent">

        <div class="tab-pane fade show active" id="posts" role="tabpanel">
            <c:choose>
                <c:when test="${empty postResults}">
                    <div class="alert alert-secondary text-center py-4 rounded-4"><i class="bi bi-search fs-2 d-block mb-2"></i>No posts matched your search filters.</div>
                </c:when>
                <c:otherwise>
                    <div class="row g-4">
                        <c:forEach var="post" items="${postResults}">
                            <div class="col-md-6">
                                <div class="card h-100 border-0 shadow-sm card-hover rounded-4">
                                    <div class="card-body p-4 d-flex flex-column">
                                        <div class="mb-2">
                                            <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 rounded-pill px-2 py-1">
                                                <i class="bi bi-folder2"></i> ${not empty post.categoryName ? post.categoryName : 'Uncategorized'}
                                            </span>
                                        </div>
                                        <h5 class="fw-bold mb-2">${post.title}</h5>
                                        <div class="text-muted small mb-3">
                                            <i class="bi bi-person"></i> ${post.authorName} • ${post.createdAt}
                                        </div>
                                        <p class="text-secondary content-truncate small">${post.content}</p>
                                        <a href="view-post?id=${post.id}" class="btn btn-outline-primary btn-sm w-100 fw-bold mt-auto">Read Post</a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <c:set var="urlParams" value="&q=${searchQuery}${not empty activeCategoryId ? '&categoryId=' += activeCategoryId : ''}" />
                    <c:if test="${noOfPages > 1}">
                        <nav aria-label="Search results navigation" class="mt-5 pt-3">
                            <ul class="pagination justify-content-center shadow-sm" style="border-radius: 0.5rem; overflow: hidden; display: inline-flex; margin: 0 auto;">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link px-4 py-2" href="search?page=${currentPage - 1}${urlParams}">Previous</a>
                                </li>
                                <c:forEach begin="1" end="${noOfPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link px-3 py-2 ${currentPage == i ? 'fw-bold' : ''}" href="search?page=${i}${urlParams}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == noOfPages ? 'disabled' : ''}">
                                    <a class="page-link px-4 py-2" href="search?page=${currentPage + 1}${urlParams}">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="tab-pane fade" id="people" role="tabpanel">
            <c:choose>
                <c:when test="${empty userResults}">
                    <div class="alert alert-secondary text-center py-4 rounded-4"><i class="bi bi-person-x fs-2 d-block mb-2"></i>No users matched your search.</div>
                </c:when>
                <c:otherwise>
                    <div class="row g-4">
                        <c:forEach var="user" items="${userResults}">
                            <div class="col-md-6">
                                <div class="card border-0 shadow-sm card-hover rounded-4 h-100">
                                    <div class="card-body p-4 d-flex align-items-center">
                                        <img src="${user.profileImageUrl == 'default_avatar.png' ? 'https://ui-avatars.com/api/?name=' += user.username += '&size=64&background=random' : user.profileImageUrl}"
                                             class="rounded-circle me-3" style="width: 64px; height: 64px; object-fit: cover;">
                                        <div>
                                            <h5 class="fw-bold mb-1">${user.username}</h5>
                                            <p class="text-muted small mb-2 content-truncate" style="-webkit-line-clamp: 2;">
                                                ${empty user.bio ? 'No bio available.' : user.bio}
                                            </p>
                                            <a href="profile?id=${user.id}" class="btn btn-primary btn-sm fw-bold">View Profile</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>