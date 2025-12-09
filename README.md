🚖 RideShare Backend — README.md

A clean, modular Spring Boot + MongoDB + JWT backend for a mini Uber-like ride-sharing system.

📌 Overview

This backend supports:

🔐 User Authentication (JWT)

👤 Passengers create ride requests

👨‍✈️ Drivers accept + complete rides

🔍 Searching, filtering, sorting rides

📊 Analytics using MongoDB Aggregation Pipelines

🛠 Tech Stack
Layer	Technology
Backend	Spring Boot 3
Auth	JWT (HS256)
Database	MongoDB Atlas
Security	Spring Security
Build Tool	Maven
Java	21
📁 Folder Structure
src/main/java/com.example.rideshare
│
├── config/
│   ├── SecurityConfig.java
│   └── JwtFilter.java
│
├── controllers/
│   ├── AuthController.java
│   ├── RideController.java
│   ├── RideQueryController.java
│   └── AnalyticsController.java
│
├── models/
│   ├── User.java
│   └── Ride.java
│
├── repository/
│   ├── UserRepository.java
│   └── RideRepository.java
│
├── services/
│   ├── AuthService.java
│   ├── RideService.java
│   ├── RideQueryService.java
│   └── AnalyticsService.java
│
└── utils/
    └── JwtUtil.java

🚀 Setup Instructions
1️⃣ Clone the repository
git clone <repo-url>
cd RideShareBackend

2️⃣ Configure application.properties
server.port=8081

spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster.mongodb.net/rideshare_db

app.jwt.secret=mysuperlongsecretkeymysuperlongsecretkeymysuperlongsecretkey
app.jwt.expiration-ms=3600000

3️⃣ Run the backend
./mvnw spring-boot:run


Backend runs on:

http://localhost:8081

🔐 Authentication (JWT)

All protected endpoints require:

Authorization: Bearer <token>

🧑 AUTH ENDPOINTS
1. Register
POST /api/auth/register
Request
{
  "username": "ram",
  "password": "1234",
  "role": "USER"
}

Response
{
  "username": "ram",
  "role": "USER"
}

2. Login
POST /api/auth/login
Request
{
  "username": "ram123",
  "password": "1234"
}

Response
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "ram123",
  "role": "USER"
}

🚗 RIDE ENDPOINTS (Core Workflow)
3. Create Ride
POST /api/v1/rides
Request
{
  "pickupLocation": "Hyderabad",
  "dropLocation": "Bangalore",
  "fare": 1500,
  "distanceKm": 600
}

Response (REAL example)
{
  "id": "6937f7781ba9b7280ca749d4",
  "userId": "ram123",
  "driverId": null,
  "pickupLocation": "Hyderabad",
  "dropLocation": "Bangalore",
  "fare": 1500.0,
  "distanceKm": 600.0,
  "status": "REQUESTED",
  "createdAt": "2025-12-09T10:18:32.156613618Z"
}

4. Accept Ride (Driver)
POST /api/v1/driver/rides/{rideId}/accept
Response
{
  "id": "6937f7781ba9b7280ca749d4",
  "driverId": "driver001",
  "status": "ACCEPTED"
}

5. Complete Ride
POST /api/v1/rides/{rideId}/complete
Response
{
  "id": "6937f7781ba9b7280ca749d4",
  "status": "COMPLETED"
}

👤 USER QUERIES
6. Get All Rides for User
GET /api/v1/rides/user/{userId}
Response
[
  {
    "id": "6937f7781ba9b7280ca749d4",
    "status": "REQUESTED",
    "pickupLocation": "Hyderabad"
  }
]

7. Get User Rides by Status
GET /api/v1/rides/user/{userId}/status/{status}
Response
[
  {
    "id": "6937f7781ba9b7280ca749d4",
    "status": "REQUESTED"
  }
]

👨‍✈️ DRIVER QUERIES
8. Pending Ride Requests
GET /api/v1/driver/rides/requests
Response
[
  {
    "id": "6937f7781ba9b7280ca749d4",
    "status": "REQUESTED"
  }
]

9. Driver Active Rides
GET /api/v1/driver/{driverId}/active-rides
Response
[
  {
    "id": "6937f9123be21a1a0c987abc",
    "status": "ACCEPTED"
  }
]

🔍 SEARCH / FILTER / SORT ENDPOINTS
10. Search Ride
GET /api/v1/rides/search?text=hyd
Response
[
  {
    "pickupLocation": "Hyderabad",
    "dropLocation": "Bangalore"
  }
]

11. Filter by Distance
GET /api/v1/rides/filter-distance?min=10&max=50
Response
[
  {
    "distanceKm": 35,
    "pickupLocation": "Madhapur"
  }
]

12. Filter by Date Range
GET /api/v1/rides/filter-date-range?start=...&end=...
Response
[
  {
    "createdAt": "2025-01-05T09:00:00Z"
  }
]

13. Sort by Fare
GET /api/v1/rides/sort?order=desc
Response
[
  { "fare": 2000 },
  { "fare": 1500 },
  { "fare": 500 }
]

14. Filter by Status + Search
GET /api/v1/rides/filter-status?status=REQUESTED&search=hyd
Response
[
  {
    "status": "REQUESTED",
    "pickupLocation": "Hyderabad"
  }
]

15. Advanced Search
GET /api/v1/rides/advanced-search?search=hyd&status=COMPLETED&sort=fare&order=asc&page=0&size=10
Response
[
  {
    "pickupLocation": "Hyderabad",
    "status": "COMPLETED",
    "fare": 1200
  }
]

16. Rides by Date
GET /api/v1/rides/date/2025-12-09
Response
[
  { "id": "6937f7781ba9b7280ca749d4" }
]

📊 ANALYTICS ENDPOINTS
17. Rides Per Day
GET /api/v1/analytics/rides-per-day
Response
[
  { "_id": "2025-12-09", "count": 5 }
]

18. Driver Summary
GET /api/v1/analytics/driver/{driverId}/summary
Response
{
  "totalRides": 15,
  "completedRides": 12,
  "avgDistance": 8.3,
  "totalFare": 5600
}

19. User Spending
GET /api/v1/analytics/user/{userId}/spending
Response
{
  "completedRides": 8,
  "totalSpent": 4100
}

20. Status Summary
GET /api/v1/analytics/status-summary
Response
[
  { "_id": "REQUESTED", "count": 4 },
  { "_id": "ACCEPTED", "count": 3 },
  { "_id": "COMPLETED", "count": 10 }
]

