package com.RevHire.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.RevHire.entity.FavoriteJob;

public interface FavoriteJobRepository extends JpaRepository<FavoriteJob, Long> {

    List<FavoriteJob> findBySeekerSeekerId(Long seekerId);

}
