package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.backend.model.AppAuthorization;

@Repository
public interface AppAuthorizationRepository extends JpaRepository<AppAuthorization, Long> {
}
