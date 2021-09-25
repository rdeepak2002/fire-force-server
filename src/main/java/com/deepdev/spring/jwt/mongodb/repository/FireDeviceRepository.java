package com.deepdev.spring.jwt.mongodb.repository;

import com.deepdev.spring.jwt.mongodb.models.FireDevice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FireDeviceRepository extends MongoRepository<FireDevice, String> { }
