// ================= INIT STORAGE =================
if(!localStorage.getItem("jobseekerProfile")){
    localStorage.setItem("jobseekerProfile",JSON.stringify({}));
}
if(!localStorage.getItem("resumeData")){
    localStorage.setItem("resumeData",JSON.stringify({}));
}
if(!localStorage.getItem("jobApplications")){
    localStorage.setItem("jobApplications",JSON.stringify([]));
}
if(!localStorage.getItem("favouriteJobs")){
    localStorage.setItem("favouriteJobs",JSON.stringify([]));
}
if(!localStorage.getItem("notifications")){
    localStorage.setItem("notifications",JSON.stringify([]));
}

// ================= HELPERS =================
function getProfile(){ return JSON.parse(localStorage.getItem("jobseekerProfile")); }
function saveProfile(p){ localStorage.setItem("jobseekerProfile",JSON.stringify(p)); }

function getResume(){ return JSON.parse(localStorage.getItem("resumeData")); }
function saveResume(r){ localStorage.setItem("resumeData",JSON.stringify(r)); }

function getApplications(){ return JSON.parse(localStorage.getItem("jobApplications")); }
function saveApplications(a){ localStorage.setItem("jobApplications",JSON.stringify(a)); }

function getNotifications(){ return JSON.parse(localStorage.getItem("notifications")); }
function saveNotifications(n){ localStorage.setItem("notifications",JSON.stringify(n)); }

// ================= PROFILE SCORE =================
function calculateProfileScore(){
    let p=getProfile();
    let score=0;

    if(p.fullName) score+=20;
    if(p.email) score+=10;
    if(p.phone) score+=10;
    if(p.education) score+=20;
    if(p.skills) score+=20;
    if(p.experience) score+=20;

    return score;
}

// ================= DASHBOARD =================
function loadDashboard(){
    let p=getProfile();
    if(document.getElementById("welcomeName"))
        document.getElementById("welcomeName").innerText=p.fullName||"User";

    let score=calculateProfileScore();
    if(document.getElementById("profileScore"))
        document.getElementById("profileScore").innerText=score+"%";

    if(document.getElementById("totalApps"))
        document.getElementById("totalApps").innerText=getApplications().length;

    loadNotificationCount();
}

// ================= PROFILE =================
function saveProfileForm(e){
    e.preventDefault();
    let profile={
        fullName:fullName.value,
        email:email.value,
        phone:phone.value,
        location:location.value,
        education:education.value,
        skills:skills.value,
        experience:experience.value,
        certifications:certifications.value
    };
    saveProfile(profile);
    alert("Profile Saved Successfully!");
    window.location.href="dashboard.html";
}

// ================= RESUME =================
function saveResumeForm(e){
    e.preventDefault();
    let resume={
        objective:objective.value,
        education:resEducation.value,
        experience:resExperience.value,
        skills:resSkills.value,
        projects:projects.value,
        certifications:resCertifications.value
    };
    saveResume(resume);
    alert("Resume Saved!");
}

function viewResume(){
    let r=getResume();
    document.getElementById("resumeView").innerHTML=`
        <h4>Objective</h4><p>${r.objective||""}</p>
        <h4>Education</h4><p>${r.education||""}</p>
        <h4>Experience</h4><p>${r.experience||""}</p>
        <h4>Skills</h4><p>${r.skills||""}</p>
        <h4>Projects</h4><p>${r.projects||""}</p>
        <h4>Certifications</h4><p>${r.certifications||""}</p>
    `;
}

// ================= JOB SEARCH =================
function searchJobs(){
    let role=jobRole.value.toLowerCase();
    let location=jobLocation.value.toLowerCase();

    let jobs=JSON.parse(localStorage.getItem("jobs"))||[];
    let result=jobs.filter(j=>
        j.title.toLowerCase().includes(role) &&
        j.location.toLowerCase().includes(location)
    );

    let div=document.getElementById("jobResults");
    div.innerHTML="";
    result.forEach(j=>{
        div.innerHTML+=`
        <div class="card p-3 mb-3 shadow">
            <h5>${j.title}</h5>
            <p>${j.location}</p>
            <button class="btn btn-primary modern-btn"
            onclick="applyJob(${j.id})">Apply</button>
            <button class="btn btn-outline-danger modern-btn"
            onclick="addFavourite(${j.id})">❤</button>
        </div>`;
    });
}

// ================= APPLY JOB =================
function applyJob(id){
    let apps=getApplications();
    apps.push({
        id:Date.now(),
        jobId:id,
        status:"Applied"
    });
    saveApplications(apps);

    let notes=getNotifications();
    notes.push("Application Submitted Successfully!");
    saveNotifications(notes);

    alert("Applied Successfully!");
}

// ================= APPLICATIONS =================
function loadApplicationsPage(){
    let apps=getApplications();
    let jobs=JSON.parse(localStorage.getItem("jobs"))||[];
    let div=document.getElementById("applicationList");

    if(!div) return;

    div.innerHTML="";
    apps.forEach(a=>{
        let job=jobs.find(j=>j.id==a.jobId);
        div.innerHTML+=`
        <div class="card p-3 mb-3">
            <h5>${job?job.title:"Job"}</h5>
            <p>Status: <b>${a.status}</b></p>
            <button class="btn btn-danger modern-btn"
            onclick="withdrawApplication(${a.id})">Withdraw</button>
        </div>`;
    });
}

function withdrawApplication(id){
    if(confirm("Are you sure to withdraw?")){
        let apps=getApplications().filter(a=>a.id!=id);
        saveApplications(apps);
        loadApplicationsPage();
    }
}

// ================= FAVOURITES =================
function addFavourite(id){
    let fav=JSON.parse(localStorage.getItem("favouriteJobs"));
    fav.push(id);
    localStorage.setItem("favouriteJobs",JSON.stringify(fav));
    alert("Added to favourites!");
}

// ================= NOTIFICATIONS =================
function loadNotificationCount(){
    let count=getNotifications().length;
    let badge=document.getElementById("notificationCount");
    if(badge) badge.innerText=count;
}

function loadNotifications(){
    let notes=getNotifications();
    let div=document.getElementById("notificationList");
    if(!div) return;

    div.innerHTML="";
    notes.forEach(n=>{
        div.innerHTML+=`<div class="alert alert-info">${n}</div>`;
    });
}

// ================= RESET PASSWORD =================
function resetPassword(e){
    e.preventDefault();
    let current=currentPass.value;
    let newP=newPass.value;
    let confirmP=confirmPass.value;

    let users=JSON.parse(localStorage.getItem("revhireUsers"))||[];
    let user=users[0]; // demo current user

    if(user.password!==current){
        alert("Current password incorrect");
        return;
    }

    if(newP!==confirmP){
        alert("Passwords do not match");
        return;
    }

    user.password=newP;
    localStorage.setItem("revhireUsers",JSON.stringify(users));
    alert("Password Updated Successfully!");
    window.location.href="dashboard.html";
}

document.addEventListener("DOMContentLoaded",()=>{
    loadDashboard();
    viewResume();
    loadApplicationsPage();
    loadNotifications();
});