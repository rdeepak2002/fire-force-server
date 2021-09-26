package com.deepdev.spring.jwt.mongodb.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.io.CharSource;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FCMInitializer {
  @Value("${app.firebase-admin-sdk}")
  private String firebaseAdminSDK;

  Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

  @PostConstruct
  public void initialize() {
    try {
      InputStream targetStream =
          CharSource.wrap(firebaseAdminSDK).asByteSource(StandardCharsets.UTF_8).openStream();

      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(targetStream)).build();

      if(FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
        System.out.println("Firebase application has been initialized");
      }
    }
    catch (IOException e) {
      System.err.println("Error initializing Firebase application: " + e.getMessage());
    }
  }

}
