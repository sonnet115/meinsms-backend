package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, Long> {
    List<Classes> findAllByTeacher(User teacher);

    Optional<Classes>findAllByClassCode(String classCode);

    Optional<List<Classes>> findAllByStudents(Students students);
}
