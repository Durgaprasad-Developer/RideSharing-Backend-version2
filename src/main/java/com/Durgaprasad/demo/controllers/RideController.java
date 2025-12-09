package com.Durgaprasad.demo.controllers;

import com.Durgaprasad.demo.models.Ride;
import com.Durgaprasad.demo.services.RideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    @Autowired
    private RideService rideService;

    @PostMapping
    public ResponseEntity<?> createRide(@Valid @RequestBody Ride req, Authentication auth){
        String username = auth.getName();
        return ResponseEntity.ok(rideService.createRide(username, req));
    }

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<?> completeRide(@PathVariable String rideId){
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRides(@PathVariable String userId){
        return ResponseEntity.ok(rideService.getRidesByUser(userId));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getUserRidesByStatus(@PathVariable String userId, @PathVariable String status){
        return ResponseEntity.ok(rideService.getRidesByUserAndStatus(userId, status));
    }

    @GetMapping("/driver/{driverId}/active-rides")
    public ResponseEntity<?> getDriverActive(@PathVariable String driverId){
        return ResponseEntity.ok(rideService.getDriverActiveRides(driverId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String text){
        return ResponseEntity.ok(rideService.searchByPickupOrDrop(text));
    }

    @GetMapping("/filter-distance")
    public ResponseEntity<?> filterDistance(@RequestParam Double min, @RequestParam Double max){
        return ResponseEntity.ok(rideService.filterByDistance(min, max));
    }

    @GetMapping("/filter-date-range")
    public ResponseEntity<?> filterDateRange(@RequestParam String start, @RequestParam String end){
        Instant s = Instant.parse(start);
        Instant e = Instant.parse(end);
        return ResponseEntity.ok(rideService.filterByDateRange(s, e));
    }

    @GetMapping("/sort")
    public ResponseEntity<?> sortByFare(@RequestParam(required = false, defaultValue = "asc") String order){
        return ResponseEntity.ok(rideService.sortByFare(order));
    }

    @GetMapping("/filter-status")
    public ResponseEntity<?> filterStatus(@RequestParam String status, @RequestParam String search){
        return ResponseEntity.ok(rideService.filterStatusWithSearch(status, search));
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<?> advancedSearch(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return ResponseEntity.ok(rideService.advancedSearch(search, status, sort, order, page, size));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<?> ridesByDate(@PathVariable String date){
        LocalDate d = LocalDate.parse(date);
        return ResponseEntity.ok(rideService.getRidesByDate(d));
    }
}
