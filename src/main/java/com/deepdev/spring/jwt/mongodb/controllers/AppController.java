package com.deepdev.spring.jwt.mongodb.controllers;

import com.deepdev.spring.jwt.mongodb.models.FireDevice;
import com.deepdev.spring.jwt.mongodb.models.User;
import com.deepdev.spring.jwt.mongodb.models.UserDevice;
import com.deepdev.spring.jwt.mongodb.payload.request.CreateUserDeviceRequest;
import com.deepdev.spring.jwt.mongodb.payload.request.FireStatusUpdateRequest;
import com.deepdev.spring.jwt.mongodb.payload.response.MessageResponse;
import com.deepdev.spring.jwt.mongodb.repository.FireDeviceRepository;
import com.deepdev.spring.jwt.mongodb.repository.UserDeviceRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.mongodb.client.model.geojson.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class AppController {
  @Autowired UserDeviceRepository userDeviceRepository;
  @Autowired FireDeviceRepository fireDeviceRepository;

  @GetMapping("/get_fire_devices")
  public ResponseEntity<?> getFireDevices(@RequestParam(required = false) String lat, @RequestParam(required = false) String lon, @RequestParam(required = false) String d) {
    // check if query parameters are defined
    if(lat != null && lon != null && d != null) {
      // note: distance is in KM
      try {
        List<FireDevice> fireDevices = fireDeviceRepository.findByLocationNear(new Point(Double.parseDouble(lat), Double.parseDouble(lon)), new Distance(Double.parseDouble(d),
            Metrics.KILOMETERS));
        return ResponseEntity.ok(fireDevices);
      }
      catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid latitude and longitude. Verify lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180"));
      }
    }
    // otherwise return all devices
    List<FireDevice> fireDevices = fireDeviceRepository.findAll();
    return ResponseEntity.ok(fireDevices);
  }

  @PostMapping("/create_user_device")
  public ResponseEntity<?> createUserDevice(@Valid @RequestBody
      CreateUserDeviceRequest createUserDeviceRequest) {
    // parse the request
    LocalDateTime updatedAt = LocalDateTime.now(Clock.systemUTC());
    String notificationToken = createUserDeviceRequest.getNotificationToken();
    // TODO: allow for devices to be associated to accounts
    User user = null;

    // create / find instance of device to update and store in database
    Optional<UserDevice> maybeUserDevice = userDeviceRepository.findByNotificationToken(notificationToken);
    UserDevice userDevice = maybeUserDevice.orElseGet(() -> new UserDevice(updatedAt, notificationToken, user));

    userDevice.setUpdatedAt(updatedAt);
    userDevice.setNotificationToken(notificationToken);
    userDevice.setUser(user);

    userDeviceRepository.save(userDevice);

    // return a response
    return ResponseEntity.ok(userDevice);
  }

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
      case FireDevice.GOOD_STATUS:
        message = "There is no fire in the area.";
        break;
      case FireDevice.WARN_STATUS:
        message = "There may be a fire in the area.";
        break;
      case FireDevice.BAD_STATUS:
        message = "There is a fire in the area.";
        break;
      default:
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid status"));
    }

    // create / find instance of device to update and store in database
    Optional<FireDevice> maybeFireDevice = fireDeviceRepository.findById(deviceId);
    FireDevice fireDevice = maybeFireDevice.orElseGet(() -> new FireDevice(deviceId, updatedAt, location, status, data, message));

    String oldStatus = fireDevice.getStatus();

    fireDevice.setUpdatedAt(updatedAt);
    fireDevice.setLocation(location);
    fireDevice.setStatus(status);
    fireDevice.setData(data);
    fireDevice.setMessage(message);

    fireDeviceRepository.save(fireDevice);

    // alert all necessary user devices about this status update (via firebase cloud messaging and notifications)
    // only alert when status changes to "BAD" (from another status)
    Map<String, String> notificationData = new HashMap<>();

    boolean changedToGoodStatus = status.equals(FireDevice.GOOD_STATUS) && !status.equals(oldStatus);
    boolean changedToWarnStatus = status.equals(FireDevice.WARN_STATUS) && !status.equals(oldStatus);
    boolean changedToBadStatus = status.equals(FireDevice.BAD_STATUS) && !status.equals(oldStatus);

    String title;

    if(changedToBadStatus) {
      title = "FIRE ALERT";
    }
    else if(changedToWarnStatus) {
      title = "FIRE WARNING";
    }
    else {
      title = "Fire Status Update";
    }

    notificationData.put("createdAt", fireDevice.getCreatedAt().toString());
    notificationData.put("updatedAt", updatedAt.toString());
    notificationData.put("location", location.toString());
    notificationData.put("status", status);
    notificationData.put("data", data);
    notificationData.put("title", title);
    notificationData.put("message", message);

    if(changedToGoodStatus || changedToWarnStatus || changedToBadStatus) {
      System.out.println("Alerting all user devices");

      List<UserDevice> userDevices = userDeviceRepository.findAll();
      List<String> notificationTokens = new ArrayList<>();

      while(!userDevices.isEmpty()) {
        UserDevice userDevice = userDevices.remove(0);
        String notificationToken = userDevice.getNotificationToken();

        if(notificationToken != null && !notificationToken.isEmpty()) {
          notificationTokens.add(notificationToken);
        }

        // send batch of up to 10 messages
        if(notificationTokens.size() == 10 || (!notificationTokens.isEmpty() && userDevices.isEmpty())) {
          try {
            MulticastMessage fcmMessage = MulticastMessage.builder()
                .putAllData(notificationData)
                .addAllTokens(notificationTokens)
                .build();

            FirebaseMessaging.getInstance().sendMulticast(fcmMessage);
          }
          catch (Exception e) {
            System.err.printf("Error sending message to %s : %n", notificationToken);
            e.printStackTrace();
          }
        }
      }
    }

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
