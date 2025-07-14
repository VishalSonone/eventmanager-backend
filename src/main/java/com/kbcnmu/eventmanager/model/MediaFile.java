package com.kbcnmu.eventmanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String fileType; // e.g. "image", "pdf"
    private String filePath;
    private String originalName;
    private String eventName; // optional, can be null
    private LocalDateTime uploadedAt;
}
