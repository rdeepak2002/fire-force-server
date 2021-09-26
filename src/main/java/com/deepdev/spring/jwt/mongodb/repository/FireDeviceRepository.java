package com.deepdev.spring.jwt.mongodb.repository;

import com.deepdev.spring.jwt.mongodb.models.FireDevice;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FireDeviceRepository extends MongoRepository<FireDevice, String> {
  List<FireDevice> findByLocationNear(Point location, Distance distance);
}
