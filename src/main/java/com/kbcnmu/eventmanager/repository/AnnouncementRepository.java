package com.kbcnmu.eventmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kbcnmu.eventmanager.model.Announcement;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllByOrderByCreatedAtDesc();
    
    List<Announcement> findByTargetInOrderByCreatedAtDesc(List<Announcement.TargetAudience> targets);
}