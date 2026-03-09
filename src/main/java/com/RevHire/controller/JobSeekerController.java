package com.RevHire.controller;

import com.RevHire.dto.ApplicationResponseDTO;
import com.RevHire.dto.FavoriteJobDTO;
import com.RevHire.entity.*;
import com.RevHire.repository.*;
import com.RevHire.service.JobSeekerService;
import com.RevHire.service.ResumeService;
import com.RevHire.service.impl.ApplicationServiceImpl;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;

@RestController
@RequestMapping("/api/jobseeker")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;
    private final ResumeService resumeService;
    private final ApplicationServiceImpl applicationService;

    private final ResumeRepository resumeRepo;
    private final FavoriteJobRepository favoriteJobRepo;
    private final JobSeekerProfileRepository profileRepo;
    private final ResumeEducationRepository educationRepo;
    private final ResumeExperienceRepository experienceRepo;
    private final ResumeCertificationRepository certificationRepo;
    private final ResumeProjectRepository projectRepo;
    private final ResumeSkillRepository resumeSkillRepo;
    public JobSeekerController(
            JobSeekerService jobSeekerService,
            ResumeService resumeService,
            ResumeRepository resumeRepo,
            FavoriteJobRepository favoriteJobRepo,
            JobSeekerProfileRepository profileRepo,
            ResumeEducationRepository educationRepo,
            ResumeExperienceRepository experienceRepo,
            ResumeCertificationRepository certificationRepo,
            ResumeProjectRepository projectRepo,
            ResumeSkillRepository resumeSkillRepo,
            ApplicationServiceImpl applicationService) {

        this.jobSeekerService = jobSeekerService;
        this.resumeService = resumeService;
        this.resumeRepo = resumeRepo;
        this.favoriteJobRepo = favoriteJobRepo;
        this.profileRepo = profileRepo;
        this.educationRepo = educationRepo;
        this.experienceRepo = experienceRepo;
        this.certificationRepo = certificationRepo;
        this.projectRepo = projectRepo;
        this.resumeSkillRepo = resumeSkillRepo;
        this.applicationService = applicationService;
    }

    /* ================= PROFILE ================= */

    @PostMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerProfile> createProfile(
            @PathVariable Long userId,
            @RequestBody JobSeekerProfile profile) {

        return ResponseEntity.ok(jobSeekerService.createProfile(profile, userId));
    }


    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {

        return jobSeekerService.getProfile(userId).map(profile -> {

            Optional<Resume> resumeOpt =
                    resumeRepo.findBySeekerSeekerId(profile.getSeekerId());

            Map<String, Object> response = new HashMap<>();

            response.put("profileId", profile.getSeekerId());
            response.put("fullName", profile.getFullName());
            response.put("phone", profile.getPhone());
            response.put("location", profile.getLocation());
            response.put("profileCompletion", profile.getProfileCompletion());
            response.put("totalExperience", profile.getTotalExperience());

            if (resumeOpt.isPresent()) {

                Resume r = resumeOpt.get();

                response.put("objective", r.getObjective());

                /* ===== SKILLS ===== */

                List<ResumeSkill> skillList =
                        resumeService.getSkillsByResume(r.getResumeId());

                response.put("skills", skillList);

                /* ===== EDUCATION ===== */

                List<ResumeEducation> education =
                        educationRepo.findByResumeResumeId(r.getResumeId());

                response.put("education", education);

                /* ===== EXPERIENCE ===== */

                List<ResumeExperience> experience =
                        experienceRepo.findByResumeResumeId(r.getResumeId());

                response.put("experience", experience);

                /* ===== CERTIFICATIONS ===== */

                List<ResumeCertification> certs =
                        certificationRepo.findByResumeResumeId(r.getResumeId());

                response.put("certifications", certs);

                /* ===== PROJECTS ===== */

                List<ResumeProject> projects =
                        projectRepo.findByResumeResumeId(r.getResumeId());

                response.put("projects", projects);
            }

            return ResponseEntity.ok(response);

        }).orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestBody JobSeekerProfile profile) {

        JobSeekerProfile existing =
                profileRepo.findByUserUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));

        existing.setFullName(profile.getFullName());
        existing.setPhone(profile.getPhone());
        existing.setLocation(profile.getLocation());
        existing.setTotalExperience(profile.getTotalExperience());

        profileRepo.save(existing);

        return ResponseEntity.ok(existing);
    }


    /* ================= RESUME FILE UPLOAD ================= */

    @PostMapping("/resume/upload/{userId}")
    public ResponseEntity<?> uploadResume(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        try {

            if (file.getSize() > 2 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File size must be less than 2MB"));
            }

            ResumeFile savedFile =
                    jobSeekerService.uploadResumeFile(userId, file);

            return ResponseEntity.ok(Map.of(
                    "message", "Resume uploaded successfully",
                    "fileName", savedFile.getFileName()
            ));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* ================= SAVE RESUME BUILDER ================= */
/*
    @PostMapping("/resume/save/{userId}")
    public ResponseEntity<?> saveResume(
            @PathVariable Long userId,
            @RequestBody Map<String,Object> data) {

        try {

            JobSeekerProfile profile = profileRepo.findByUserUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            // Get or create resume
            Resume resume = resumeRepo.findBySeekerSeekerId(profile.getSeekerId())
                    .orElseGet(() -> {
                        Resume r = new Resume();
                        r.setSeeker(profile);
                        return resumeRepo.save(r);
                    });

            // Save objective
            resume.setObjective((String) data.get("objective"));
            resumeRepo.save(resume);

            return ResponseEntity.ok(Map.of(
                    "message","Resume saved successfully"
            ));

        } catch(Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }*/
/*
    @PostMapping("/resume/save/{userId}")
    public ResponseEntity<?> saveResume(
            @PathVariable Long userId,
            @RequestBody Map<String,Object> data) {

        try {

            JobSeekerProfile profile = profileRepo.findByUserUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            Resume resume = resumeRepo.findBySeekerSeekerId(profile.getSeekerId())
                    .orElseGet(() -> {
                        Resume r = new Resume();
                        r.setSeeker(profile);
                        return resumeRepo.save(r);
                    });

            // OBJECTIVE
            resume.setObjective((String) data.get("objective"));
            resumeRepo.save(resume);

            Long resumeId = resume.getResumeId();



            List<Map<String,Object>> educations =
                    (List<Map<String,Object>>) data.get("educations");

            if(educations!=null){
                for(Map<String,Object> e : educations){

                    ResumeEducation edu = new ResumeEducation();
                    edu.setResume(resume);
                    edu.setInstitution((String)e.get("institution"));
                    edu.setDegree((String)e.get("degree"));

                    educationRepo.save(edu);
                }
            }



            List<Map<String,Object>> experiences =
                    (List<Map<String,Object>>) data.get("experiences");

            if(experiences!=null){
                for(Map<String,Object> ex : experiences){

                    ResumeExperience exp = new ResumeExperience();
                    exp.setResume(resume);
                    exp.setCompanyName((String)ex.get("companyName"));
                    exp.setRole((String)ex.get("role"));

                    experienceRepo.save(exp);
                }
            }



            List<Map<String,Object>> projects =
                    (List<Map<String,Object>>) data.get("projects");

            if(projects!=null){
                for(Map<String,Object> p : projects){

                    ResumeProject proj = new ResumeProject();
                    proj.setResume(resume);
                    proj.setProjectTitle((String)p.get("projectTitle"));
                    proj.setTechnologies((String)p.get("technologies"));
                    proj.setProjectLink((String)p.get("projectLink"));
                    proj.setDescription((String)p.get("description"));

                    projectRepo.save(proj);
                }
            }



            List<Map<String,Object>> certs =
                    (List<Map<String,Object>>) data.get("certifications");

            if(certs!=null){
                for(Map<String,Object> c : certs){

                    ResumeCertification cert = new ResumeCertification();
                    cert.setResume(resume);
                    cert.setCertificationName((String)c.get("certificationName"));
                    cert.setCompany((String)c.get("company"));
                    cert.setTechnologies((String)c.get("technologies"));

                    certificationRepo.save(cert);
                }
            }


            List<String> skills = (List<String>) data.get("skills");

            if(skills!=null){
                for(String s : skills){

                    ResumeSkill skill = new ResumeSkill();
                    skill.setResume(resume);
                    skill.setSkillName(s);

                    resumeService.addSkill(skill);
                }
            }

            return ResponseEntity.ok(Map.of(
                    "message","Resume saved successfully"
            ));

        } catch(Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }*/


    @PostMapping("/resume/save/{userId}")
    public ResponseEntity<?> saveResume(
            @PathVariable Long userId,
            @RequestBody Map<String,Object> data) {

        try {

            JobSeekerProfile profile = profileRepo.findByUserUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            // ✅ Always get latest resume
            Resume resume = resumeRepo
                    .findTopBySeekerSeekerIdOrderByResumeIdDesc(profile.getSeekerId())
                    .orElseGet(() -> {
                        Resume r = new Resume();
                        r.setSeeker(profile);
                        return resumeRepo.save(r);
                    });

            resume.setObjective((String) data.get("objective"));
            resumeRepo.save(resume);

            Long resumeId = resume.getResumeId();

            // ✅ DELETE OLD DATA (prevents duplicates)

            educationRepo.deleteByResumeResumeId(resumeId);
            experienceRepo.deleteByResumeResumeId(resumeId);
            projectRepo.deleteByResumeResumeId(resumeId);
            certificationRepo.deleteByResumeResumeId(resumeId);
// ✅ ADD THIS
            resumeSkillRepo.deleteByResumeResumeId(resumeId);
            /* EDUCATION */

            List<Map<String,Object>> educations =
                    (List<Map<String,Object>>) data.get("educations");

            if(educations != null){
                for(Map<String,Object> e : educations){

                    ResumeEducation edu = new ResumeEducation();
                    edu.setResume(resume);
                    edu.setInstitution((String)e.get("institution"));
                    edu.setDegree((String)e.get("degree"));

                    educationRepo.save(edu);
                }
            }

            /* EXPERIENCE */

            List<Map<String,Object>> experiences =
                    (List<Map<String,Object>>) data.get("experiences");

            if(experiences != null){
                for(Map<String,Object> ex : experiences){

                    ResumeExperience exp = new ResumeExperience();
                    exp.setResume(resume);
                    exp.setCompanyName((String)ex.get("companyName"));
                    exp.setRole((String)ex.get("role"));

                    experienceRepo.save(exp);
                }
            }

            /* PROJECTS */

            List<Map<String,Object>> projects =
                    (List<Map<String,Object>>) data.get("projects");

            if(projects != null){
                for(Map<String,Object> p : projects){

                    ResumeProject proj = new ResumeProject();
                    proj.setResume(resume);
                    proj.setProjectTitle((String)p.get("projectTitle"));
                    proj.setTechnologies((String)p.get("technologies"));
                    proj.setProjectLink((String)p.get("projectLink"));
                    proj.setDescription((String)p.get("description"));

                    projectRepo.save(proj);
                }
            }

            /* CERTIFICATIONS */

            List<Map<String,Object>> certs =
                    (List<Map<String,Object>>) data.get("certifications");

            if(certs != null){
                for(Map<String,Object> c : certs){

                    ResumeCertification cert = new ResumeCertification();
                    cert.setResume(resume);
                    cert.setCertificationName((String)c.get("certificationName"));
                    cert.setCompany((String)c.get("company"));
                    cert.setTechnologies((String)c.get("technologies"));

                    certificationRepo.save(cert);
                }
            }

            /* SKILLS */


            List<String> skills = (List<String>) data.get("skills");

            if(skills != null){
                for(String s : skills){

                    ResumeSkill skill = new ResumeSkill();
                    skill.setResume(resume);
                    skill.setSkillName(s.trim());

                    resumeSkillRepo.save(skill);
                }
            }

            return ResponseEntity.ok(Map.of(
                    "message","Resume saved successfully"
            ));

        } catch(Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }













    /* ================= ADD CERTIFICATION ================= */

    @PostMapping("/resume/{resumeId}/certification")
    public ResponseEntity<?> addCertification(
            @PathVariable Long resumeId,
            @RequestBody ResumeCertification cert) {

        Resume resume =
                resumeRepo.findById(resumeId)
                        .orElseThrow(() -> new RuntimeException("Resume not found"));

        cert.setResume(resume);

        return ResponseEntity.ok(certificationRepo.save(cert));
    }


    /* ================= ADD PROJECT ================= */

    @PostMapping("/resume/{resumeId}/project")
    public ResponseEntity<?> addProject(
            @PathVariable Long resumeId,
            @RequestBody ResumeProject project) {

        Resume resume =
                resumeRepo.findById(resumeId)
                        .orElseThrow(() -> new RuntimeException("Resume not found"));

        project.setResume(resume);

        return ResponseEntity.ok(projectRepo.save(project));
    }


    /* ================= DOWNLOAD RESUME ================= */
/*
    @GetMapping("/resume/download/{fileId}")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long fileId) throws Exception {

        ResumeFile file =
                jobSeekerService.getResumeFile(fileId);

        Path path = Paths.get(file.getFilePath());

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }*/
    @GetMapping("/resume/download/{fileId}")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long fileId) throws Exception {

        ResumeFile file =
                resumeService.getResumeFile(fileId);   // ✅ FIXED

        Path path = Paths.get(file.getFilePath());

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }



    /* ================= FAVORITE JOBS ================= */

    @PostMapping("/favorites/{seekerId}/{jobId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable Long seekerId,
            @PathVariable Long jobId) {

        jobSeekerService.addFavoriteJob(seekerId, jobId);

        return ResponseEntity.ok(Map.of(
                "message", "Job added to favorites"
        ));
    }


    @GetMapping("/favorites/{seekerId}")
    public ResponseEntity<List<FavoriteJobDTO>> getFavorites(
            @PathVariable Long seekerId) {

        return ResponseEntity.ok(
                jobSeekerService.getFavorites(seekerId));
    }


    @DeleteMapping("/favorites/{favId}")
    public void removeFavorite(@PathVariable Long favId) {

        jobSeekerService.removeFavoriteJob(favId);
    }


    /* ================= DASHBOARD ================= */

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getDashboard(@PathVariable Long userId) {

        JobSeekerProfile profile =
                profileRepo.findByUserUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));

        Long seekerId = profile.getSeekerId();

        long savedJobs =
                favoriteJobRepo.countBySeekerSeekerId(seekerId);

        List<ApplicationResponseDTO> applications =
                applicationService.getApplicationsBySeeker(seekerId);

        int totalApps = applications.size();

        List<ApplicationResponseDTO> recent =
                applications.stream().limit(5).toList();

        return ResponseEntity.ok(Map.of(
                "profileScore",
                profile.getProfileCompletion() != null ?
                        profile.getProfileCompletion() : 0,
                "totalApplications", totalApps,
                "savedJobs", savedJobs,
                "recentApplications", recent
        ));
    }

}
