package com.deepdev.spring.jwt.mongodb.payload.request;

import javax.validation.constraints.NotBlank;

public class FireStatusUpdateRequest {
  @NotBlank
  private String deviceId;

  @NotBlank
  private String status;

  @NotBlank
  private String latitude;

  @NotBlank
  private String longitude;

  @NotBlank
  private String data;

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
