package com.rikka.raymispring.repository;

import com.rikka.raymispring.model.entity.ExceptionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionLogRepository extends JpaRepository<ExceptionLogEntity, Long> {
}
