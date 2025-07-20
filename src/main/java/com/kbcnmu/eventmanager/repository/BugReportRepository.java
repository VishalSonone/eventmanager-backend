package com.kbcnmu.eventmanager.repository;

import com.kbcnmu.eventmanager.model.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugReportRepository extends JpaRepository<BugReport, Long> {}
