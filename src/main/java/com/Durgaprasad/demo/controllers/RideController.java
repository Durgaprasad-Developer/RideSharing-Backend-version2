package com.Durgaprasad.demo.controllers;

import com.Durgaprasad.demo.dto.CreateRideRequest;
import com.Durgaprasad.demo.services.RideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RideController {

    @Autowired
    private RideService rideService;

    @PostMapping("/rides")
    public ResponseEntity<?> createRide(@Valid @RequestBody CreateRideRequest req, Authentication auth){
        String username = auth.getName();
        return ResponseEntity.ok(rideService.createRide(req, username));
    }

    @PostMapping("/rides/{rideId}/complete")
    public ResponseEntity<?> complete(@PathVariable String rideId){
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @GetMapping("/user/rides")
    public ResponseEntity<?> getMyRides(Authentication auth){
        String username = auth.getName();
        return ResponseEntity.ok(rideService.getRidesByUser(username));
    }
}
