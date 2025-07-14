package com.kbcnmu.eventmanager.repository;

import com.kbcnmu.eventmanager.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    List<MediaFile> findByFileType(String fileType);
    List<MediaFile> findByEventName(String eventName);
}
