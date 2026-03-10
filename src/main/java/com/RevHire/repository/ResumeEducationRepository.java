    package com.RevHire.repository;

    import com.RevHire.entity.Resume;
    import org.springframework.data.jpa.repository.JpaRepository;
    import com.RevHire.entity.ResumeEducation;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.data.jpa.repository.Modifying;
    import java.util.List;

    public interface ResumeEducationRepository extends JpaRepository<ResumeEducation, Long> {

        List<ResumeEducation> findByResume_ResumeId(Long resumeId);

        List<ResumeEducation> findByResumeResumeId(Long resumeId);

        void deleteByResume(Resume resume);

        @Transactional
        @Modifying
        void deleteByResumeResumeId(Long resumeId);

    }