package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Classes;
import com.bezkoder.springjwt.models.RatingCategory;
import com.bezkoder.springjwt.models.Students;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.ClassesCreateRequest;
import com.bezkoder.springjwt.payload.request.RatingCategoryCreateRequest;
import com.bezkoder.springjwt.payload.response.CommonResponse;
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
        try {
            RatingCategory ratingCategory = new RatingCategory();
            ratingCategory.setName(ratingCategoryCreateRequest.getName());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            User user = userRepository.getById(userDetails.getId());

            ratingCategory.setTeacher(user);
            ratingCategoryRepository.save(ratingCategory);
            return ResponseEntity.ok(new CommonResponse(true, "rating_cat_created_successful", ratingCategory));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonResponse(false, "rating_cat_created_failed", ""));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.getById(userDetails.getId());
        return ResponseEntity.ok(new CommonResponse(true, "", user.getRatingCategories()));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getByClassId(@PathVariable Long id) {
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(id);
        return ratingCategoryOptional.map(ratingCategory -> ResponseEntity.ok(new CommonResponse(true, "", ratingCategory))).orElseGet(() -> ResponseEntity.ok(new CommonResponse(false, "rating_cat_id_invalid", "")));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RatingCategoryCreateRequest ratingCategoryCreateRequest) {
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(id);
        if (ratingCategoryOptional.isPresent()) {
            RatingCategory ratingCategory = ratingCategoryOptional.get();
            ratingCategory.setName(ratingCategoryCreateRequest.getName());
            ratingCategoryRepository.save(ratingCategory);
            return ResponseEntity.ok(new CommonResponse(true, "rating_cat_updated_successful", ""));
        }
        return ResponseEntity.ok(new CommonResponse(false, "rating_cat_id_invalid", ""));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(id);
        if (ratingCategoryOptional.isPresent()) {
            RatingCategory ratingCategory = ratingCategoryOptional.get();
            ratingCategoryRepository.delete(ratingCategory);
            return ResponseEntity.ok(new CommonResponse(true, "rating_cat_deleted_successful", ""));
        }
        return ResponseEntity.ok(new CommonResponse(false, "rating_cat_id_invalid", ""));
    }
}

