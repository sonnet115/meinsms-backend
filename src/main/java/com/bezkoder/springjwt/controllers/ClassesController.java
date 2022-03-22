package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Classes;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.ClassesCreateRequest;
import com.bezkoder.springjwt.repository.ClassesRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/classes")
public class ClassesController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassesRepository classesRepository;

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

    @GetMapping("get")
    public ResponseEntity<?> get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.getById(userDetails.getId());
        return ResponseEntity.ok(user.getClasses());
    }
}
