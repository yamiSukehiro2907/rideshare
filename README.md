# RideShare Backend API 🚗

A ride-sharing backend service built with Spring Boot, MongoDB, and JWT authentication implementing role-based access
control for passengers and drivers.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Security Implementation](#security-implementation)

## ✨ Features

- User registration and JWT authentication
- Role-based access control (DRIVER, USER)
- BCrypt password encryption
- Ride lifecycle management (Request → Accept → Complete)
- Global exception handling
- Input validation with Jakarta Bean Validation
- Custom MongoDB repository implementation
- Role validation using enum helper method

## 🛠 Tech Stack

- **Framework:** Spring Boot 4.0.0
- **Database:** MongoDB
- **Security:** Spring Security + JWT
- **Build Tool:** Maven
- **Java Version:** 25

## 📁 Project Structure

```
src/main/java/com/vimal/uber/
├── config/              # Security, MongoDB configuration
├── controllers/         # REST endpoints
├── dtos/                # Data Transfer Objects
├── enums/               # Role, RideStatus enums
├── exceptions/          # Global exception handling
├── helpers/             # Utility helpers
├── models/              # MongoDB entities (User, Ride)
├── repositories/        # Custom MongoDB repositories
├── security/            # Custom authentication & user details
├── services/            # Business logic
└── utils/               # JWT utilities
```

## 🚀 Getting Started

### Prerequisites

- Java 25
- MongoDB
- Maven

### Environment Setup

Create `.env` file:

```env
MONGODB_URI=mongodb://localhost:27017
MONGODB_DB=rideshare

ACCESS_TOKEN_SECRET=your_secret_minimum_32_characters
REFRESH_TOKEN_SECRET=your_secret_minimum_32_characters
ACCESS_TOKEN_EXPIRATION=3600000
REFRESH_TOKEN_EXPIRATION=86400000
```

### Run Application

```bash
mvn clean install
mvn spring-boot:run
```

Server starts at `http://localhost:8081`

## 🔌 API Endpoints

### Authentication (Public)

| Method | Endpoint         | Description          |
|--------|------------------|----------------------|
| POST   | `/auth/register` | Register user/driver |
| POST   | `/auth/login`    | Login and get JWT    |

### Passenger (USER_ROLE)

| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| POST   | `/api/v1/rides`      | Create ride request |
| GET    | `/api/v1/user/rides` | View ride history   |

### Driver (DRIVER_ROLE)

| Method | Endpoint                             | Description        |
|--------|--------------------------------------|--------------------|
| GET    | `/api/v1/driver/rides/requests`      | View pending rides |
| POST   | `/api/v1/driver/rides/{id}/accept`   | Accept ride        |
| POST   | `/api/v1/driver/rides/{id}/complete` | Complete ride      |

### Example Usage

**Register:**

```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123","role":"USER_ROLE"}'
```

**Login:**

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
```

**Create Ride:**

```bash
curl -X POST http://localhost:8081/api/v1/rides \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"pickupLocation":"Koramangala","dropLocation":"Indiranagar"}'
```

## 🔐 Security Implementation

### Custom Spring Security Setup

**1. Custom Authentication Provider**

- Validates credentials against MongoDB
- Provides detailed error messages (locked account, disabled, expired)
- Uses BCrypt for password verification

**2. JWT Filter (`JwtFilter`)**

- Extends `OncePerRequestFilter`
- Validates JWT on every request
- Bypasses `/auth/**` endpoints
- Extracts user details and sets authentication context

**3. Custom User Details Service**

- Implements `UserDetailsService`
- Loads users from MongoDB by username or ID
- Wraps User entity in `CustomUserDetails`

**4. Security Configuration**

- Stateless session management
- CSRF disabled for API usage
- Role-based endpoint protection
- JWT filter applied before authentication

### Authentication Flow

```
1. User registers → Password encrypted with BCrypt
2. User logs in → Custom Authentication Provider validates
3. JWT tokens generated → Access + Refresh tokens returned
4. Client sends token → JWT Filter validates on each request
5. User details loaded → Authorization checked in controllers
```

### Response Format

**Success:**

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    ...
  }
}
```

**Error:**

```json
{
  "error": "ERROR_TYPE",
  "message": "Error description",
  "timestamp": "2025-12-08T10:30:00Z"
}
```

## 👤 Maintainer

**Vimal Kumar Yadav**  
Email: vimalyadavkr001@gmail.com

---