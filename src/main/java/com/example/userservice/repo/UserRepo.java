package com.example.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.userservice.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
