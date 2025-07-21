package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.Announcement;
import com.kbcnmu.eventmanager.repository.AnnouncementRepository;
import com.kbcnmu.eventmanager.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public AnnouncementController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllAnnouncements() {
        try {
            List<Announcement> announcements = announcementRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching announcements: " + e.getMessage());
        }
    }

    @GetMapping("/student")
    public ResponseEntity<?> getStudentAnnouncements() {
        try {
            List<Announcement> announcements = announcementRepository.findByTargetInOrderByCreatedAtDesc(
                    Arrays.asList(
                            Announcement.TargetAudience.ALL,
                            Announcement.TargetAudience.STUDENTS
                    )
            );
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching student announcements: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAnnouncement(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("target") String target,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
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

            // Handle PDF file upload to Cloudinary
            if (file != null && !file.isEmpty()) {
                if (!"application/pdf".equals(file.getContentType())) {
                    return ResponseEntity.badRequest().body("Only PDF files are allowed");
                }

                String originalFileName = file.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = UUID.randomUUID() + fileExtension;

                // Upload to Cloudinary
                String cloudUrl = cloudinaryService.uploadFile(file);

                announcement.setFileName(originalFileName);
                announcement.setFilePath(cloudUrl);  // Cloudinary URL
            }

            Announcement savedAnnouncement = announcementRepository.save(announcement);
            return ResponseEntity.ok(savedAnnouncement);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating announcement: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        try {
            return announcementRepository.findById(id)
                    .map(announcement -> {
                        // No need to delete from Cloudinary in this version (optional)
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
