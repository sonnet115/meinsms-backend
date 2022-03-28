package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Classes;
import com.bezkoder.springjwt.models.Students;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.ClassesCreateRequest;
import com.bezkoder.springjwt.payload.request.StudentAddToClassRequest;
import com.bezkoder.springjwt.payload.request.StudentCreateRequest;
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

        return ResponseEntity.ok("Classes Created Successfully");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.getById(userDetails.getId());
        return ResponseEntity.ok(user.getClasses());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getByClassId(@PathVariable Long id) {
        Optional<Classes> classes = classesRepository.findById(id);
        if (classes.isPresent()) {
            return ResponseEntity.ok(classes.get());
        }
        return ResponseEntity.ok("No Class Found");
    }

    @GetMapping("/get/code/{classCode}")
    public ResponseEntity<?> getByClassCode(@PathVariable String classCode) {
        Optional<Classes> classes = classesRepository.findAllByClassCode(classCode);
        if (classes.isPresent()) {
            return ResponseEntity.ok(classes.get());
        }
        return ResponseEntity.ok("No Class Found");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ClassesCreateRequest classesCreateRequest) {

        Optional<Classes> classesOptional = classesRepository.findById(id);

        if (classesOptional.isPresent()) {
            Classes classes = classesOptional.get();
            classes.setClassName(classesCreateRequest.getName());
            classesRepository.save(classes);
            return ResponseEntity.ok("Class Updated Successfully");
        }
        return ResponseEntity.ok("No Class Found");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Classes> classesOptional = classesRepository.findById(id);
        if (classesOptional.isPresent()) {
            Classes classes = classesOptional.get();
            classesRepository.delete(classes);
            return ResponseEntity.ok("Class Deleted Successfully");
        }
        return ResponseEntity.ok("No Class Found");
    }

    @GetMapping("/get/student/{studentId}")
    public ResponseEntity<?> getByStudent(@PathVariable Long studentId) {
        Optional<Students> studentsOptional = studentsRepository.findById(studentId);
        if (studentsOptional.isPresent()) {
            Students students = studentsOptional.get();
            Optional<List<Classes>> classOptional = classesRepository.findAllByStudents(students);
            if (classOptional.isPresent()) {
                List<Classes> classesList = classOptional.get();
                return ResponseEntity.ok(classesList);
            } else {
                return ResponseEntity.ok(new MessageResponse("No Class found for that Student", 404));
            }
        } else {
            return ResponseEntity.ok(new MessageResponse("Invalid Student ID", 400));
        }
    }
}

