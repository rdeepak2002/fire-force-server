# Fire Force App API Server

## Author
Deepak Ramalingam

## API
To view the API endpoints, open the "API.json" file in Postman.

## GeoSpatial Indices

Run the following in Mongo Shell to create GeoSpatial indices

```shell
db.fire_devices.ensureIndex({location:"2dsphere"});
```

## Environment Variables
- PORT (optional variable to specify port)
- DB_URI (URI of MongoDB Atlas instance)
- JWT_SECRET (32 or 64 length string of random characters)
- FIREBASE_ADMIN_SDK (stringified version of Firebase Admin SDK JSON content)
- FCM_SERVER_KEY (cloud messaging server key)
- FCM_SENDER_ID (cloud messaging sender id)

## Run Spring Boot application
```
mvn spring-boot:run
```
