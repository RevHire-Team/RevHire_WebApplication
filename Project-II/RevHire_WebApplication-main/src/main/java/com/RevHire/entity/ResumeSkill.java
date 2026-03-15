    package com.RevHire.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.ManyToOne;
    import jakarta.persistence.Table;
    import lombok.Getter;
    import lombok.Setter;

    @Entity
    @Table(name = "RESUME_SKILLS")
    @Getter @Setter
    public class ResumeSkill {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "skill_id")
        private Long skillId;

        @ManyToOne
        @JoinColumn(name = "resume_id", nullable = false)
        @JsonIgnore
        private Resume resume;

        @Column(name = "skill_name")
        private String skillName;
    }