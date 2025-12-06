package com.Durgaprasad.demo.controllers;

import com.Durgaprasad.demo.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/driver")
public class DriverController {

    @Autowired
    private RideService rideService;

    @GetMapping("/rides/requests")
    public ResponseEntity<?> requests(){
        return ResponseEntity.ok(rideService.getPendingRequests());
    }

    @PostMapping("/rides/{rideId}/accept")
    public ResponseEntity<?> accept(@PathVariable String rideId, Authentication auth){
        String driver = auth.getName();
        return ResponseEntity.ok(rideService.acceptRide(rideId, driver));
    }
}
