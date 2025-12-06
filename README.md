# 🚕 RideShare Backend — Simple Documentation

A small backend project where users can request rides and drivers can accept them. Built using **Spring Boot**, **MongoDB**, and **JWT authentication**.

---

## 📌 What This Project Does

* Users can register and log in
* Passengers can request a ride
* Drivers can view pending ride requests
* Drivers can accept rides
* Both can complete a ride
* All secure using JWT tokens

---

## 🛠 Tech Used

* Spring Boot
* MongoDB (Atlas)
* Spring Security + JWT
* Java 17+

---

## 📁 Main Folders

```
config/        → security + JWT
controller/    → all API endpoints
service/       → business logic
repository/    → MongoDB operations
model/         → database entities
dto/           → request data
exception/     → error handling
```

---

## 👥 User Roles

* `ROLE_USER` → Passenger
* `ROLE_DRIVER` → Driver

---

## 📄 Important API Endpoints

### Public

* **POST** `/api/auth/register` — create account
* **POST** `/api/auth/login` — login + get JWT

### Passenger

* **POST** `/api/v1/rides` — request a ride
* **GET** `/api/v1/user/rides` — view own rides

### Driver

* **GET** `/api/v1/driver/rides/requests` — view pending rides
* **POST** `/api/v1/driver/rides/{id}/accept` — accept ride

### Shared

* **POST** `/api/v1/rides/{id}/complete` — complete ride

---

## 🔐 Authentication

Send this in every protected request:

```
Authorization: Bearer <token>
```

---

## 🗄 Database Models

### User

```
id, username, password, role
```

### Ride

```
id, userId, driverId, pickup, drop, status, createdAt
```

---

## ✔ Status Flow

`REQUESTED → ACCEPTED → COMPLETED`
