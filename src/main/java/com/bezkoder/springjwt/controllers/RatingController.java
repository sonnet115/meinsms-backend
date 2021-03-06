package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.RatingCategoryCreateRequest;
import com.bezkoder.springjwt.payload.request.RatingCreateRequest;
import com.bezkoder.springjwt.payload.response.CommonResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/rating")
public class RatingController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RatingCategoryRepository ratingCategoryRepository;

    @Autowired
    StudentsRepository studentsRepository;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    RatingRepository ratingRepository;


    @PostMapping("/create")
    public ResponseEntity<?> rateStudents(@RequestBody RatingCreateRequest ratingCreateRequest) {
        Optional<Students> studentsOptional = studentsRepository.findById(ratingCreateRequest.getSid());
        Optional<Classes> classesOptional = classesRepository.findById(ratingCreateRequest.getCid());
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(ratingCreateRequest.getRcid());
        if (studentsOptional.isPresent() && classesOptional.isPresent() && ratingCategoryOptional.isPresent()) {
            Rating rating = new Rating();
            rating.setNegative(ratingCreateRequest.getNegative());
            rating.setPositive(ratingCreateRequest.getPositive());
            rating.setStudents(studentsOptional.get());
            rating.setRatingCategory(ratingCategoryOptional.get());
            rating.setClasses(classesOptional.get());
            ratingRepository.save(rating);
            return ResponseEntity.ok(new CommonResponse(true, "rating_submitted_successful", rating));
        }
        return ResponseEntity.ok(new CommonResponse(false, "rating_submitted_failed", ""));
    }

    @GetMapping("/get/student/{studentId}/classes/{classesId}")
    public ResponseEntity<?> getByStudent(@PathVariable Long studentId, @PathVariable Long classesId) {
        Optional<Students> studentsOptional = studentsRepository.findById(studentId);
        Optional<Classes> classesOptional = classesRepository.findById(classesId);
        if (studentsOptional.isPresent() && classesOptional.isPresent()) {
            Students students = studentsOptional.get();
            Classes classes = classesOptional.get();
            Optional<List<Rating>> ratingListOptional = ratingRepository.findAllByStudentsAndClasses(students, classes);
            if (ratingListOptional.isPresent()) {
                List<Rating> ratingList = ratingListOptional.get();
                return ResponseEntity.ok(new CommonResponse(true, "", ratingList));
            } else {
                return ResponseEntity.ok(new CommonResponse(false, "no_rating_found_for_student", ""));
            }
        } else {
            return ResponseEntity.ok(new CommonResponse(false, "invalid_student_id", ""));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RatingCategoryCreateRequest ratingCategoryCreateRequest) {
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(id);
        if (ratingCategoryOptional.isPresent()) {
            RatingCategory ratingCategory = ratingCategoryOptional.get();
            ratingCategory.setName(ratingCategoryCreateRequest.getName());
            ratingCategoryRepository.save(ratingCategory);
            return ResponseEntity.ok(new CommonResponse(true, "rating_updated_successful", ""));
        }
        return ResponseEntity.ok(new CommonResponse(false, "invalid_rating_id", ""));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<RatingCategory> ratingCategoryOptional = ratingCategoryRepository.findById(id);
        if (ratingCategoryOptional.isPresent()) {
            RatingCategory ratingCategory = ratingCategoryOptional.get();
            ratingCategoryRepository.delete(ratingCategory);
            return ResponseEntity.ok(new CommonResponse(true, "rating_deleted_successful", ""));
        }
        return ResponseEntity.ok(new CommonResponse(false, "invalid_rating_id", ""));
    }
}

