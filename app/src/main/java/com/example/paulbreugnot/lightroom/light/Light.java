package com.example.paulbreugnot.lightroom.light;

import android.graphics.Color;

import com.example.paulbreugnot.lightroom.utils.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Light {

    private long id;
    private int level;
    private Status status;
    private int color = Color.GREEN;
    private long roomId;
    @JsonCreator
    public Light(@JsonProperty("id") long id,
                @JsonProperty("level") int level,
                @JsonProperty("status") String status,
                @JsonProperty("roomId") long roomId) {
        this.id = id;
        this.level = level;
        this.status = status.equals("ON") ? Status.ON : Status.OFF;
        this.roomId = roomId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void switchLight() {
        if (status == Status.ON) {
            status = Status.OFF;
        }
        else {
            status = Status.ON;
        }
    }
}
