package com.Durgaprasad.demo.services;

import com.Durgaprasad.demo.models.Ride;
import org.bson.Document;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class RideQueryService {

    private final MongoTemplate mongoTemplate;

    public RideQueryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Ride> searchByPickupOrDrop(String text) {
        Criteria c1 = Criteria.where("pickupLocation").regex(text, "i");
        Criteria c2 = Criteria.where("dropLocation").regex(text, "i");
        Query q = new Query(new Criteria().orOperator(c1, c2));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> filterByDistance(Double min, Double max) {
        Query q = new Query(Criteria.where("distanceKm").gte(min).lte(max));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> filterByDateRange(Instant start, Instant end) {
        Query q = new Query(Criteria.where("createdAt").gte(start).lte(end));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> sortByFare(String order) {
        Sort.Direction dir = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Query q = new Query().with(Sort.by(dir, "fare"));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> getRidesForUser(String userId) {
        Query q = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> getRidesForUserByStatus(String userId, String status) {
        Query q = new Query(Criteria.where("userId").is(userId).and("status").is(status));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> getDriverActiveRides(String driverId) {
        Query q = new Query(Criteria.where("driverId").is(driverId).and("status").is("ACCEPTED"));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> filterStatusWithKeyword(String status, String text) {
        Criteria statusCrit = Criteria.where("status").is(status);
        Criteria pickup = Criteria.where("pickupLocation").regex(text, "i");
        Criteria drop = Criteria.where("dropLocation").regex(text, "i");
        Query q = new Query(new Criteria().andOperator(statusCrit, new Criteria().orOperator(pickup, drop)));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> advancedSearch(String search, String status, String sortField, String order, int page, int size) {
        Query q = new Query();
        if (search != null && !search.isBlank()) {
            Criteria pickup = Criteria.where("pickupLocation").regex(search, "i");
            Criteria drop = Criteria.where("dropLocation").regex(search, "i");
            q.addCriteria(new Criteria().orOperator(pickup, drop));
        }
        if (status != null && !status.isBlank()) {
            q.addCriteria(Criteria.where("status").is(status));
        }
        Sort.Direction dir = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        q.with(PageRequest.of(page, size, Sort.by(dir, sortField == null ? "createdAt" : sortField)));
        return mongoTemplate.find(q, Ride.class);
    }

    public List<Ride> ridesByDate(LocalDate date) {
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return filterByDateRange(start, end);
    }

    public List<Document> ridesPerDay() {
        ProjectionOperation proj = project().andExpression("dateToString('%Y-%m-%d',$createdAt)").as("day");
        GroupOperation group = group("day").count().as("count");
        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "_id"));
        Aggregation agg = newAggregation(proj, group, sort);
        return mongoTemplate.aggregate(agg, "rides", Document.class).getMappedResults();
    }

    public Document driverSummary(String driverId) {
        MatchOperation match = match(Criteria.where("driverId").is(driverId));
        GroupOperation group = group().count().as("totalRides")
                .sum(ConditionalOperators.when(Criteria.where("status").is("COMPLETED")).then(1).otherwise(0)).as("completedRides")
                .avg("distanceKm").as("avgDistance")
                .sum("fare").as("totalFare");
        Aggregation agg = newAggregation(match, group);
        return mongoTemplate.aggregate(agg, "rides", Document.class).getUniqueMappedResult();
    }

    public Document userSpending(String userId) {
        MatchOperation match = match(Criteria.where("userId").is(userId).and("status").is("COMPLETED"));
        GroupOperation group = group().count().as("completedRides").sum("fare").as("totalSpent");
        Aggregation agg = newAggregation(match, group);
        return mongoTemplate.aggregate(agg, "rides", Document.class).getUniqueMappedResult();
    }

    public List<Document> statusSummary() {
        GroupOperation group = group("status").count().as("count");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, "rides", Document.class).getMappedResults();
    }
}
