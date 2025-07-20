package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.Admin;
import com.kbcnmu.eventmanager.service.AdminService;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            Admin admin = adminService.login(email, password);
            admin.setPassword(null); // hide password

            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getAdminDetails(@RequestParam String email) {
        try {
            System.out.println("Requested Email: " + email); // log for debug

            Admin admin = adminService.getAdminDetails(email);
            admin.setPassword(null); // hide password
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAdminDetails(@RequestBody Admin updatedAdmin) {
        try {
            Admin admin = adminService.updateAdminDetails(updatedAdmin);
            admin.setPassword(null); // hide password
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}