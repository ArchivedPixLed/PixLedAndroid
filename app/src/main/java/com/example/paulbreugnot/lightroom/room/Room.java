package com.example.paulbreugnot.lightroom.room;

import com.example.paulbreugnot.lightroom.utils.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Room {

    private long id;
    private String name;
    private int floor;
    private Status status;
    private long buildingId;

    @JsonCreator
    public Room(@JsonProperty("id") long id,
                @JsonProperty("name") String name,
                @JsonProperty("floor") int floor,
                @JsonProperty("buildingId") long buildingId,
                @JsonProperty("status") String status) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.buildingId = buildingId;
        this.status = status.equals("ON") ? Status.ON : Status.OFF;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(long buildingId) {
        this.buildingId = buildingId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
