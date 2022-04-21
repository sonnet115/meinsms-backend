package com.meinsms.backend.controllers;

import com.meinsms.backend.models.Classes;
import com.meinsms.backend.models.ERole;
import com.meinsms.backend.models.Students;
import com.meinsms.backend.models.User;
import com.meinsms.backend.payload.request.StudentAddToClassRequest;
import com.meinsms.backend.payload.request.StudentCreateRequest;
import com.meinsms.backend.payload.response.CommonResponse;
import com.meinsms.backend.repository.ClassesRepository;
import com.meinsms.backend.repository.StudentsRepository;
import com.meinsms.backend.repository.UserRepository;
import com.meinsms.backend.security.services.UserDetailsImpl;
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

        if (user.getRoles().contains(ERole.ROLE_PARENT)) {
            Students students = new Students();
            students.setName(studentCreateRequest.getName());
            students.setParent(user);
            studentsRepository.save(students);
            return ResponseEntity.ok(new CommonResponse(true, "student_created_successful", user.getStudents()));
        } else {
            return ResponseEntity.ok(new CommonResponse(false, "need_tobe_parent", user.getStudents()));
        }
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
            return ResponseEntity.ok(new CommonResponse(true, "student_added_class_successful", null));
        }

        return ResponseEntity.ok(new CommonResponse(false, "invalid_class_or_student_id", null));
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
                return ResponseEntity.ok(new CommonResponse(false, "no_student_for_class", null));
            }
        } else {
            return ResponseEntity.ok(new CommonResponse(false, "invalid_class_id", null));
        }
    }

    @GetMapping("/get/by/parent")
    public ResponseEntity<?> getByParent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.getById(userDetails.getId());

        Optional<List<Students>> studentsOptional = studentsRepository.findAllByParent(user);
        if (studentsOptional.isPresent()) {
            List<Students> studentsList = studentsOptional.get();
            return ResponseEntity.ok(new CommonResponse(true, "", studentsList));
        } else {
            return ResponseEntity.ok(new CommonResponse(false, "no_student_for_parent", null));
        }
    }
}


