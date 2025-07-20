package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.BugReport;
import com.kbcnmu.eventmanager.repository.BugReportRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bug-reports")
@CrossOrigin
public class BugReportController {

    @Autowired
    private BugReportRepository bugReportRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitBug(
            @RequestParam String studentName,
            @RequestParam String message,
            @RequestParam(required = false) MultipartFile screenshot
    ) {
        try {
            String filePath = null;

            if (screenshot != null && !screenshot.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + screenshot.getOriginalFilename();
                Path path = Paths.get("uploads/bugs", fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, screenshot.getBytes());
                filePath = "/uploads/bugs/" + fileName;
            }

            BugReport bug = new BugReport();
            bug.setStudentName(studentName);
            bug.setMessage(message);
            bug.setScreenshotUrl(filePath);
            bug.setSubmittedAt(LocalDateTime.now());

            return ResponseEntity.ok(bugReportRepository.save(bug));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading screenshot.");
        }
    }

    @GetMapping
    public ResponseEntity<List<BugReport>> getAllReports() {
        return ResponseEntity.ok(
            bugReportRepository.findAll(Sort.by(Sort.Direction.DESC, "submittedAt"))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        bugReportRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
