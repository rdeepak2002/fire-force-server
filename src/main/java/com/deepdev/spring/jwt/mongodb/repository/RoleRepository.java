package com.deepdev.spring.jwt.mongodb.repository;

import java.util.Optional;

import com.deepdev.spring.jwt.mongodb.models.ERole;
import com.deepdev.spring.jwt.mongodb.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
