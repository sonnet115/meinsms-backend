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
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/classes")
public class ClassesController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    StudentsRepository studentsRepository;

    Random rand = new Random();

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ClassesCreateRequest classesCreateRequest) {
        try {
            int classCode = 0;
            for (; ; ) {
                classCode = rand.nextInt(100000);
                Optional<Classes> classExist = classesRepository.findAllByClassCode(String.valueOf(classCode));
                if (classExist.isPresent()) {
                    continue;
                }
                break;
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            User user = userRepository.getById(userDetails.getId());

            Classes classes = new Classes();
            classes.setClassName(classesCreateRequest.getName());
            classes.setTeacher(user);
            classes.setClassCode(String.valueOf(classCode));
            classesRepository.save(classes);

            return ResponseEntity.ok(new CommonResponse(true, "class_create_successful", classes));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonResponse(false, "class_create_failed", ""));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.getById(userDetails.getId());
        return ResponseEntity.ok(new CommonResponse(true, "", user.getClasses()));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getByClassId(@PathVariable Long id) {
        Optional<Classes> classes = classesRepository.findById(id);
        return classes.map(value -> ResponseEntity.ok(new CommonResponse(true, "", value))).orElseGet(() -> ResponseEntity.ok(new CommonResponse(false, "no_class_found", "")));
    }

    @GetMapping("/get/code/{classCode}")
    public ResponseEntity<?> getByClassCode(@PathVariable String classCode) {
        Optional<Classes> classes = classesRepository.findAllByClassCode(classCode);
        return classes.map(value -> ResponseEntity.ok(new CommonResponse(true, "", value))).orElseGet(() -> ResponseEntity.ok(new CommonResponse(false, "no_class_found", "")));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ClassesCreateRequest classesCreateRequest) {

        Optional<Classes> classesOptional = classesRepository.findById(id);

        if (classesOptional.isPresent()) {
            Classes classes = classesOptional.get();
            classes.setClassName(classesCreateRequest.getName());
            classesRepository.save(classes);
            return ResponseEntity.ok(new CommonResponse(true, "class_updated_success", classes));
        }
        return ResponseEntity.ok(new CommonResponse(false, "class_updated_failed", ""));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Classes> classesOptional = classesRepository.findById(id);
        if (classesOptional.isPresent()) {
            Classes classes = classesOptional.get();
            classesRepository.delete(classes);
            return ResponseEntity.ok(new CommonResponse(true, "class_deleted_success", classes));
        }
        return ResponseEntity.ok(new CommonResponse(true, "class_deleted_failed", ""));
    }

    @GetMapping("/get/student/{studentId}")
    public ResponseEntity<?> getByStudent(@PathVariable Long studentId) {
        Optional<Students> studentsOptional = studentsRepository.findById(studentId);
        if (studentsOptional.isPresent()) {
            Students students = studentsOptional.get();
            Optional<List<Classes>> classOptional = classesRepository.findAllByStudents(students);
            if (classOptional.isPresent()) {
                List<Classes> classesList = classOptional.get();
                return ResponseEntity.ok(new CommonResponse(true, "", classesList));
            } else {
                return ResponseEntity.ok(new CommonResponse(false, "no_class_found_for_student", ""));
            }
        } else {
            return ResponseEntity.ok(new CommonResponse(false, "invalid_student_id", ""));
        }
    }
}

