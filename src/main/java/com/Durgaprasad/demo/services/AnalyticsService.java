package com.Durgaprasad.demo.services;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;
// ...existing code...
import org.springframework.data.mongodb.core.query.Criteria; // added
import org.springframework.data.domain.Sort; // added
// ...existing code...

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class AnalyticsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Double totalEarnings(String driverId){
        MatchOperation match = match(Criteria.where("driverId").is(driverId).and("status").is("COMPLETED"));
        GroupOperation group = group().sum("fare").as("total");
        Aggregation agg = newAggregation(match, group);
        Document res = mongoTemplate.aggregate(agg, "rides", Document.class).getUniqueMappedResult();
        return res != null ? res.getDouble("total") : 0.0;
    }

    public List<Document> ridesPerDay(){
        ProjectionOperation proj = project()
                .andExpression("dateToString('%Y-%m-%d', $createdAt)").as("day");
        GroupOperation group = group("day").count().as("count");
        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "_id"));
        Aggregation agg = newAggregation(proj, group, sort);
        return mongoTemplate.aggregate(agg, "rides", Document.class).getMappedResults();
    }

    public Map<String, Object> driverSummary(String driverId){
        MatchOperation match = match(Criteria.where("driverId").is(driverId));
        GroupOperation group = group().count().as("totalRides")
                .sum(ConditionalOperators.when(Criteria.where("status").is("COMPLETED")).then(1).otherwise(0)).as("completedRides")
                .avg("distanceKm").as("avgDistance")
                .sum("fare").as("totalFare");
        Aggregation agg = newAggregation(match, group);
        Document res = mongoTemplate.aggregate(agg, "rides", Document.class).getUniqueMappedResult();
        return res != null ? res : Map.of("totalRides",0,"completedRides",0,"avgDistance",0.0,"totalFare",0.0);
    }

    public Map<String, Object> userSpending(String userId){
        MatchOperation match = match(Criteria.where("userId").is(userId).and("status").is("COMPLETED"));
        GroupOperation group = group().count().as("completedRides").sum("fare").as("totalSpent");
        Aggregation agg = newAggregation(match, group);
        Document res = mongoTemplate.aggregate(agg, "rides", Document.class).getUniqueMappedResult();
        return res != null ? res : Map.of("completedRides",0,"totalSpent",0.0);
    }

    public List<Document> statusSummary(){
        GroupOperation group = group("status").count().as("count");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, "rides", Document.class).getMappedResults();
    }
}
