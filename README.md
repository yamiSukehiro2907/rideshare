# Ride Share API Documentation

A Spring Boot-based ride-sharing application with JWT authentication, role-based access control, and MongoDB integration.

## Table of Contents
- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [API Endpoints](#api-endpoints)
  - [Authentication](#authentication)
  - [Passenger Endpoints](#passenger-endpoints)
  - [Driver Endpoints](#driver-endpoints)
- [Error Responses](#error-responses)

---

## Overview

This is a ride-sharing backend service that allows users to request rides and drivers to accept and complete them. The application uses JWT tokens for authentication and supports two user roles: `USER` (passengers) and `DRIVER`.

## Tech Stack

- **Framework:** Spring Boot 4.0.0
- **Database:** MongoDB
- **Security:** Spring Security with JWT
- **Java Version:** 25
- **Build Tool:** Maven

## Prerequisites

- Java 25
- MongoDB instance
- Maven

## Setup

1. Create a `.env` file in the root directory with the following variables:

```env
MONGODB_URI=your_mongodb_connection_string
MONGODB_DB=your_database_name

ACCESS_TOKEN_SECRET=your_access_token_secret_key
ACCESS_TOKEN_EXPIRATION=3600000
```

2. Build and run the application:

```bash
mvn clean install
mvn spring-boot:run
```

The server will start on `http://localhost:8081`

---

## API Endpoints

### Authentication

#### 1. Register User

**Endpoint:** `POST /auth/register`

**Description:** Create a new user account (passenger or driver)

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123",
  "role": "USER"
}
```

**Valid Roles:** `USER`, `DRIVER`

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {user}
}
```

**Error Responses:**

Username already exists (409 Conflict):
```json
{
  "success": false,
  "message": "Username already exists",
  "data": null
}
```

Invalid role (400 Bad Request):
```json
{
  "success": false,
  "message": "Invalid role. Only DRIVER and USER are allowed!",
  "data": null
}
```

Password too short (400 Bad Request):
```json
{
  "success": false,
  "message": "Password should be at least 6 characters long",
  "data": null
}
```

Validation error (400 Bad Request):
```json
{
  "error": "VALIDATION_ERROR",
  "message": {
    "username": "Username is required",
    "password": "Password must be at least 3 characters long"
  },
  "timestamp": "2025-12-08T10:30:00.000Z"
}
```

---

#### 2. Login

**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and receive access/refresh tokens

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "507f1f77bcf86cd799439011",
    "username": "john_doe",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Error Responses:**

Invalid credentials (401 Unauthorized):
```json
{
  "error": "UNAUTHORIZED",
  "message": "Invalid username or password",
  "timestamp": "2025-12-08T10:30:00.000Z"
}
```

---

### Passenger Endpoints

**Authentication Required:** Bearer token in `Authorization` header

**Role Required:** `USER`

#### 3. Create Ride Request

**Endpoint:** `POST /api/v1/rides`

**Description:** Create a new ride request

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "pickupLocation": "123 Main Street, Downtown",
  "dropLocation": "456 Park Avenue, Uptown"
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Ride created successfully!",
  "data": {
    "rideId": "507f1f77bcf86cd799439011",
    "userId": "507f1f77bcf86cd799439012",
    "driverId": "",
    "createdDate": "2025-12-08T10:30:00",
    "rideStatus": "REQUESTED",
    "dropLocation": "456 Park Avenue, Uptown",
    "pickupLocation": "123 Main Street, Downtown"
  }
}
```

**Error Responses:**

Authentication failed (401 Unauthorized):
```json
{
  "success": false,
  "message": "Authentication Failed",
  "data": null
}
```

Missing fields (400 Bad Request):
```json
{
  "error": "VALIDATION_ERROR",
  "message": {
    "pickupLocation": "Pickup is required",
    "dropLocation": "Drop is required"
  },
  "timestamp": "2025-12-08T10:30:00.000Z"
}
```

---

#### 4. Get My Rides

**Endpoint:** `GET /api/v1/user/rides`

**Description:** Retrieve all rides for the authenticated user

**Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Rides found!",
  "data": [
    {
      "rideId": "507f1f77bcf86cd799439011",
      "userId": "507f1f77bcf86cd799439012",
      "driverId": "507f1f77bcf86cd799439013",
      "createdDate": "2025-12-08T10:30:00",
      "rideStatus": "COMPLETED",
      "dropLocation": "456 Park Avenue, Uptown",
      "pickupLocation": "123 Main Street, Downtown"
    },
    {
      "rideId": "507f1f77bcf86cd799439014",
      "userId": "507f1f77bcf86cd799439012",
      "driverId": "",
      "createdDate": "2025-12-08T11:00:00",
      "rideStatus": "REQUESTED",
      "dropLocation": "789 Oak Road, Suburb",
      "pickupLocation": "321 Elm Street, City Center"
    }
  ]
}
```

**Error Responses:**

Authentication failed (401 Unauthorized):
```json
{
  "success": false,
  "message": "Authentication Failed!",
  "data": null
}
```

---

### Driver Endpoints

**Authentication Required:** Bearer token in `Authorization` header

**Role Required:** `DRIVER`

#### 5. Get Requested Rides

**Endpoint:** `GET /api/v1/driver/rides/requests`

**Description:** Retrieve all rides with status `REQUESTED` (available for acceptance)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Rides found!",
  "data": [
    {
      "rideId": "507f1f77bcf86cd799439011",
      "userId": "507f1f77bcf86cd799439012",
      "driverId": "",
      "createdDate": "2025-12-08T10:30:00",
      "rideStatus": "REQUESTED",
      "dropLocation": "456 Park Avenue, Uptown",
      "pickupLocation": "123 Main Street, Downtown"
    },
    {
      "rideId": "507f1f77bcf86cd799439014",
      "userId": "507f1f77bcf86cd799439015",
      "driverId": "",
      "createdDate": "2025-12-08T11:00:00",
      "rideStatus": "REQUESTED",
      "dropLocation": "789 Oak Road, Suburb",
      "pickupLocation": "321 Elm Street, City Center"
    }
  ]
}
```

**Error Responses:**

Authentication failed (401 Unauthorized):
```json
{
  "success": false,
  "message": "Authentication Failed",
  "data": null
}
```

---

#### 6. Accept Ride

**Endpoint:** `POST /api/v1/driver/rides/{rideId}/accept`

**Description:** Accept a ride request

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `rideId` - The ID of the ride to accept

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Ride accepted successfully!",
  "data": {
    "rideId": "507f1f77bcf86cd799439011",
    "userId": "507f1f77bcf86cd799439012",
    "driverId": "507f1f77bcf86cd799439013",
    "createdDate": "2025-12-08T10:30:00",
    "rideStatus": "ACCEPTED",
    "dropLocation": "456 Park Avenue, Uptown",
    "pickupLocation": "123 Main Street, Downtown"
  }
}
```

**Error Responses:**

Ride not found (404 Not Found):
```json
{
  "success": false,
  "message": "Ride not found!",
  "data": null
}
```

Ride already accepted (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride already accepted!",
  "data": null
}
```

Ride already completed (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride already completed!",
  "data": null
}
```

Ride already has a driver (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride driver already exists!",
  "data": null
}
```

---

#### 7. Complete Ride

**Endpoint:** `POST /api/v1/driver/rides/{rideId}/complete`

**Description:** Mark a ride as completed

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `rideId` - The ID of the ride to complete

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Ride completed successfully!",
  "data": {
    "rideId": "507f1f77bcf86cd799439011",
    "userId": "507f1f77bcf86cd799439012",
    "driverId": "507f1f77bcf86cd799439013",
    "createdDate": "2025-12-08T10:30:00",
    "rideStatus": "COMPLETED",
    "dropLocation": "456 Park Avenue, Uptown",
    "pickupLocation": "123 Main Street, Downtown"
  }
}
```

**Error Responses:**

Ride not found (404 Not Found):
```json
{
  "success": false,
  "message": "Ride not found!",
  "data": null
}
```

Ride not accepted yet (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride not accepted yet!",
  "data": null
}
```

Ride already completed (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride already completed!",
  "data": null
}
```

Not the assigned driver (409 Conflict):
```json
{
  "success": false,
  "message": "You are not the driver!",
  "data": null
}
```

---

## Error Responses

### Common Error Formats

#### Access Denied (403 Forbidden)
```json
{
  "error": "FORBIDDEN",
  "message": "You don't have permission to access this resource",
  "timestamp": "2025-12-08T10:30:00.000Z"
}
```

#### Unauthorized (401 Unauthorized)
```json
{
  "error": "UNAUTHORIZED",
  "message": "Authentication failed: <error_details>",
  "timestamp": "2025-12-08T10:30:00.000Z"
}
```

#### Token Not Provided (401 Unauthorized)
```json
{
  "success": false,
  "message": "Token not provided"
}
```

#### Internal Server Error (500 Internal Server Error)
```json
{
  "error": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred",
  "timestamp": "2025-12-08T10:30:00.000Z"
}
```

---

## Ride Status Flow

1. **REQUESTED** - Initial state when a passenger creates a ride
2. **ACCEPTED** - When a driver accepts the ride request
3. **COMPLETED** - When the driver marks the ride as completed

---

## Notes

- All endpoints except `/auth/**` require JWT authentication
- Access tokens are included in the `Authorization` header as `Bearer <token>`
- Token expiration times are configurable via environment variables
- Passwords must be at least 3 characters long
- Usernames must be unique
- Only the driver who accepted a ride can complete it
- Rides cannot be accepted or completed if they're already in `COMPLETED` status
