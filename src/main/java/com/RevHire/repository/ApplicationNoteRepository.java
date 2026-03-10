package com.RevHire.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.RevHire.entity.ApplicationNote;

public interface ApplicationNoteRepository extends JpaRepository<ApplicationNote, Long> {

    List<ApplicationNote> findByApplicationApplicationId(Long applicationId);

}
