package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Classes;
import com.bezkoder.springjwt.models.RatingCategory;
import com.bezkoder.springjwt.models.Students;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.ClassesCreateRequest;
import com.bezkoder.springjwt.payload.request.RatingCategoryCreateRequest;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.ClassesRepository;
import com.bezkoder.springjwt.repository.RatingCategoryRepository;
import com.bezkoder.springjwt.repository.StudentsRepository;
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
@RequestMapping("/api/rating-category")
public class RatingCategoryController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RatingCategoryRepository ratingCategoryRepository;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody RatingCategoryCreateRequest ratingCategoryCreateRequest) {
        RatingCategory ratingCategory = new RatingCategory();
        ratingCategory.setName(ratingCategoryCreateRequest.getName());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.getById(userDetails.getId());

        ratingCategory.setTeacher(user);
        ratingCategoryRepository.save(ratingCategory);
        return ResponseEntity.ok(new MessageResponse(ratingCategory.getName() + " is created successfully", 200));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.getById(userDetails.getId());
        return ResponseEntity.ok(user.getRatingCategories());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getByClassId(@PathVariable Long id) {
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(id);
        if (ratingCategoryOptional.isPresent()) {
            return ResponseEntity.ok(ratingCategoryOptional.get());
        }
        return ResponseEntity.ok(new MessageResponse("Rating Category ID is invalid", 400));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RatingCategoryCreateRequest ratingCategoryCreateRequest) {
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(id);
        if (ratingCategoryOptional.isPresent()) {
            RatingCategory ratingCategory = ratingCategoryOptional.get();
            ratingCategory.setName(ratingCategoryCreateRequest.getName());
            ratingCategoryRepository.save(ratingCategory);
            return ResponseEntity.ok(new MessageResponse(ratingCategoryCreateRequest.getName() + " Updated Successfully", 200));
        }
        return ResponseEntity.ok(new MessageResponse("Rating Category ID is Invalid", 400));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(id);
        if (ratingCategoryOptional.isPresent()) {
            RatingCategory ratingCategory = ratingCategoryOptional.get();
            ratingCategoryRepository.delete(ratingCategory);
            return ResponseEntity.ok(new MessageResponse(ratingCategory.getName() + " Deleted Successfully", 200));
        }
        return ResponseEntity.ok(new MessageResponse("Rating Category ID is Invalid", 400));
    }
}

