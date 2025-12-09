package com.Durgaprasad.demo.repository;

import com.Durgaprasad.demo.models.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RideRepository extends MongoRepository<Ride, String> {
    List<Ride> findByStatus(String status);
    List<Ride> findByUserId(String userId);
    List<Ride> findByUserIdAndStatus(String userId, String status);
    List<Ride> findByDriverIdAndStatus(String driverId, String status);
    List<Ride> findByDriverId(String driverId);
}
