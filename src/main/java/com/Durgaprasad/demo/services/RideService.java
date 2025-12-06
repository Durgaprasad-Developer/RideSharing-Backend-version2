package com.Durgaprasad.demo.services;

import com.Durgaprasad.demo.dto.CreateRideRequest;
import com.Durgaprasad.demo.exceptions.BadRequestException;
import com.Durgaprasad.demo.exceptions.NotFoundException;
import com.Durgaprasad.demo.models.Ride;
import com.Durgaprasad.demo.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    public Ride createRide(CreateRideRequest req, String userId){
        Ride r = new Ride();
        r.setUserId(userId);
        r.setPickupLocation(req.getPickupLocation());
        r.setDropLocation(req.getDropLocation());
        r.setStatus("REQUESTED");
        r.setCreatedAt(Instant.now());
        return rideRepository.save(r);
    }

    public List<Ride> getPendingRequests(){
        return rideRepository.findByStatus("REQUESTED");
    }

    public Ride acceptRide(String rideId, String driverId){
        Ride r = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if(!r.getStatus().equals("REQUESTED")){
            throw new BadRequestException("Ride must be REQUESTED");
        }

        r.setDriverId(driverId);
        r.setStatus("ACCEPTED");
        return rideRepository.save(r);
    }

    public Ride completeRide(String rideId){
        Ride r = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if(!r.getStatus().equals("ACCEPTED")){
            throw new BadRequestException("Ride must be ACCEPTED");
        }

        r.setStatus("COMPLETED");
        return rideRepository.save(r);
    }

    public List<Ride> getRidesByUser(String userId){
        return rideRepository.findByUserId(userId);
    }
}
