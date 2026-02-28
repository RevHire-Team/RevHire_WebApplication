

// ======================================
// STORAGE HELPERS
// ======================================
function getUsers() {
    return JSON.parse(localStorage.getItem("revhireUsers")) || [];
}

function saveUsers(users) {
    localStorage.setItem("revhireUsers", JSON.stringify(users));
}


// ======================================
// PASSWORD VALIDATION FUNCTION
// ======================================
function isValidPassword(password) {

    // Rule: min 6 chars, 1 number, 1 special
    let regex = /^(?=.*[0-9])(?=.*[!@#$%^&*]).{6,}$/;

    if (!regex.test(password)) {
        return false;
    }

    // Check sequential numbers & letters
    let sequences = [
        "123","234","345","456","567","678","789",
        "abc","bcd","cde","def","efg","fgh"
    ];

    for (let seq of sequences) {
        if (password.toLowerCase().includes(seq)) {
            return false;
        }
    }

    return true;
}



// ======================================
// JOBSEEKER REGISTRATION
// ======================================
document.getElementById("jobseekerRegisterForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    let name = document.getElementById("name").value.trim();
    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value.trim();
    let message = document.getElementById("registerMsg");

    message.innerText = "";

    if (!isValidPassword(password)) {
        message.className = "text-danger fw-bold";
        message.innerText =
            "Invalid Password! Must be minimum 6 characters, include 1 number, 1 special character and no sequential values.";
        return;
    }

    let users = getUsers();

    if (users.some(u => u.email === email)) {
        message.className = "text-danger fw-bold";
        message.innerText = "Email already registered!";
        return;
    }

    users.push({
        name: name,
        email: email,
        password: password,
        role: "JOBSEEKER"
    });

    saveUsers(users);

    message.className = "text-success fw-bold";
    message.innerText = "Registration of Job Seeker is Successful!";
});



// ======================================
// EMPLOYER REGISTRATION
// ======================================
document.getElementById("employerRegisterForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    let name = document.getElementById("employerName").value.trim();
    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value.trim();
    let message = document.getElementById("registerMsg");

    message.innerText = "";

    if (!isValidPassword(password)) {
        message.className = "text-danger fw-bold";
        message.innerText =
            "Invalid Password! Must be minimum 6 characters, include 1 number, 1 special character and no sequential values.";
        return;
    }

    let users = getUsers();

    if (users.some(u => u.email === email)) {
        message.className = "text-danger fw-bold";
        message.innerText = "Email already registered!";
        return;
    }

    users.push({
        name: name,
        email: email,
        password: password,
        role: "EMPLOYER"
    });

    saveUsers(users);

    message.className = "text-success fw-bold";
    message.innerText = "Registration of Employer is Successful!";
});



// ======================================
// LOGIN LOGIC
// ======================================
document.getElementById("loginForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value.trim();
    let message = document.getElementById("errorMsg");

    message.innerText = "";

    let users = getUsers();

    let user = users.find(u => u.email === email && u.password === password);

    if (!user) {
        message.className = "text-danger fw-bold";
        message.innerText = "Invalid login credentials!";
        return;
    }

    message.className = "text-success fw-bold";
    message.innerText = "Login Successful! Redirecting...";

    setTimeout(() => {
        if (user.role === "JOBSEEKER") {
            window.location.href = "../jobseeker/dashboard.html";
        } else {
            window.location.href = "../employer/dashboard.html";
        }
    }, 1000);
});



// ======================================
// FORGOT PASSWORD LOGIC
// ======================================
document.getElementById("forgotForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    let email = document.getElementById("resetEmail").value.trim();
    let newPassword = document.getElementById("newPassword").value.trim();
    let confirmPassword = document.getElementById("confirmPassword").value.trim();
    let message = document.getElementById("resetMsg");

    message.innerText = "";

    let users = getUsers();
    let userIndex = users.findIndex(u => u.email === email);

    if (userIndex === -1) {
        message.className = "text-danger fw-bold";
        message.innerText = "Invalid Email!";
        return;
    }

    if (!isValidPassword(newPassword)) {
        message.className = "text-danger fw-bold";
        message.innerText =
            "Invalid Password! Must be minimum 6 characters, include 1 number, 1 special character and no sequential values.";
        return;
    }

    if (newPassword !== confirmPassword) {
        message.className = "text-danger fw-bold";
        message.innerText = "Passwords do not match!";
        return;
    }

    users[userIndex].password = newPassword;
    saveUsers(users);

    message.className = "text-success fw-bold";
    message.innerText = "Password Reset Successful! Redirecting to login...";

    setTimeout(() => {
        window.location.href = "login.html";
    }, 1500);
});// ======================================
// STORAGE HELPERS
// ======================================
function getUsers() {
    return JSON.parse(localStorage.getItem("revhireUsers")) || [];
}

function saveUsers(users) {
    localStorage.setItem("revhireUsers", JSON.stringify(users));
}


// ======================================
// PASSWORD VALIDATION FUNCTION
// ======================================
function isValidPassword(password) {

    // Rule: min 6 chars, 1 number, 1 special
    let regex = /^(?=.*[0-9])(?=.*[!@#$%^&*]).{6,}$/;

    if (!regex.test(password)) {
        return false;
    }

    // Check sequential numbers & letters
    let sequences = [
        "123","234","345","456","567","678","789",
        "abc","bcd","cde","def","efg","fgh"
    ];

    for (let seq of sequences) {
        if (password.toLowerCase().includes(seq)) {
            return false;
        }
    }

    return true;
}



// ======================================
// JOBSEEKER REGISTRATION
// ======================================
document.getElementById("jobseekerRegisterForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    let name = document.getElementById("name").value.trim();
    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value.trim();
    let message = document.getElementById("registerMsg");

    message.innerText = "";

    if (!isValidPassword(password)) {
        message.className = "text-danger fw-bold";
        message.innerText =
            "Invalid Password! Must be minimum 6 characters, include 1 number, 1 special character and no sequential values.";
        return;
    }

    let users = getUsers();

    let alreadyRegistered = users.find(
        u => u.email === email && u.role === "JOBSEEKER"
    );

    if (alreadyRegistered) {
        message.className = "text-danger fw-bold";
        message.innerText = "Jobseeker is registered!";
        return;
    }

    users.push({
        name: name,
        email: email,
        password: password,
        role: "JOBSEEKER"
    });

    saveUsers(users);

    message.className = "text-success fw-bold";
    message.innerText = "Registration of Job Seeker is Successful!";
});

// ======================================
// EMPLOYER REGISTRATION
// ======================================
document.getElementById("employerRegisterForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    let name = document.getElementById("employerName").value.trim();
    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value.trim();
    let message = document.getElementById("registerMsg");

    message.innerText = "";

    if (!isValidPassword(password)) {
        message.className = "text-danger fw-bold";
        message.innerText =
            "Invalid Password! Must be minimum 6 characters, include 1 number, 1 special character and no sequential values.";
        return;
    }

    let users = getUsers();

    let alreadyRegistered = users.find(
        u => u.email === email && u.role === "EMPLOYER"
    );

    if (alreadyRegistered) {
        message.className = "text-danger fw-bold";
        message.innerText = "Employer is registered!";
        return;
    }

    users.push({
        name: name,
        email: email,
        password: password,
        role: "EMPLOYER"
    });

    saveUsers(users);

    message.className = "text-success fw-bold";
    message.innerText = "Registration of Employer is Successful!";
});
// ======================================
// LOGIN LOGIC
// ======================================
document.getElementById("loginForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value.trim();
    let message = document.getElementById("errorMsg");

    message.innerText = "";

    let users = getUsers();

    let user = users.find(u => u.email === email && u.password === password);

    if (!user) {
        message.className = "text-danger fw-bold";
        message.innerText = "Invalid login credentials!";
        return;
    }

    message.className = "text-success fw-bold";
    message.innerText = "Login Successful! Redirecting...";

    setTimeout(() => {
        if (user.role === "JOBSEEKER") {
            window.location.href = "../jobseeker/dashboard.html";
        } else {
            window.location.href = "../employer/dashboard.html";
        }
    }, 1000);
});



// ======================================
// FORGOT PASSWORD LOGIC
// ======================================
document.getElementById("forgotForm")?.addEventListener("submit", function (e) {
    e.preventDefault();

    let email = document.getElementById("resetEmail").value.trim();
    let newPassword = document.getElementById("newPassword").value.trim();
    let confirmPassword = document.getElementById("confirmPassword").value.trim();
    let message = document.getElementById("resetMsg");

    message.innerText = "";

    let users = getUsers();
    let userIndex = users.findIndex(u => u.email === email);

    if (userIndex === -1) {
        message.className = "text-danger fw-bold";
        message.innerText = "Invalid Email!";
        return;
    }

    if (!isValidPassword(newPassword)) {
        message.className = "text-danger fw-bold";
        message.innerText =
            "Invalid Password! Must be minimum 6 characters, include 1 number, 1 special character and no sequential values.";
        return;
    }

    if (newPassword !== confirmPassword) {
        message.className = "text-danger fw-bold";
        message.innerText = "Passwords do not match!";
        return;
    }

    users[userIndex].password = newPassword;
    saveUsers(users);

    message.className = "text-success fw-bold";
    message.innerText = "Password Reset Successful! Redirecting to login...";

    setTimeout(() => {
        window.location.href = "login.html";
    }, 1500);
});



