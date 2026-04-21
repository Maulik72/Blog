<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>${post.title} - Advanced Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        /* Small custom tweak for readability */
        .blog-container { max-width: 800px; margin: auto; }
        .post-content { font-size: 1.1rem; line-height: 1.7; white-space: pre-line; }
    </style>
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4 shadow-sm">
    <div class="container">
        <a class="navbar-brand" href="home"><i class="bi bi-arrow-left"></i> Back to Home</a>
    </div>
</nav>

<div class="container blog-container mb-5">

    <div class="card p-4 p-md-5 shadow-sm border-0 mb-4">

        <div class="d-flex justify-content-between align-items-start mb-2">
            <h1 class="fw-bold mb-0">${post.title}</h1>

            <c:if test="${not empty sessionScope.loggedUser and sessionScope.loggedUser.id == post.userId}">
                <div class="d-flex gap-2 ms-3">
                    <a href="edit-post?id=${post.id}" class="btn btn-warning btn-sm shadow-sm">
                        <i class="bi bi-pencil-square"></i> Edit
                    </a>

                    <form action="${pageContext.request.contextPath}/delete-post" method="POST" class="m-0" onsubmit="return confirm('Are you sure you want to delete this post? This action cannot be undone.');">
                        <input type="hidden" name="id" value="${post.id}">
                        <button type="submit" class="btn btn-danger btn-sm shadow-sm">
                            <i class="bi bi-trash"></i> Delete
                        </button>
                    </form>
                </div>
            </c:if>
        </div>

        <p class="text-muted mb-4">
            <i class="bi bi-person-circle"></i> By <a href="profile?id=${post.userId}" class="text-decoration-none fw-semibold">${post.authorName}</a>
            | <i class="bi bi-calendar3"></i> Published: ${post.createdAt}
        </p>

        <div class="post-content mb-4 text-dark">
            ${post.content}
        </div>

        <hr>

        <div class="d-flex align-items-center gap-3 mt-3">
            <form action="${pageContext.request.contextPath}/react-post" method="POST" class="m-0">
                <input type="hidden" name="postId" value="${post.id}">
                <input type="hidden" name="action" value="LIKE">
                <button type="submit" class="btn ${post.currentUserReaction == 'LIKE' ? 'btn-success' : 'btn-outline-success'} rounded-pill px-3">
                    <i class="bi bi-hand-thumbs-up-fill"></i> ${post.likesCount}
                </button>
            </form>

            <form action="${pageContext.request.contextPath}/react-post" method="POST" class="m-0">
                <input type="hidden" name="postId" value="${post.id}">
                <input type="hidden" name="action" value="DISLIKE">
                <button type="submit" class="btn ${post.currentUserReaction == 'DISLIKE' ? 'btn-danger' : 'btn-outline-danger'} rounded-pill px-3">
                    <i class="bi bi-hand-thumbs-down-fill"></i> ${post.dislikesCount}
                </button>
            </form>
        </div>
    </div> <c:if test="${not empty sessionScope.loggedUser}">
        <div class="card p-4 shadow-sm border-0 mb-4 bg-white">
            <h5 class="mb-3"><i class="bi bi-chat-left-text"></i> Leave a comment</h5>
            <form action="add-comment" method="POST">
                <input type="hidden" name="postId" value="${post.id}">
                <textarea name="content" class="form-control bg-light" rows="3" required placeholder="What are your thoughts on this post?"></textarea>
                <div class="d-flex justify-content-end mt-3">
                    <button class="btn btn-primary px-4">Post Comment</button>
                </div>
            </form>
        </div>
    </c:if>

    <div>
        <h4 class="mb-3">Comments (${fn:length(comments)})</h4>

        <c:choose>
            <c:when test="${empty comments}">
                <p class="text-muted fst-italic">No comments yet. Be the first to share your thoughts!</p>
            </c:when>
            <c:otherwise>
                <div id="comments-container">
                    <c:forEach var="c" items="${comments}" varStatus="status">

                        <div class="card mb-3 border-0 shadow-sm comment-item ${status.index >= 3 ? 'd-none' : ''}">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <strong class="text-primary">${c.username}</strong>
                                    <small class="text-muted">${c.createdAt}</small>
                                </div>
                                <p class="mb-0 text-dark">${c.content}</p>
                            </div>
                        </div>

                    </c:forEach>
                </div>

                <c:if test="${fn:length(comments) > 3}">
                    <button id="loadMoreBtn" class="btn btn-outline-primary w-100 py-2 mt-2 fw-semibold" onclick="showAllComments()">
                        View all ${fn:length(comments)} comments <i class="bi bi-chevron-down"></i>
                    </button>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>

</div>

<script>
    function showAllComments() {
        const hiddenComments = document.querySelectorAll('.comment-item.d-none');
        hiddenComments.forEach(comment => {
            comment.classList.remove('d-none');
        });
        document.getElementById('loadMoreBtn').style.display = 'none';
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>