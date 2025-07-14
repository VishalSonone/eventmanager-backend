package com.kbcnmu.eventmanager.service;

import com.kbcnmu.eventmanager.exception.DuplicateResourceException;
import com.kbcnmu.eventmanager.exception.ResourceNotFoundException;
import com.kbcnmu.eventmanager.exception.ValidationException;
import com.kbcnmu.eventmanager.model.Enrollment;
import com.kbcnmu.eventmanager.model.Event;
import com.kbcnmu.eventmanager.model.Student;
import com.kbcnmu.eventmanager.repository.StudentRepository;
import com.kbcnmu.eventmanager.repository.EnrollmentRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student login(String email, String password) {
        Student student = studentRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!password.equals(student.getPassword())) {
            throw new ValidationException("Invalid password");
        }

        if (!"approved".equalsIgnoreCase(student.getStatus())) {
            throw new ValidationException("Account is not approved yet");
        }

        return student;
    }

    public Student registerStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        if (studentRepository.existsByPrn(student.getPrn())) {
            throw new DuplicateResourceException("PRN already registered");
        }
        if (studentRepository.existsByPhone(student.getPhone())) {
            throw new DuplicateResourceException("Phone number already registered");
        }

        student.setStatus("pending");
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    public List<Student> getStudentsByStatus(String status) {
        return studentRepository.findByStatus(status);
    }

    public Student approveStudent(Long id) {
        Student student = getStudentById(id);
        student.setStatus("approved");
        return studentRepository.save(student);
    }

    public Student rejectStudent(Long id) {
        Student student = getStudentById(id);
        student.setStatus("rejected");
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Student student = getStudentById(id);

        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setPhone(updatedStudent.getPhone());
        student.setPrn(updatedStudent.getPrn());
        student.setDepartment(updatedStudent.getDepartment());
        student.setStudentClass(updatedStudent.getStudentClass());

        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        enrollmentRepository.deleteByStudentId(id);
        studentRepository.deleteById(id);
    }

    public Set<Event> getStudentEvents(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(Enrollment::getEvent)
                .collect(Collectors.toSet());
    }
}