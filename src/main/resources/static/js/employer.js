// ================= DATABASE INIT =================
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

// ================= HELPERS =================
function getJobs(){ return JSON.parse(localStorage.getItem("jobs")); }
function saveJobs(j){ localStorage.setItem("jobs", JSON.stringify(j)); }

function getApplications(){ return JSON.parse(localStorage.getItem("applications")); }
function saveApplications(a){ localStorage.setItem("applications", JSON.stringify(a)); }

function getProfile(){ return JSON.parse(localStorage.getItem("employerProfile")); }
function saveProfile(p){ localStorage.setItem("employerProfile", JSON.stringify(p)); }

function generateId(){ return Date.now(); }

// ================= DASHBOARD =================
function loadDashboard(){
    let jobs = getJobs();
    let apps = getApplications();

    document.getElementById("totalJobs").innerText = jobs.length;
    document.getElementById("activeJobs").innerText =
        jobs.filter(j=>j.status==="OPEN").length;
    document.getElementById("totalApps").innerText = apps.length;
    document.getElementById("shortlisted").innerText =
        apps.filter(a=>a.status==="SHORTLISTED").length;
}

// ================= CREATE JOB =================
function createJob(e){
    e.preventDefault();

    let job={
        id:generateId(),
        title:title.value,
        description:description.value,
        skills:skills.value,
        experience:experience.value,
        education:education.value,
        location:location.value,
        salary:salary.value,
        type:type.value,
        deadline:deadline.value,
        status:"OPEN"
    };

    let jobs=getJobs();
    jobs.push(job);
    saveJobs(jobs);

    alert("Job Posted Successfully!");
    window.location.href="list.html";
}

// ================= LOAD JOB LIST =================
// ================= MANAGE JOBS =================
function loadJobs(){
    let jobs=getJobs();
    let table=document.getElementById("jobTable");
    if(!table) return;

    table.innerHTML="";
    jobs.forEach(j=>{
        table.innerHTML+=`
        <tr>
            <td>${j.title}</td>
            <td>${j.status}</td>
            <td>${j.deadline}</td>
            <td>
                <button class="btn btn-info btn-sm" onclick="viewJob(${j.id})">View</button>
                <button class="btn btn-warning btn-sm" onclick="editJob(${j.id})">Edit</button>
                <button class="btn btn-secondary btn-sm" onclick="toggleJobStatus(${j.id})">
                    ${j.status==="OPEN"?"Close":"Reopen"}
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteJob(${j.id})">Delete</button>
            </td>
        </tr>`;
    });
}

function toggleJobStatus(id){
    let jobs=getJobs();
    let job=jobs.find(j=>j.id==id);
    if(job.status==="OPEN"){
        job.status="CLOSED";
    } else {
        job.status="OPEN";
    }
    saveJobs(jobs);
    loadJobs();
}

function deleteJob(id){
    let jobs=getJobs().filter(j=>j.id!==id);
    saveJobs(jobs);
    loadJobs();
}

function viewJob(id){
    localStorage.setItem("viewJobId",id);
    window.location.href="details.html";
}

function editJob(id){
    localStorage.setItem("editJobId",id);
    window.location.href="edit.html";
}

// ================= JOB DETAILS =================
function loadJobDetails(){
    let id=localStorage.getItem("viewJobId");
    if(!id) return;

    let job=getJobs().find(j=>j.id==id);
    if(!job) return;

    document.getElementById("jobDetails").innerHTML=`
        <h3>${job.title}</h3>
        <p><b>Description:</b> ${job.description}</p>
        <p><b>Skills:</b> ${job.skills}</p>
        <p><b>Experience:</b> ${job.experience}</p>
        <p><b>Education:</b> ${job.education}</p>
        <p><b>Location:</b> ${job.location}</p>
        <p><b>Salary:</b> ${job.salary}</p>
        <p><b>Status:</b> ${job.status}</p>
    `;
}

// ================= EDIT JOB =================
function loadEditJob(){
    let id=localStorage.getItem("editJobId");
    if(!id) return;

    let job=getJobs().find(j=>j.id==id);
    if(!job) return;

    title.value=job.title;
    description.value=job.description;
    skills.value=job.skills;
    experience.value=job.experience;
    education.value=job.education;
    location.value=job.location;
    salary.value=job.salary;
    type.value=job.type;
    deadline.value=job.deadline;
}

function updateJob(e){
    e.preventDefault();
    let id=localStorage.getItem("editJobId");
    let jobs=getJobs();
    let job=jobs.find(j=>j.id==id);

    job.title=title.value;
    job.description=description.value;
    job.skills=skills.value;
    job.experience=experience.value;
    job.education=education.value;
    job.location=location.value;
    job.salary=salary.value;
    job.type=type.value;
    job.deadline=deadline.value;

    saveJobs(jobs);
    alert("Job Updated!");
    window.location.href="list.html";
}

// ================= PROFILE =================
function loadProfile(){
    let p=getProfile();
    document.getElementById("profileData").innerHTML=`
        <p><b>Company:</b> ${p.companyName}</p>
        <p><b>Industry:</b> ${p.industry}</p>
        <p><b>Size:</b> ${p.size}</p>
        <p><b>Website:</b> ${p.website}</p>
        <p><b>Location:</b> ${p.location}</p>
        <p><b>Description:</b> ${p.description}</p>
    `;
}

function loadEditProfile(){
    let p=getProfile();
    companyName.value=p.companyName;
    industry.value=p.industry;
    size.value=p.size;
    website.value=p.website;
    location.value=p.location;
    description.value=p.description;
}

function updateProfile(e){
    e.preventDefault();
    let p={
        companyName:companyName.value,
        industry:industry.value,
        size:size.value,
        website:website.value,
        location:location.value,
        description:description.value
    };
    saveProfile(p);
    alert("Profile Updated!");
    window.location.href="profile.html";
}

// ================= APPLICATIONS =================
// ================= APPLICATIONS =================
function loadApplications(filteredApps=null){
    let apps=filteredApps || getApplications();
    let table=document.getElementById("appTable");
    if(!table) return;

    table.innerHTML="";
    apps.forEach(a=>{
        table.innerHTML+=`
        <tr>
            <td>${a.name}</td>
            <td>${a.skills}</td>
            <td>${a.education || ""}</td>
            <td>${a.experience || 0}</td>
            <td>${a.status||"PENDING"}</td>
            <td>
                <button class="btn btn-success btn-sm"
                onclick="changeStatus(${a.id},'SHORTLISTED')">Shortlist</button>
                <button class="btn btn-danger btn-sm"
                onclick="changeStatus(${a.id},'REJECTED')">Reject</button>
            </td>
        </tr>`;
    });
}

function changeStatus(id,status){
    let apps=getApplications();
    let app=apps.find(a=>a.id==id);
    app.status=status;
    saveApplications(apps);
    loadApplications();
}

// ================= FILTER APPLICANTS =================
function filterApplicants(){
    let skill=document.getElementById("filterSkill").value.toLowerCase();
    let education=document.getElementById("filterEducation").value.toLowerCase();
    let experience=parseInt(document.getElementById("filterExperience").value)||0;

    let apps=getApplications();

    let filtered=apps.filter(a=>{
        let skillMatch=skill? a.skills.toLowerCase().includes(skill):true;
        let eduMatch=education? (a.education||"").toLowerCase().includes(education):true;
        let expMatch=(a.experience||0)>=experience;

        return skillMatch && eduMatch && expMatch;
    });

    loadApplications(filtered);
}

// ================= NOTIFICATIONS =================
function loadNotificationCount(){
    let apps=getApplications();
    let newApps=apps.filter(a=>!a.status || a.status==="PENDING").length;

    let badge=document.getElementById("notificationCount");
    if(badge){
        badge.innerText=newApps;
    }
}


document.addEventListener("DOMContentLoaded",()=>{
    loadDashboard();
    loadJobs();
    loadJobDetails();
    loadEditJob();
    loadProfile();
    loadEditProfile();
    loadApplications();
    loadNotificationCount();
   
});

async function updateProfile(event) {
    event.preventDefault();

    // Ensure you have a way to get the userId (e.g., from a hidden input or URL)
    const userId = document.getElementById('userId').value;

    const profileData = {
        companyName: document.getElementById('companyName').value,
        industry: document.getElementById('industry').value,
        companySize: parseInt(document.getElementById('companySize').value) || 0, // Convert to Number
        description: document.getElementById('description').value,
        website: document.getElementById('website').value,
        location: document.getElementById('location').value,
        contactEmail: document.getElementById('contactEmail').value // Passes current email back
    };

    try {
        const response = await fetch(`/employer/profile/${userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(profileData)
        });

        if (response.ok) {
            alert("Profile Updated Successfully!");
            window.location.href = "/employer/profile";
        } else {
            alert("Update failed. Please check your inputs.");
        }
    } catch (error) {
        console.error("Error updating profile:", error);
    }
}