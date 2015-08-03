package com.github.mmonkey.Destinations.Models;

import java.sql.Timestamp;

public class BackModel {

    private DestinationModel destination;
    private Timestamp createdOn;

    public DestinationModel getDestination() {
        return this.destination;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public BackModel(DestinationModel destination, Timestamp createdOn) {
        this.destination = destination;
        this.createdOn = createdOn;
    }
}
