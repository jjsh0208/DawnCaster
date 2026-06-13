package com.github.jjsh0208.dawncasterbackend.domain.users.repository;

import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

     Optional<User> findByEmail(String email);
}
