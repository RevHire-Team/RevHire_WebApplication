/* ===========================
JOB SEEKER COMMON SCRIPT
RevHire Job Portal
FINAL COMPLETE VERSION
=========================== */

document.addEventListener("DOMContentLoaded", function () {

    console.log("RevHire JobSeeker Module Loaded");

    loadProfileInitial();
    loadNotificationBadge();

});


/* ===========================
LOCAL STORAGE HELPERS
=========================== */

function getProfile() {
    return JSON.parse(localStorage.getItem("profile")) || {};
}

function getResume() {
    return JSON.parse(localStorage.getItem("resume")) || {};
}

function getApplications() {
    return JSON.parse(localStorage.getItem("applications")) || [];
}

function getSavedJobs() {
    return JSON.parse(localStorage.getItem("savedJobs")) || [];
}

function getNotifications() {
    return JSON.parse(localStorage.getItem("jobSeekerNotifications")) || [];
}

function saveNotifications(notifications) {
    localStorage.setItem(
        "jobSeekerNotifications",
        JSON.stringify(notifications)
    );
}


/* ===========================
PROFILE INITIAL
=========================== */

function loadProfileInitial(){

    let profile = getProfile();
    let element = document.getElementById("profileInitial");

    if(!element) return;

    element.innerText =
    profile.name && profile.name.trim() !== ""
        ? profile.name.charAt(0).toUpperCase()
        : "U";

}


/* ===========================
NOTIFICATION BADGE
=========================== */

function loadNotificationBadge(){

    let notifications = getNotifications();
    let badge = document.getElementById("notificationCount");

    if(!badge) return;

    let unreadCount =
    notifications.filter(n => n.read !== true).length;

    if(unreadCount > 0){

        badge.innerText = unreadCount;
        badge.style.display = "inline-block";

    }else{

        badge.style.display = "none";

    }

}


/* ===========================
ADD NOTIFICATION
=========================== */

function addNotification(message){

    let notifications = getNotifications();

    notifications.push({

        message: message,
        date: new Date().toLocaleDateString(),
        time: new Date().toLocaleTimeString(),
        read: false

    });

    saveNotifications(notifications);
    loadNotificationBadge();

}


/* ===========================
PROFILE COMPLETION
=========================== */

function calculateProfileCompletion(){

    let profile = getProfile();

    let fields = [
        profile.name,
        profile.email,
        profile.phone,
        profile.location,
        profile.education,
        profile.experience,
        profile.skills
    ];

    let filled =
    fields.filter(f => f && f.trim() !== "").length;

    return Math.round(
        (filled / fields.length) * 100
    ) || 0;

}


/* ===========================
RESUME SCORE
=========================== */

function calculateResumeScore(){

    let resume = getResume();

    let fields = [
        resume.objective,
        resume.education,
        resume.experience,
        resume.skills,
        resume.projects,
        resume.certifications
    ];

    let filled =
    fields.filter(f => f && f.trim() !== "").length;

    return Math.round(
        (filled / fields.length) * 100
    ) || 0;

}


/* ===========================
APPLICATION STATS
=========================== */

function loadApplicationStats(){

    let applications = getApplications();

    let total =
    document.getElementById("totalApps");

    let shortlisted =
    document.getElementById("shortlistedCount");

    if(total)
        total.innerText = applications.length;

    if(shortlisted){

        let count =
        applications.filter(
            a => a.status === "SHORTLISTED"
        ).length;

        shortlisted.innerText =
        count + " Shortlisted";

    }

}


/* ===========================
SAVED JOB COUNT
=========================== */

function loadSavedJobsCount(){

    let savedJobs = getSavedJobs();

    let element =
    document.getElementById("savedJobs");

    if(element)
        element.innerText = savedJobs.length;

}


/* ===========================
RECENT NOTIFICATIONS
=========================== */

function loadRecentNotifications(){

    let notifications = getNotifications();
    let box = document.getElementById("notificationBox");

    if(!box) return;

    box.innerHTML = "";

    if(notifications.length === 0){

        box.innerHTML =
        "<p class='text-muted'>No notifications</p>";

        return;

    }

    notifications
    .slice(-5)
    .reverse()
    .forEach(n => {

        box.innerHTML +=
        `<div class="border-bottom py-2">
            ${n.message}
            <div class="text-muted small">
                ${n.date} ${n.time}
            </div>
        </div>`;

    });

}


/* ===========================
DASHBOARD LOADER
=========================== */

function loadDashboard() {

    const userId = document.getElementById("userId").value;

    fetch(`/api/jobseeker/dashboard/${userId}`)
        .then(res => res.json())
        .then(data => {

            document.getElementById("profileScore").innerText =
                data.profileScore + "%";

            document.getElementById("totalApps").innerText =
                data.totalApplications;

            document.getElementById("savedJobs").innerText =
                data.savedJobs;

            const table = document.getElementById("recentApplications");
            table.innerHTML = "";

            data.recentApplications.forEach(app => {

                const row = `
                    <tr>
                        <td>${app.jobTitle}</td>
                        <td>${app.status}</td>
                        <td>${new Date(app.appliedDate).toLocaleDateString()}</td>
                    </tr>
                `;

                table.innerHTML += row;
            });

        })
        .catch(err => console.error("Dashboard load failed", err));
}


/* ===========================
RECENT APPLICATIONS TABLE
=========================== */

function loadRecentApplications(){

    let applications = getApplications();

    let table =
    document.getElementById("recentApplications");

    if(!table) return;

    table.innerHTML = "";

    if(applications.length === 0){

        table.innerHTML =
        "<tr><td colspan='3' class='text-muted'>No applications yet</td></tr>";

        return;

    }

    applications
    .slice(-5)
    .reverse()
    .forEach(app => {

        table.innerHTML +=
        `<tr>
            <td>${app.title || "-"}</td>
            <td>
                <span class="badge ${
                    app.status==="SHORTLISTED"
                    ? "bg-success"
                    : app.status==="REJECTED"
                    ? "bg-danger"
                    : "bg-warning text-dark"
                }">
                ${app.status || "APPLIED"}
                </span>
            </td>
            <td>${app.date || "-"}</td>
        </tr>`;

    });

}


/* =====================================================
APPLY JOB FUNCTION
===================================================== */

function applyJob(jobId){

let jobs =
JSON.parse(localStorage.getItem("jobs")) || [];

let applications =
JSON.parse(localStorage.getItem("applications")) || [];

let profile =
JSON.parse(localStorage.getItem("profile")) || {};

if(!profile.name){

alert("Please complete your profile first");

window.location.href =
"create-profile.html";

return;

}

let job =
jobs.find(j => j.id == jobId);

if(!job){

alert("Job not found");
return;

}

let alreadyApplied =
applications.some(app =>
app.jobId == jobId
);

if(alreadyApplied){

alert("You already applied for this job");
return;

}

let application = {

id: Date.now(),

jobId: jobId,

name: profile.name,

email: profile.email,

phone: profile.phone,

title: job.title,

jobTitle: job.title,

company: job.company || "Company",

skills: profile.skills,

education: profile.education,

experience: profile.experience,

status: "APPLIED",

date: new Date().toLocaleDateString()

};

applications.push(application);

localStorage.setItem(
"applications",
JSON.stringify(applications)
);

/* Employer Notification */

let employerNotifications =
JSON.parse(
localStorage.getItem("employerNotifications")
) || [];

employerNotifications.push({

message:
profile.name +
" applied for " +
job.title,

date: new Date().toLocaleDateString(),
time: new Date().toLocaleTimeString(),
read:false

});

localStorage.setItem(
"employerNotifications",
JSON.stringify(employerNotifications)
);

/* JobSeeker Notification */

addNotification(
"You applied for " + job.title
);

alert("Application submitted successfully!");

window.location.href = "applications.html";

}

//----------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------

