package com.Durgaprasad.demo.controllers;

import com.Durgaprasad.demo.services.AnalyticsService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/driver/{driver}/earnings")
    public Double driverEarnings(@PathVariable String driver){
        return analyticsService.totalEarnings(driver);
    }

    @GetMapping("/rides-per-day")
    public List<Document> ridesPerDay(){
        return analyticsService.ridesPerDay();
    }

    @GetMapping("/driver/{driver}/summary")
    public Map<String, Object> driverSummary(@PathVariable String driver){
        return analyticsService.driverSummary(driver);
    }

    @GetMapping("/user/{user}/spending")
    public Map<String, Object> userSpending(@PathVariable String user){
        return analyticsService.userSpending(user);
    }

    @GetMapping("/status-summary")
    public List<Document> statusSummary(){
        return analyticsService.statusSummary();
    }
}
