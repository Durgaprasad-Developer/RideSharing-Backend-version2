package com.Durgaprasad.demo.controllers;

import com.Durgaprasad.demo.models.Ride;
import com.Durgaprasad.demo.services.RideQueryService;
import org.bson.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rides/query")
public class RideQueryController {

    private final RideQueryService rideQueryService;

    public RideQueryController(RideQueryService rideQueryService) {
        this.rideQueryService = rideQueryService;
    }

    @GetMapping("/search")
    public List<Ride> search(@RequestParam String text) {
        return rideQueryService.searchRides(text);
    }

    @GetMapping("/filter-distance")
    public List<Ride> filterDistance(@RequestParam Double min, @RequestParam Double max) {
        return rideQueryService.filterByDistance(min, max);
    }

    @GetMapping("/filter-date-range")
    public List<Ride> filterDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return rideQueryService.filterByDateRange(start, end);
    }

    @GetMapping("/sort")
    public List<Ride> sortByFare(@RequestParam(required = false, defaultValue = "asc") String order) {
        return rideQueryService.sortByFare(order);
    }

    @GetMapping("/user/{userId}")
    public List<Ride> ridesForUser(@PathVariable String userId) {
        return rideQueryService.getRidesForUser(userId);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public List<Ride> ridesForUserByStatus(@PathVariable String userId, @PathVariable String status) {
        return rideQueryService.getRidesForUserByStatus(userId, status);
    }

    @GetMapping("/driver/{driverId}/active-rides")
    public List<Ride> driverActiveRides(@PathVariable String driverId) {
        return rideQueryService.getDriverActiveRides(driverId);
    }

    @GetMapping("/filter-status")
    public List<Ride> filterStatusAndSearch(@RequestParam String status, @RequestParam String search) {
        return rideQueryService.filterByStatusAndSearch(status, search);

    }

    @GetMapping("/advanced-search")
    public List<Ride> advancedSearch(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return rideQueryService.advancedSearch(search, status, sort, order, page, size);
    }

    @GetMapping("/date/{date}")
    public List<Ride> ridesByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return rideQueryService.ridesByDate(date);
    }

    @GetMapping("/analytics/rides-per-day")
    public List<Document> ridesPerDay() {
        return rideQueryService.ridesPerDay();
    }

    @GetMapping("/analytics/driver/{driverId}/summary")
    public Document driverSummary(@PathVariable String driverId) {
        return rideQueryService.driverSummary(driverId);
    }

    @GetMapping("/analytics/user/{userId}/spending")
    public Document userSpending(@PathVariable String userId) {
        return rideQueryService.userSpending(userId);
    }

    @GetMapping("/analytics/status-summary")
    public List<Document> statusSummary() {
        return rideQueryService.statusSummary();
    }
}
