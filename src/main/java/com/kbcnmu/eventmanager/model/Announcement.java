package com.kbcnmu.eventmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    
    @Enumerated(EnumType.STRING)
    private TargetAudience target;
    
    private String fileName;
    private String filePath;
    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public enum TargetAudience {
        ALL, STUDENTS, FACULTY
    }
}