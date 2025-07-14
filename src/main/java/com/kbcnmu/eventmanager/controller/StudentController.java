package com.kbcnmu.eventmanager.controller;

import com.kbcnmu.eventmanager.model.Student;
import com.kbcnmu.eventmanager.model.Event;
import com.kbcnmu.eventmanager.payload.LoginRequest;
import com.kbcnmu.eventmanager.payload.LoginResponse;
import com.kbcnmu.eventmanager.payload.RegisterRequest;
import com.kbcnmu.eventmanager.service.StudentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<Student> register(@Valid @RequestBody RegisterRequest request) {
        Student student = Student.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .prn(request.getPrn())
                .department(request.getDepartment())
                .password(request.getPassword())
                .studentClass(request.getStudentClass())
                .status("pending")
                .build();

        return ResponseEntity.ok(studentService.registerStudent(student));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Student student = studentService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse("Login successful", student));
    }

    // ✅ Get all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // ✅ Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // ✅ Get students by status (pending/approved/rejected)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Student>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(studentService.getStudentsByStatus(status));
    }

    // ✅ Approve student
    @PutMapping("/{id}/approve")
    public ResponseEntity<Student> approve(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.approveStudent(id));
    }

    // ✅ Reject student
    @PutMapping("/{id}/reject")
    public ResponseEntity<Student> reject(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.rejectStudent(id));
    }

    // ✅ Update student
    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @Valid @RequestBody Student updatedStudent) {
        return ResponseEntity.ok(studentService.updateStudent(id, updatedStudent));
    }

    // ✅ Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully");
    }

    // ✅ Get enrolled events of a student
    @GetMapping("/{id}/events")
    public ResponseEntity<Set<Event>> getEnrolledEvents(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentEvents(id));
    }
}
