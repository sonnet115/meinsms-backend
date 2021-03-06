package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Classes;
import com.bezkoder.springjwt.models.Students;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.ClassesCreateRequest;
import com.bezkoder.springjwt.payload.request.StudentAddToClassRequest;
import com.bezkoder.springjwt.payload.request.StudentCreateRequest;
import com.bezkoder.springjwt.payload.response.CommonResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.payload.response.StudentCreateResponse;
import com.bezkoder.springjwt.repository.ClassesRepository;
import com.bezkoder.springjwt.repository.StudentsRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;
import jdk.internal.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/students")
public class StudentsController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    StudentsRepository studentsRepository;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody StudentCreateRequest studentCreateRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.getById(userDetails.getId());

        Students students = new Students();
        students.setName(studentCreateRequest.getName());
        students.setParent(user);
        studentsRepository.save(students);
        return ResponseEntity.ok(new CommonResponse(true, "student_created_successful", ""));
    }

    @PostMapping("/add-to-class")
    public ResponseEntity<?> addStudentToClass(@RequestBody StudentAddToClassRequest studentAddToClassRequest) {
        Optional<Students> studentsOptional = studentsRepository.findById(studentAddToClassRequest.getStudentId());
        Optional<Classes> classesOptional = classesRepository.findAllByClassCode(studentAddToClassRequest.getClassCode());
        if (studentsOptional.isPresent() && classesOptional.isPresent()) {
            Students students = studentsOptional.get();
            Classes classes = classesOptional.get();
            Set<Classes> classesSet = students.getClasses();
            classesSet.add(classes);
            students.setClasses(classesSet);
            studentsRepository.save(students);
            return ResponseEntity.ok(new CommonResponse(true, "student_added_class_successful", ""));
        }

        return ResponseEntity.ok(new CommonResponse(false, "invalid_class_or_student_id", ""));
    }

    @GetMapping("/get/class/{classId}")
    public ResponseEntity<?> getByClass(@PathVariable Long classId) {
        Optional<Classes> classesOptional = classesRepository.findById(classId);
        if (classesOptional.isPresent()) {
            Classes classes = classesOptional.get();
            Optional<List<Students>> studentsOptional = studentsRepository.findAllByClasses(classes);
            if (studentsOptional.isPresent()) {
                List<Students> studentsList = studentsOptional.get();
                return ResponseEntity.ok(new CommonResponse(true, "", studentsList));
            } else {
                return ResponseEntity.ok(new CommonResponse(false, "no_student_for_class", ""));
            }
        } else {
            return ResponseEntity.ok(new CommonResponse(false, "invalid_class_id", ""));
        }
    }

    @GetMapping("/get/parent/{parentId}")
    public ResponseEntity<?> getByParent(@PathVariable Long parentId) {
        Optional<User> userOptional = userRepository.findById(parentId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<List<Students>> studentsOptional = studentsRepository.findAllByParent(user);
            if (studentsOptional.isPresent()) {
                List<Students> studentsList = studentsOptional.get();
                return ResponseEntity.ok(new CommonResponse(true, "", studentsList));
            } else {
                return ResponseEntity.ok(new CommonResponse(false, "no_student_for_parent", ""));
            }
        } else {
            return ResponseEntity.ok(new CommonResponse(false, "invalid_parent_id", ""));
        }
    }
}

