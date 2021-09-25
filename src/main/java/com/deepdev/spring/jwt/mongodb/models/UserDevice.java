package com.deepdev.spring.jwt.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Clock;
import java.time.LocalDateTime;

@Document(collection = "user_devices")
public class UserDevice {
  @Id
  private String id;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @Indexed
  private String notificationToken;

  @DBRef
  private User user;

  public UserDevice(LocalDateTime updatedAt,
      String notificationToken, User user) {
    this.createdAt = LocalDateTime.now(Clock.systemUTC());
    this.updatedAt = updatedAt;
    this.notificationToken = notificationToken;
    this.user = user;
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

  public String getNotificationToken() {
    return notificationToken;
  }

  public void setNotificationToken(String notificationToken) {
    this.notificationToken = notificationToken;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
