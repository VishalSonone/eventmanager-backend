package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.MediaFile;
import com.kbcnmu.eventmanager.repository.MediaFileRepository;
import com.kbcnmu.eventmanager.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaFileRepository mediaFileRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Upload file to Cloudinary
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "eventName", required = false) String eventName) {
        try {
            String originalName = file.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(originalName);
            String fileType = detectFileType(originalName);

            String cloudUrl = cloudinaryService.uploadFile(file);  // Cloudinary URL

            MediaFile media = MediaFile.builder()
                    .filename(filename)
                    .fileType(fileType)
                    .filePath(cloudUrl) // Use Cloudinary URL here
                    .originalName(originalName)
                    .eventName(eventName)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            mediaFileRepo.save(media);
            return ResponseEntity.ok(media);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Upload failed"));
        }
    }

    @GetMapping("/list")
    public List<MediaFile> listFiles(
        @RequestParam(value = "type", required = false) String fileType,
        @RequestParam(value = "eventName", required = false) String eventName,
        @RequestParam(value = "search", required = false) String searchText
    ) {
        List<MediaFile> allFiles = mediaFileRepo.findAll();
        return allFiles.stream()
                .filter(f -> fileType == null || f.getFileType().equalsIgnoreCase(fileType))
                .filter(f -> eventName == null || (f.getEventName() != null && f.getEventName().equalsIgnoreCase(eventName)))
                .filter(f -> searchText == null || f.getOriginalName().toLowerCase().contains(searchText.toLowerCase()))
                .sorted(Comparator.comparing(MediaFile::getUploadedAt).reversed())
                .toList();
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        Optional<MediaFile> mediaOpt = mediaFileRepo.findAll().stream()
                .filter(f -> f.getFilename().equals(filename))
                .findFirst();

        if (mediaOpt.isPresent()) {
            MediaFile media = mediaOpt.get();
            mediaFileRepo.delete(media);

            // 🔥 ACTUAL DELETION FROM CLOUDINARY
            cloudinaryService.deleteFileByUrl(media.getFilePath());

            return ResponseEntity.ok(Map.of("message", "File deleted from DB and Cloudinary"));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "File not found"));
        }
    }

    @PutMapping("/update/{filename}")
    public ResponseEntity<?> updateMetadata(
            @PathVariable String filename,
            @RequestBody Map<String, String> updates) {

        Optional<MediaFile> mediaOpt = mediaFileRepo.findAll()
                .stream()
                .filter(m -> m.getFilename().equals(filename))
                .findFirst();

        if (mediaOpt.isPresent()) {
            MediaFile media = mediaOpt.get();
            media.setOriginalName(updates.get("originalName"));
            media.setEventName(updates.get("eventName"));
            mediaFileRepo.save(media);
            return ResponseEntity.ok(Map.of("message", "Metadata updated"));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Media record not found"));
        }
    }

    private String detectFileType(String name) {
        if (name.matches(".*\\.(jpg|jpeg|png|gif|webp)$")) return "image";
        else if (name.matches(".*\\.(pdf|docx?|pptx?|txt)$")) return "document";
        else return "other";
    }
}
