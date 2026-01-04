package com.pageon.backend.repository;

import com.pageon.backend.entity.ContentActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ContentActionLog, Long> {

}
