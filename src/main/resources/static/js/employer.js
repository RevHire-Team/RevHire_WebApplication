/* =====================================================
REVHIRE EMPLOYER MODULE
Professional Shared Script
===================================================== */


/* ================= DATABASE INIT ================= */

if (!localStorage.getItem("jobs")) {
    localStorage.setItem("jobs", JSON.stringify([]));
}

if (!localStorage.getItem("applications")) {
    localStorage.setItem("applications", JSON.stringify([]));
}

if (!localStorage.getItem("employerProfile")) {
    localStorage.setItem("employerProfile", JSON.stringify({
        companyName: "",
        industry: "",
        size: "",
        website: "",
        location: "",
        description: ""
    }));
}

if (!localStorage.getItem("employerNotifications")) {
    localStorage.setItem("employerNotifications", JSON.stringify([]));
}

if (!localStorage.getItem("jobSeekerNotifications")) {
    localStorage.setItem("jobSeekerNotifications", JSON.stringify([]));
}


/* =====================================================
HELPERS
===================================================== */

function getJobs() {
    return JSON.parse(localStorage.getItem("jobs")) || [];
}

function saveJobs(jobs) {
    localStorage.setItem("jobs", JSON.stringify(jobs));
}

function getApplications() {
    return JSON.parse(localStorage.getItem("applications")) || [];
}

function saveApplications(apps) {
    localStorage.setItem("applications", JSON.stringify(apps));
}

function getEmployerProfile() {
    return JSON.parse(localStorage.getItem("employerProfile")) || {};
}

function saveEmployerProfile(profile) {
    localStorage.setItem("employerProfile", JSON.stringify(profile));
}

function generateId() {
    return Date.now();
}


/* =====================================================
COUNTER ANIMATION (PROFESSIONAL DASHBOARD)
===================================================== */

function animateCounter(elementId, target) {

    let element = document.getElementById(elementId);

    if (!element) return;

    let current = 0;

    let increment = Math.ceil(target / 30);

    if (increment < 1) increment = 1;

    let interval = setInterval(function () {

        current += increment;

        if (current >= target) {
            current = target;
            clearInterval(interval);
        }

        element.innerText = current;

    }, 20);
}


/* =====================================================
EMPLOYER NOTIFICATIONS
===================================================== */

function getEmployerNotifications() {
    return JSON.parse(localStorage.getItem("employerNotifications")) || [];
}

function saveEmployerNotifications(list) {
    localStorage.setItem("employerNotifications", JSON.stringify(list));
}

function addEmployerNotification(message) {

    let notifications = getEmployerNotifications();

    notifications.push({
        message: message,
        date: new Date().toLocaleDateString(),
        time: new Date().toLocaleTimeString(),
        read: false
    });

    saveEmployerNotifications(notifications);

    updateEmployerBadge();
}

function updateEmployerBadge() {

    let notifications = getEmployerNotifications();

    let unread = notifications.filter(n => !n.read).length;

    let badge = document.getElementById("notificationCount");

    if (!badge) return;

    if (unread > 0) {

        badge.style.display = "inline-block";
        badge.innerText = unread;

    } else {

        badge.style.display = "none";

    }
}


/* =====================================================
JOB SEEKER NOTIFICATIONS (WHEN EMPLOYER TAKES ACTION)
===================================================== */

function addJobSeekerNotification(message) {

    let list =
        JSON.parse(localStorage.getItem("jobSeekerNotifications")) || [];

    list.push({

        message: message,
        date: new Date().toLocaleDateString(),
        time: new Date().toLocaleTimeString(),
        read: false

    });

    localStorage.setItem(
        "jobSeekerNotifications",
        JSON.stringify(list)
    );
}


/* =====================================================
DASHBOARD
===================================================== */

function loadDashboard() {

    let jobs = getJobs();

    let apps = getApplications();

    let totalJobs = jobs.length;

    let activeJobs =
        jobs.filter(j => j.status === "OPEN").length;

    let totalApps = apps.length;

    let shortlisted =
        apps.filter(a => a.status === "SHORTLISTED").length;

    let pending =
        apps.filter(a => a.status === "APPLIED").length;


    animateCounter("totalJobs", totalJobs);

    animateCounter("activeJobs", activeJobs);

    animateCounter("totalApps", totalApps);

    animateCounter("shortlisted", shortlisted);

    animateCounter("pendingReviews", pending);
}


/* =====================================================
CREATE JOB
===================================================== */

function createJob(event) {

    event.preventDefault();

    let job = {

        id: generateId(),

        title: title.value.trim(),

        description: description.value.trim(),

        skills: skills.value.trim(),

        experience: experience.value.trim(),

        education: education.value.trim(),

        location: location.value.trim(),

        salary: salary.value.trim(),

        type: type.value.trim(),

        deadline: deadline.value,

        status: "OPEN",

        postedDate: new Date().toLocaleDateString()

    };

    let jobs = getJobs();

    jobs.push(job);

    saveJobs(jobs);

    addEmployerNotification("New job posted: " + job.title);

    alert("Job Posted Successfully!");

    window.location.href = "manage-jobs.html";
}


/* =====================================================
LOAD JOBS TABLE
===================================================== */

function loadJobs() {

    let jobs = getJobs();

    let table = document.getElementById("jobTable");

    if (!table) return;

    table.innerHTML = "";

    if (jobs.length === 0) {

        table.innerHTML =
            "<tr><td colspan='4' class='text-center text-muted'>No jobs posted yet</td></tr>";

        return;
    }

    jobs.forEach(job => {

        table.innerHTML += `

        <tr>

        <td>${job.title}</td>

        <td>${job.status}</td>

        <td>${job.deadline}</td>

        <td>

        <button class="btn btn-info btn-sm"
        onclick="viewJob(${job.id})">
        View
        </button>

        <button class="btn btn-warning btn-sm"
        onclick="editJob(${job.id})">
        Edit
        </button>

        <button class="btn btn-secondary btn-sm"
        onclick="toggleJobStatus(${job.id})">
        ${job.status === "OPEN" ? "Close" : "Reopen"}
        </button>

        <button class="btn btn-danger btn-sm"
        onclick="deleteJob(${job.id})">
        Delete
        </button>

        </td>

        </tr>`;
    });
}


/* =====================================================
JOB STATUS CHANGE
===================================================== */

function toggleJobStatus(id) {

    let jobs = getJobs();

    let job = jobs.find(j => j.id == id);

    if (!job) return;

    job.status =
        job.status === "OPEN"
            ? "CLOSED"
            : "OPEN";

    saveJobs(jobs);

    loadJobs();

    addEmployerNotification(
        `Job "${job.title}" status changed to ${job.status}`
    );
}


/* =====================================================
DELETE JOB
===================================================== */

function deleteJob(id) {

    if (!confirm("Delete this job?")) return;

    let jobs = getJobs().filter(j => j.id !== id);

    saveJobs(jobs);

    loadJobs();

    addEmployerNotification("A job was deleted");
}


/* =====================================================
VIEW JOB
===================================================== */

function viewJob(id) {

    localStorage.setItem("viewJobId", id);

    window.location.href = "job-details.html";
}


/* =====================================================
EDIT JOB
===================================================== */

function editJob(id) {

    localStorage.setItem("editJobId", id);

    window.location.href = "edit-job.html";
}


/* =====================================================
LOAD APPLICATIONS TABLE
===================================================== */

function loadApplications() {

    let apps = getApplications();

    let table = document.getElementById("appTable");

    if (!table) return;

    table.innerHTML = "";

    if (apps.length === 0) {

        table.innerHTML =
            "<tr><td colspan='6' class='text-center text-muted'>No applications yet</td></tr>";

        return;
    }

    apps.forEach((app, index) => {

        let color =
            app.status === "SHORTLISTED"
                ? "success"
                : app.status === "REJECTED"
                    ? "danger"
                    : "secondary";

        table.innerHTML += `

        <tr>

        <td>${app.name}</td>

        <td>${app.skills}</td>

        <td>${app.education}</td>

        <td>${app.experience}</td>

        <td>
        <span class="badge bg-${color}">
        ${app.status}
        </span>
        </td>

        <td>

        ${
            app.status === "APPLIED"
                ? `
        <button class="btn btn-success btn-sm me-1"
        onclick="shortlistApplication(${index})">
        Shortlist
        </button>

        <button class="btn btn-danger btn-sm"
        onclick="rejectApplication(${index})">
        Reject
        </button>`
                : "-"
        }

        </td>

        </tr>`;
    });
}


/* =====================================================
SHORTLIST APPLICATION
===================================================== */

function shortlistApplication(index) {

    let apps = getApplications();

    apps[index].status = "SHORTLISTED";

    saveApplications(apps);

    addEmployerNotification(
        "You shortlisted " + apps[index].name
    );

    addJobSeekerNotification(
        "You were shortlisted for " + apps[index].title
    );

    loadApplications();

    updateEmployerBadge();
}


/* =====================================================
REJECT APPLICATION
===================================================== */

function rejectApplication(index) {

    let apps = getApplications();

    apps[index].status = "REJECTED";

    saveApplications(apps);

    addEmployerNotification(
        "You rejected " + apps[index].name
    );

    addJobSeekerNotification(
        "Your application was rejected for " + apps[index].title
    );

    loadApplications();

    updateEmployerBadge();
}


/* =====================================================
INIT
===================================================== */

document.addEventListener("DOMContentLoaded", function () {

    loadDashboard();

    loadJobs();

    loadApplications();

    updateEmployerBadge();

});