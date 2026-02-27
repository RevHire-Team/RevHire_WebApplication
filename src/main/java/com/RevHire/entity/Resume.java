    package com.RevHire.entity;

    import java.util.List;

    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.ManyToOne;
    import jakarta.persistence.OneToMany;
    import jakarta.persistence.Table;
    import lombok.Getter;
    import lombok.Setter;

    @Entity
    @Table(name = "RESUMES")
    @Getter @Setter
    public class Resume {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "resume_id")
        private Long resumeId;

        @ManyToOne
        @JoinColumn(name = "seeker_id", nullable = false)
        private JobSeekerProfile seeker;

        @Column(length = 1000)
        private String objective;

        @OneToMany(mappedBy = "resume")
        private List<ResumeEducation> educations;

        @OneToMany(mappedBy = "resume")
        private List<ResumeExperience> experiences;

        @OneToMany(mappedBy = "resume")
        private List<ResumeSkill> skills;

        @OneToMany(mappedBy = "resume")
        private List<ResumeFile> files;
    }