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

    if(document.getElementById("totalJobs"))
        totalJobs.innerText = jobs.length;

    if(document.getElementById("activeJobs"))
        activeJobs.innerText = jobs.filter(j=>j.status==="OPEN").length;

    if(document.getElementById("totalApps"))
        totalApps.innerText = apps.length;

    if(document.getElementById("shortlisted"))
        shortlisted.innerText = apps.filter(a=>a.status==="SHORTLISTED").length;
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

    window.location.href="manage-jobs.html";   // ✅ FIXED
}


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
                <button class="btn btn-info btn-sm"
                onclick="viewJob(${j.id})">View</button>

                <button class="btn btn-warning btn-sm"
                onclick="editJob(${j.id})">Edit</button>

                <button class="btn btn-secondary btn-sm"
                onclick="toggleJobStatus(${j.id})">
                ${j.status==="OPEN"?"Close":"Reopen"}
                </button>

                <button class="btn btn-danger btn-sm"
                onclick="deleteJob(${j.id})">Delete</button>
            </td>
        </tr>`;
    });
}

function toggleJobStatus(id){
    let jobs=getJobs();
    let job=jobs.find(j=>j.id==id);
    job.status=(job.status==="OPEN")?"CLOSED":"OPEN";
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
    window.location.href="job-details.html";   // ✅ FIXED
}

function editJob(id){
    localStorage.setItem("editJobId",id);
    window.location.href="edit-job.html";   // ✅ FIXED
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

    window.location.href="manage-jobs.html";   // ✅ FIXED
}


// ================= INIT =================
document.addEventListener("DOMContentLoaded",()=>{
    loadDashboard();
    loadJobs();
    loadJobDetails();
    loadEditJob();
});