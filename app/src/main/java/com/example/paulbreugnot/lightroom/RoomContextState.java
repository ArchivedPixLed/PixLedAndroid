package com.example.paulbreugnot.lightroom;

public class RoomContextState {

    public static final String ON = "on";
    public static final String OFF = "off";

    private String room;
    private String lightStatus;
    private int light;
    private float noise;

    public RoomContextState(String room, String lightStatus, int light, float noise) {
        super();
        this.room = room;
        this.lightStatus = lightStatus;
        this.light = light;
        this.noise = noise;
    }

    public String getRoom() {
        return this.room;
    }

    public String getLightStatus() {
        return this.lightStatus;
    }

    public int getLight() {
        return this.light;
    }

    public float getNoise() {
        return this.noise;
    }
}
