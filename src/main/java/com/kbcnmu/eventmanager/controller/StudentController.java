package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.Event;
import com.kbcnmu.eventmanager.model.Student;
import com.kbcnmu.eventmanager.payload.LoginRequest;
import com.kbcnmu.eventmanager.payload.LoginResponse;
import com.kbcnmu.eventmanager.service.StudentService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")

public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginStudent(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Student student = studentService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse("Login successful", student));
        } catch (Exception ex) {
            return ResponseEntity
                .status(401)
                .body(new LoginResponse(ex.getMessage())); // uses error constructor
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Student> registerStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.registerStudent(student);
        return ResponseEntity.ok(savedStudent);
    }
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // 2. Approve a student
    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveStudent(@PathVariable Long id) {
        studentService.approveStudent(id);
        return ResponseEntity.ok().build();
    }

    // 3. Reject a student
    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> rejectStudent(@PathVariable Long id) {
        studentService.rejectStudent(id);
        return ResponseEntity.ok().build();
    }

    // 4. Update student details
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        Student updated = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updated);
    }

    // 5. Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/events")
    public ResponseEntity<Set<Event>> getStudentEvents(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentEvents(id));
    }
}
