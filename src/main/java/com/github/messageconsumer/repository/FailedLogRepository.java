package com.github.messageconsumer.repository;

import com.github.messageconsumer.entity.FailedLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedLogRepository extends JpaRepository<FailedLog, Long> {
}
