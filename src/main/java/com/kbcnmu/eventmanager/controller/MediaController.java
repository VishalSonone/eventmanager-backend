package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.MediaFile;
import com.kbcnmu.eventmanager.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "http://localhost:5173")
public class MediaController {

    private static final String UPLOAD_DIR = "uploads/media/";

    @Autowired
    private MediaFileRepository mediaFileRepo;

    // Upload file
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "eventName", required = false) String eventName) {
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String originalName = file.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(originalName);
            Path filepath = Paths.get(UPLOAD_DIR, filename);
            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            String fileType = detectFileType(originalName);
            MediaFile media = MediaFile.builder()
                    .filename(filename)
                    .fileType(fileType)
                    .filePath("/uploads/media/" + filename)
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

    // List all files
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

    // Delete file
    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            Optional<MediaFile> mediaOpt = mediaFileRepo.findAll().stream()
                    .filter(f -> f.getFilename().equals(filename))
                    .findFirst();

            mediaOpt.ifPresent(mediaFileRepo::delete);
            Path path = Paths.get(UPLOAD_DIR, filename);
            Files.deleteIfExists(path);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Delete failed"));
        }
    }

    private String detectFileType(String name) {
        if (name.matches(".*\\.(jpg|jpeg|png|gif|webp)$")) return "image";
        else if (name.matches(".*\\.(pdf|docx?|pptx?|txt)$")) return "document";
        else return "other";
    }
    
    @PutMapping("/update/{filename}")
    public ResponseEntity<?> updateMetadata(
            @PathVariable String filename,
            @RequestBody Map<String, String> updates) {

        File file = new File(UPLOAD_DIR + filename);
        if (!file.exists()) {
            return ResponseEntity.status(404).body(Map.of("error", "File not found"));
        }

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
}
