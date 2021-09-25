package com.deepdev.spring.jwt.mongodb.controllers;

import com.deepdev.spring.jwt.mongodb.models.FireDevice;
import com.deepdev.spring.jwt.mongodb.payload.request.FireStatusUpdateRequest;
import com.deepdev.spring.jwt.mongodb.payload.response.MessageResponse;
import com.deepdev.spring.jwt.mongodb.repository.FireDeviceRepository;
import com.deepdev.spring.jwt.mongodb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class AppController {
  @Autowired FireDeviceRepository fireDeviceRepository;

  @PostMapping("/fire_status_update")
  public ResponseEntity<?> fireStatusUpdate(@Valid @RequestBody FireStatusUpdateRequest fireStatusUpdateRequest) {
    // parse the request
    LocalDateTime updatedAt = LocalDateTime.now(Clock.systemUTC());
    String deviceId = fireStatusUpdateRequest.getDeviceId();
    String status = fireStatusUpdateRequest.getStatus();
    String latitude = fireStatusUpdateRequest.getLatitude();
    String longitude = fireStatusUpdateRequest.getLongitude();
    String data = fireStatusUpdateRequest.getData();

    GeoJsonPoint location;

    try {
      location = new GeoJsonPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }
    catch (Exception e) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid latitude and longitude"));
    }

    // declare message corresponding to the device status
    String message;

    // check the status is valid and construct a message
    switch (status) {
      case "GOOD":
        message = "There is no fire present.";
        break;
      case "WARN":
        message = "There may be a fire in the area.";
        break;
      case "BAD":
        message = "There is a fire in the area.";
        break;
      default:
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid status"));
    }

    // create / find instance of device to update and store in database
    Optional<FireDevice> maybeFireDevice = fireDeviceRepository.findById(deviceId);
    FireDevice fireDevice = maybeFireDevice.orElseGet(() -> new FireDevice(deviceId, updatedAt, location, status, data, message));

    fireDevice.setUpdatedAt(updatedAt);
    fireDevice.setLocation(location);
    fireDevice.setStatus(status);
    fireDevice.setData(data);
    fireDevice.setMessage(message);

    fireDeviceRepository.save(fireDevice);

    // TODO: alert all necessary user devices about this (via firebase cloud messaging and notifications)

    // return a response
    return ResponseEntity.ok(fireDevice);
  }

	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}
}
