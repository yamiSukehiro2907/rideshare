# Ride Share API

A Spring Boot-based ride-sharing application backend service that provides authentication, ride management, and role-based access control for passengers and drivers.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
    - [Authentication Endpoints](#authentication-endpoints)
    - [Passenger Endpoints](#passenger-endpoints)
    - [Driver Endpoints](#driver-endpoints)
- [Database Schema](#database-schema)
- [Security](#security)
- [Error Handling](#error-handling)

## Features

- **User Authentication**: JWT-based authentication with access and refresh tokens
- **Role-Based Access Control**: Separate functionalities for passengers and drivers
- **Ride Management**: Create, accept, and complete rides
- **MongoDB Integration**: NoSQL database for flexible data storage
- **Security**: BCrypt password encoding, stateless session management
- **Validation**: Request validation with detailed error messages

## Tech Stack

- **Java**: 25
- **Spring Boot**: 4.0.0
- **Spring Security**: JWT-based authentication
- **MongoDB**: Database
- **Lombok**: Reduce boilerplate code
- **JWT (jjwt)**: Token generation and validation
- **Maven**: Build tool

## Prerequisites

- Java 25 or higher
- MongoDB instance (local or cloud)
- Maven 3.6+

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yamiSukehiro2907/rideshare
cd uber
```

2. Install dependencies:
```bash
mvn clean install
```

## Configuration

Create a `.env` file in the root directory with the following variables:

```env
MONGODB_URI=mongodb://localhost:27017
MONGODB_DB=ride_share

ACCESS_TOKEN_SECRET=your_access_token_secret_key_minimum_32_characters
REFRESH_TOKEN_SECRET=your_refresh_token_secret_key_minimum_32_characters
ACCESS_TOKEN_EXPIRATION=900000
REFRESH_TOKEN_EXPIRATION=604800000
```

**Note**:
- `ACCESS_TOKEN_EXPIRATION` is in milliseconds (900000 = 15 minutes)
- `REFRESH_TOKEN_EXPIRATION` is in milliseconds (604800000 = 7 days)
- Secret keys should be at least 32 characters long for HS256 algorithm

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

## API Endpoints

### Authentication Endpoints

#### 1. Register User

**Endpoint**: `POST /auth/register`

**Description**: Register a new user (passenger or driver)

**Request Headers**: None required

**Request Body**:
```json
{
  "username": "john_doe",
  "password": "securePassword123",
  "role": "USER"
}
```

**Valid Roles**: `USER` (passenger) or `DRIVER`

**Success Response** (201 Created):
```json
{
  "success": true,
  "message": "User created successfully",
  "data": null
}
```

**Error Response - Username Already Exists** (409 Conflict):
```json
{
  "success": false,
  "message": "Username already exists",
  "data": null
}
```

**Error Response - Invalid Role** (400 Bad Request):
```json
{
  "success": false,
  "message": "Invalid role. Only DRIVER and USER are allowed!",
  "data": null
}
```

**Error Response - Validation Error** (400 Bad Request):
```json
{
  "error": "VALIDATION_ERROR",
  "message": {
    "username": "Username is required",
    "password": "Password must be at least 3 characters long"
  },
  "timestamp": "2024-12-08T10:30:00.000Z"
}
```

---

#### 2. Login

**Endpoint**: `POST /auth/login`

**Description**: Authenticate user and receive JWT tokens

**Request Headers**: None required

**Request Body**:
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "64a8f9b7c123456789abcdef",
    "username": "john_doe",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Error Response - Invalid Credentials** (401 Unauthorized):
```json
{
  "success": false,
  "message": "Invalid password",
  "data": null
}
```

**Error Response - User Not Found** (401 Unauthorized):
```json
{
  "success": false,
  "message": "No user found with this username",
  "data": null
}
```

---

### Passenger Endpoints

**Note**: All passenger endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <accessToken>
```

#### 3. Create Ride Request

**Endpoint**: `POST /api/v1/rides`

**Description**: Passenger creates a new ride request

**Request Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body**:
```json
{
  "pickupLocation": "123 Main Street, Downtown",
  "dropLocation": "456 Oak Avenue, Uptown"
}
```

**Success Response** (201 Created):
```json
{
  "success": true,
  "message": "Ride created successfully!",
  "data": {
    "rideId": "64a8f9b7c123456789abcdef",
    "userId": "64a8f9b7c123456789abcd00",
    "driverId": "",
    "createdDate": "2024-12-08T10:30:00",
    "rideStatus": "REQUESTED",
    "dropLocation": "456 Oak Avenue, Uptown",
    "pickupLocation": "123 Main Street, Downtown"
  }
}
```

**Error Response - Unauthorized** (401 Unauthorized):
```json
{
  "success": false,
  "message": "Only Passengers allowed",
  "data": null
}
```

**Error Response - Validation Error** (400 Bad Request):
```json
{
  "error": "VALIDATION_ERROR",
  "message": {
    "pickupLocation": "Pickup is required",
    "dropLocation": "Drop is required"
  },
  "timestamp": "2024-12-08T10:30:00.000Z"
}
```

---

#### 4. Get My Rides

**Endpoint**: `GET /api/v1/user/rides`

**Description**: Get all rides for the authenticated passenger

**Request Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body**: None

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Rides found!",
  "data": [
    {
      "rideId": "64a8f9b7c123456789abcdef",
      "userId": "64a8f9b7c123456789abcd00",
      "driverId": "64a8f9b7c123456789abcd01",
      "createdDate": "2024-12-08T10:30:00",
      "rideStatus": "COMPLETED",
      "dropLocation": "456 Oak Avenue, Uptown",
      "pickupLocation": "123 Main Street, Downtown"
    },
    {
      "rideId": "64a8f9b7c123456789abcd11",
      "userId": "64a8f9b7c123456789abcd00",
      "driverId": "",
      "createdDate": "2024-12-08T11:00:00",
      "rideStatus": "REQUESTED",
      "dropLocation": "789 Pine Road, Suburbs",
      "pickupLocation": "321 Elm Street, Midtown"
    }
  ]
}
```

**Error Response - Unauthorized** (401 Unauthorized):
```json
{
  "success": false,
  "message": "Only Passengers allowed",
  "data": null
}
```

---

### Driver Endpoints

**Note**: All driver endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <accessToken>
```

#### 5. Get Requested Rides

**Endpoint**: `GET /api/v1/driver/rides/requests`

**Description**: Get all rides with "REQUESTED" status (available for acceptance)

**Request Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body**: None

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Rides found!",
  "data": [
    {
      "rideId": "64a8f9b7c123456789abcd11",
      "userId": "64a8f9b7c123456789abcd00",
      "driverId": "",
      "createdDate": "2024-12-08T11:00:00",
      "rideStatus": "REQUESTED",
      "dropLocation": "789 Pine Road, Suburbs",
      "pickupLocation": "321 Elm Street, Midtown"
    },
    {
      "rideId": "64a8f9b7c123456789abcd22",
      "userId": "64a8f9b7c123456789abcd33",
      "driverId": "",
      "createdDate": "2024-12-08T11:15:00",
      "rideStatus": "REQUESTED",
      "dropLocation": "555 Beach Blvd, Coastal",
      "pickupLocation": "777 Mountain Ave, Highland"
    }
  ]
}
```

**Error Response - Unauthorized** (401 Unauthorized):
```json
{
  "success": false,
  "message": "Only drivers are allowed to accept rides!",
  "data": null
}
```

---

#### 6. Accept Ride

**Endpoint**: `POST /api/v1/driver/rides/{rideId}/accept`

**Description**: Driver accepts a requested ride

**Request Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**URL Parameters**:
- `rideId`: The ID of the ride to accept

**Request Body**: None

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Ride accepted successfully!",
  "data": {
    "rideId": "64a8f9b7c123456789abcd11",
    "userId": "64a8f9b7c123456789abcd00",
    "driverId": "64a8f9b7c123456789abcd01",
    "createdDate": "2024-12-08T11:00:00",
    "rideStatus": "ACCEPTED",
    "dropLocation": "789 Pine Road, Suburbs",
    "pickupLocation": "321 Elm Street, Midtown"
  }
}
```

**Error Response - Ride Not Found** (404 Not Found):
```json
{
  "success": false,
  "message": "Ride not found!",
  "data": null
}
```

**Error Response - Ride Already Accepted** (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride already accepted!",
  "data": null
}
```

**Error Response - Ride Already Completed** (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride already completed!",
  "data": null
}
```

**Error Response - Driver Already Assigned** (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride driver already exists!",
  "data": null
}
```

---

#### 7. Complete Ride

**Endpoint**: `POST /api/v1/driver/rides/{rideId}/complete`

**Description**: Driver marks an accepted ride as completed

**Request Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**URL Parameters**:
- `rideId`: The ID of the ride to complete

**Request Body**: None

**Success Response** (200 OK):
```json
{
  "success": true,
  "message": "Ride completed successfully!",
  "data": {
    "rideId": "64a8f9b7c123456789abcd11",
    "userId": "64a8f9b7c123456789abcd00",
    "driverId": "64a8f9b7c123456789abcd01",
    "createdDate": "2024-12-08T11:00:00",
    "rideStatus": "COMPLETED",
    "dropLocation": "789 Pine Road, Suburbs",
    "pickupLocation": "321 Elm Street, Midtown"
  }
}
```

**Error Response - Ride Not Found** (404 Not Found):
```json
{
  "success": false,
  "message": "Ride not found!",
  "data": null
}
```

**Error Response - Ride Not Accepted Yet** (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride not accepted yet!",
  "data": null
}
```

**Error Response - Ride Already Completed** (400 Bad Request):
```json
{
  "success": false,
  "message": "Ride already completed!",
  "data": null
}
```

**Error Response - Wrong Driver** (409 Conflict):
```json
{
  "success": false,
  "message": "You are not the driver!",
  "data": null
}
```

---

## Database Schema

### Users Collection

```javascript
{
  "_id": ObjectId,
  "username": String (unique),
  "password": String (bcrypt hashed),
  "role": String ("USER_ROLE" | "DRIVER_ROLE"),
  "refreshToken": String,
  "createdAt": DateTime,
  "updatedAt": DateTime
}
```

### Rides Collection

```javascript
{
  "_id": ObjectId,
  "rideId": String,
  "userId": String (reference to User),
  "driverId": String (reference to User, initially empty),
  "pickupLocation": String,
  "dropLocation": String,
  "rideStatus": String ("REQUESTED" | "ACCEPTED" | "COMPLETED"),
  "createdAt": DateTime
}
```

## Security

### JWT Token Authentication

- **Access Token**: Short-lived token (15 minutes default) used for API authentication
- **Refresh Token**: Long-lived token (7 days default) used to obtain new access tokens
- Tokens are signed using HMAC SHA-256 algorithm
- Passwords are encrypted using BCrypt

### Protected Routes

All routes except `/auth/**` require valid JWT authentication.

### Authorization

- **Passenger Routes** (`/api/v1/rides`, `/api/v1/user/rides`): Require `USER_ROLE`
- **Driver Routes** (`/api/v1/driver/**`): Require `DRIVER_ROLE`

## Error Handling

The application provides comprehensive error handling with the following error types:

### Validation Errors (400 Bad Request)
```json
{
  "error": "VALIDATION_ERROR",
  "message": {
    "field1": "error message 1",
    "field2": "error message 2"
  },
  "timestamp": "2024-12-08T10:30:00.000Z"
}
```

### Not Found Errors (404 Not Found)
```json
{
  "error": "NOT_FOUND_ERROR",
  "message": "Resource not found message",
  "timestamp": "2024-12-08T10:30:00.000Z"
}
```

### Bad Request Errors (400 Bad Request)
```json
{
  "error": "BAD_REQUEST",
  "message": "Error message",
  "timestamp": "2024-12-08T10:30:00.000Z"
}
```

### Authentication Errors (401 Unauthorized)
```json
{
  "success": false,
  "message": "Authentication Failed",
  "data": null
}
```

## Project Structure

```
src/
├── main/
│   ├── java/com/vimal/uber/
│   │   ├── config/           # Configuration classes
│   │   ├── controllers/      # REST controllers
│   │   ├── dtos/            # Data Transfer Objects
│   │   ├── enums/           # Enum types
│   │   ├── exceptions/      # Custom exceptions
│   │   ├── helpers/         # Helper utilities
│   │   ├── models/          # Domain models
│   │   ├── repositories/    # Data access layer
│   │   ├── security/        # Security configurations
│   │   ├── services/        # Business logic
│   │   └── utils/           # Utility classes
│   └── resources/
│       ├── application.yaml  # Application configuration
│       └── META-INF/
└── test/                     # Test classes
```

## Building for Production

```bash
mvn clean package
```

The JAR file will be created in the `target/` directory.

## Running in Production

```bash
java -jar target/uber-0.0.1-SNAPSHOT.jar
```

## Maintainer

**Vimal Kumar Yadav**  
Email: vimalyadavkr001@gmail.com

## Version

Current Version: **1.0.0**