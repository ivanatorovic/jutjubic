package com.example.jutjubic.repository;

import com.example.jutjubic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
