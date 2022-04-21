package com.meinsms.backend.repository;

import java.util.List;
import java.util.Optional;

import com.meinsms.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  @Query("SELECT u FROM User u where u.type = 2")
  List<User> getAllCustomers();

  Optional<User> findById(Long id);

  Boolean existsByUsername(String username);
}
