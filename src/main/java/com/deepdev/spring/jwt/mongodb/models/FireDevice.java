package com.deepdev.spring.jwt.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Clock;
import java.time.LocalDateTime;

@Document(collection = "fire_devices")
public class FireDevice {
  @Id
  private String id;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private GeoJsonPoint location;

  private String status;

  private String data;

  private String message;

  public FireDevice(String id, LocalDateTime updatedAt, GeoJsonPoint location, String status, String data, String message) {
    this.id = id;
    this.createdAt = LocalDateTime.now(Clock.systemUTC());
    this.updatedAt = updatedAt;
    this.location = location;
    this.status = status;
    this.data = data;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public GeoJsonPoint getLocation() {
    return location;
  }

  public void setLocation(GeoJsonPoint location) {
    this.location = location;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
