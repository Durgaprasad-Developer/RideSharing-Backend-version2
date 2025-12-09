package com.Durgaprasad.demo.services;

import com.Durgaprasad.demo.models.Ride;
import com.Durgaprasad.demo.repository.RideRepository;
import com.Durgaprasad.demo.exceptions.NotFoundException;
import com.Durgaprasad.demo.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Ride createRide(String userId, Ride ride){
        ride.setUserId(userId);
        ride.setStatus("REQUESTED");
        if (ride.getCreatedAt() == null) ride.setCreatedAt(Instant.now());
        return rideRepository.save(ride);
    }

    public List<Ride> getPendingRequests(){
        return rideRepository.findByStatus("REQUESTED");
    }

    public Ride acceptRide(String rideId, String driverId){
        Ride r = rideRepository.findById(rideId).orElseThrow(() -> new NotFoundException("Ride not found"));
        if (!"REQUESTED".equals(r.getStatus())) throw new BadRequestException("Ride must be REQUESTED to accept");
        r.setDriverId(driverId);
        r.setStatus("ACCEPTED");
        return rideRepository.save(r);
    }

    public Ride completeRide(String rideId){
        Ride r = rideRepository.findById(rideId).orElseThrow(() -> new NotFoundException("Ride not found"));
        if (!"ACCEPTED".equals(r.getStatus())) throw new BadRequestException("Ride must be ACCEPTED to complete");
        r.setStatus("COMPLETED");
        return rideRepository.save(r);
    }

    public List<Ride> getRidesByUser(String userId){
        return rideRepository.findByUserId(userId);
    }

    public List<Ride> searchByPickupOrDrop(String text){
        Criteria c1 = Criteria.where("pickupLocation").regex(text, "i");
        Criteria c2 = Criteria.where("dropLocation").regex(text, "i");
        Query q = new Query(new Criteria().orOperator(c1,c2));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> filterByDistance(Double min, Double max){
        Criteria c = Criteria.where("distanceKm").gte(min).lte(max);
        Query q = new Query(c);
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> filterByDateRange(Instant start, Instant end){
        Criteria c = Criteria.where("createdAt").gte(start).lte(end);
        Query q = new Query(c);
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> sortByFare(String order){
        Query q = new Query();
        q.with(Sort.by("fare"));
        if ("desc".equalsIgnoreCase(order)) q.with(Sort.by(Sort.Direction.DESC, "fare"));
        else q.with(Sort.by(Sort.Direction.ASC, "fare"));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> getRidesByUserAndStatus(String userId, String status){
        return rideRepository.findByUserIdAndStatus(userId, status);
    }

    public List<Ride> getDriverActiveRides(String driverId){
        return rideRepository.findByDriverIdAndStatus(driverId, "ACCEPTED");
    }

    public List<Ride> filterStatusWithSearch(String status, String text){
        Criteria statusCrit = Criteria.where("status").is(status);
        Criteria pickup = Criteria.where("pickupLocation").regex(text, "i");
        Criteria drop = Criteria.where("dropLocation").regex(text, "i");
        Query q = new Query(new Criteria().andOperator(statusCrit, new Criteria().orOperator(pickup, drop)));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> advancedSearch(String search, String status, String sortField, String order, int page, int size){
        Criteria c = new Criteria();
        if (search != null && !search.isBlank()){
            Criteria pickup = Criteria.where("pickupLocation").regex(search, "i");
            Criteria drop = Criteria.where("dropLocation").regex(search, "i");
            c = new Criteria().orOperator(pickup, drop);
        }
        if (status != null && !status.isBlank()){
            c = (c.getCriteriaObject().isEmpty()) ? Criteria.where("status").is(status) : c.and("status").is(status);
        }
        Query q = c.getCriteriaObject().isEmpty() ? new Query() : new Query(c);
        Sort.Direction dir = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        q.with(PageRequest.of(page, size, Sort.by(dir, sortField == null ? "createdAt" : sortField)));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> getRidesByDate(LocalDate date){
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return filterByDateRange(start, end);
    }
}
