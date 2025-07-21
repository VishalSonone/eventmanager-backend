package com.kbcnmu.eventmanager.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
                             @Value("${cloudinary.api-key}") String apiKey,
                             @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            throw new IOException("Invalid file name");
        }

        // Extract file extension
        String extension = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
        String resourceType = extension.matches("pdf|docx?|pptx?|txt") ? "raw" : "auto";

        // Generate unique public_id with extension for correct download name
        String publicId = UUID.randomUUID().toString(); // don't include extension here
        String filename = publicId + "." + extension;

        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "resource_type", resourceType,
                "public_id", publicId,             // ensures proper filename
                "use_filename", true,
                "unique_filename", false,
                "filename_override", filename      // explicitly sets extension
            )
        );

        return uploadResult.get("secure_url").toString(); // always use secure_url
    }

    public void deleteFileByUrl(String url) {
        String publicId = extractPublicId(url);
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractPublicId(String url) {
        // Works for both image and raw URLs
        // Example: https://res.cloudinary.com/.../upload/v1234567890/abc123.pdf
        String[] parts = url.split("/");
        String fileWithExt = parts[parts.length - 1];
        return fileWithExt.contains(".") ? fileWithExt.substring(0, fileWithExt.lastIndexOf('.')) : fileWithExt;
    }
}
