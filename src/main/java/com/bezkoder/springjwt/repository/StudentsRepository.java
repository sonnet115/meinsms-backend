package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Classes;
import com.bezkoder.springjwt.models.Students;
import com.bezkoder.springjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentsRepository extends JpaRepository<Students, Long> {
    Optional<List<Students>> findAllByClasses(Classes classes);
    Optional<List<Students>> findAllByParent(User user);
}
