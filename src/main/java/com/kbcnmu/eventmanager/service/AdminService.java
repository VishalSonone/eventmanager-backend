package com.kbcnmu.eventmanager.service;

import com.kbcnmu.eventmanager.model.Admin;
import com.kbcnmu.eventmanager.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin login(String email, String password) {
        Admin admin = adminRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Admin not found with email: " + email));

        if (!admin.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return admin;
    }

    public Admin getAdminDetails(String email) {
        return adminRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Admin not found with email: " + email));
    }

    public Admin updateAdminDetails(Admin updatedAdmin) {
        Admin existingAdmin = adminRepository.findByEmail(updatedAdmin.getEmail())
            .orElseThrow(() -> new RuntimeException("Admin not found with email: " + updatedAdmin.getEmail()));

        if (updatedAdmin.getFullName() != null) {
            existingAdmin.setFullName(updatedAdmin.getFullName());
        }
        if (updatedAdmin.getContactNumber() != null) {
            existingAdmin.setContactNumber(updatedAdmin.getContactNumber());
        }
        if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
            existingAdmin.setPassword(updatedAdmin.getPassword());
        }

        return adminRepository.save(existingAdmin);
    }
}