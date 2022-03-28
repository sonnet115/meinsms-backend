package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Classes;
import com.bezkoder.springjwt.models.Rating;
import com.bezkoder.springjwt.models.RatingCategory;
import com.bezkoder.springjwt.models.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<List<Rating>> findAllByStudentsAndClasses(Students students, Classes classes);
}
