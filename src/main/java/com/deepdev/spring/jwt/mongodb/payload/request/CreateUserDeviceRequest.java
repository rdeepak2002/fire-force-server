package com.deepdev.spring.jwt.mongodb.payload.request;

import javax.validation.constraints.NotBlank;

public class CreateUserDeviceRequest {
  @NotBlank
  private String notificationToken;

  public String getNotificationToken() {
    return notificationToken;
  }

  public void setNotificationToken(String notificationToken) {
    this.notificationToken = notificationToken;
  }
}
