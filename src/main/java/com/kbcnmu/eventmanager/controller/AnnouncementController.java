package com.kbcnmu.eventmanager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.kbcnmu.eventmanager.model.Announcement;
import com.kbcnmu.eventmanager.repository.AnnouncementRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;
    private final Path fileStorageLocation;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public AnnouncementController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
        this.fileStorageLocation = Paths.get(uploadDir + "/announcements")
                                      .toAbsolutePath()
                                      .normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory for uploads", ex);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAnnouncements() {
        try {
            List<Announcement> announcements = announcementRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                   .body("Error fetching announcements: " + e.getMessage());
        }
    }

    @GetMapping("/student")
    public ResponseEntity<?> getStudentAnnouncements() {
        try {
            List<Announcement> announcements = announcementRepository.findByTargetInOrderByCreatedAtDesc(
                Arrays.asList(Announcement.TargetAudience.ALL, 
                            Announcement.TargetAudience.STUDENTS)
            );
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                   .body("Error fetching student announcements: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAnnouncement(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("target") String target,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        try {
            // Validate input
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Title is required");
            }
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Content is required");
            }

            Announcement announcement = new Announcement();
            announcement.setTitle(title.trim());
            announcement.setContent(content.trim());
            
            try {
                announcement.setTarget(Announcement.TargetAudience.valueOf(target.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid target audience");
            }
            
            // Handle file upload
            if (file != null && !file.isEmpty()) {
                if (!"application/pdf".equals(file.getContentType())) {
                    return ResponseEntity.badRequest()
                           .body("Only PDF files are allowed");
                }
                
                String originalFileName = file.getOriginalFilename();
                if (originalFileName == null || originalFileName.trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                           .body("Invalid file name");
                }
                
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = UUID.randomUUID() + fileExtension;
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                
                announcement.setFileName(originalFileName);
                announcement.setFilePath(targetLocation.toString());
            }
            
            Announcement savedAnnouncement = announcementRepository.save(announcement);
            return ResponseEntity.ok(savedAnnouncement);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                   .body("Failed to store file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                   .body("Error creating announcement: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
            
            if (announcement.getFilePath() == null || announcement.getFileName() == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(announcement.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            // Set content disposition with original filename
            String contentDisposition = String.format("attachment; filename=\"%s\"", announcement.getFileName());
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        try {
            return announcementRepository.findById(id)
                .map(announcement -> {
                    // Delete associated file if exists
                    if (announcement.getFilePath() != null) {
                        try {
                            Path filePath = Paths.get(announcement.getFilePath());
                            if (Files.exists(filePath)) {
                                Files.delete(filePath);
                            }
                        } catch (IOException e) {
                            return ResponseEntity.internalServerError()
                                   .body("Failed to delete associated file: " + e.getMessage());
                        }
                    }
                    
                    announcementRepository.delete(announcement);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                   .body("Error deleting announcement: " + e.getMessage());
        }
    }
}