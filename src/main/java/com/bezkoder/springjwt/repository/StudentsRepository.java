package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Classes;
import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, Long> {
    List<Classes> findAllByTeacher(User teacher);

    Optional<Classes>findAllByClassCode(String classCode);
}
