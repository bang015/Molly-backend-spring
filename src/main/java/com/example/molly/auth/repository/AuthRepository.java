package com.example.molly.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.molly.auth.entity.Verification;

public interface AuthRepository extends JpaRepository<Verification, Long> {
  void deleteByEmail(String email);
  Optional<Verification> findByEmail(String email);
}
