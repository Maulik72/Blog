<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - Advanced Blog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h3 class="text-center mb-4">Create an Account</h3>

                    <%-- Error Message Display --%>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <form action="register" method="POST" class="needs-validation" novalidate>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Username</label>
                            <input type="text" name="username" class="form-control bg-light"
                                   required minlength="3" maxlength="20"
                                   pattern="^[a-zA-Z0-9_]+$">
                            <div class="invalid-feedback">
                                Username must be 3-20 characters long and contain NO spaces or special characters (only letters, numbers, and underscores).
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Email address</label>
                            <input type="email" name="email" class="form-control bg-light" required>
                            <div class="invalid-feedback">
                                Please enter a valid email address format (e.g., name@example.com).
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Password</label>
                            <input type="password" name="password" class="form-control bg-light"
                                   required minlength="6" maxlength="50"
                                   pattern="^\S+$">
                            <div class="invalid-feedback">
                                Password must be at least 6 characters long and cannot contain any spaces.
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 fw-bold py-2 mt-2">Register</button>
                    </form>

                    <div class="text-center mt-3">
                        <a href="login">Already have an account? Login here.</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    (() => {
      'use strict'
      const forms = document.querySelectorAll('.needs-validation')
      Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
          if (!form.checkValidity()) {
            event.preventDefault()
            event.stopPropagation()
          }
          form.classList.add('was-validated')
        }, false)
      })
    })()
</script>

</body>
</html>