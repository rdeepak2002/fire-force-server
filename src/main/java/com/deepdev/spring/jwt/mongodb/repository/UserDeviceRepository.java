package com.deepdev.spring.jwt.mongodb.repository;

import com.deepdev.spring.jwt.mongodb.models.UserDevice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserDeviceRepository extends MongoRepository<UserDevice, String> {
  Optional<UserDevice> findByNotificationToken(String notificationToken);
}
