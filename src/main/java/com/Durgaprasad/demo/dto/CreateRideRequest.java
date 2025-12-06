package com.Durgaprasad.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateRideRequest {

    @NotBlank
    private String pickupLocation;

    @NotBlank
    private String dropLocation;

    public CreateRideRequest() {}

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }
}
