package com.example.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.userservice.domain.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{
	Role findByName(String name);
}
