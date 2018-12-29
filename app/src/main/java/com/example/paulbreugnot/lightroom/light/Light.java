package com.example.paulbreugnot.lightroom.light;

import android.graphics.Color;

import com.example.paulbreugnot.lightroom.utils.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Light {

    private long id;
    private int level;
    private Status status;
    private float hue;
    private float saturation;
    private float value;
    private boolean connected;
    private long roomId;

    @JsonCreator
    public Light(@JsonProperty("id") long id,
                @JsonProperty("level") int level,
                @JsonProperty("status") String status,
                @JsonProperty("hue") float hue,
                 @JsonProperty("saturation") float saturation,
                 @JsonProperty("value") float value,
                @JsonProperty("connected") boolean connected,
                @JsonProperty("roomId") long roomId) {
        this.id = id;
        this.level = level;
        this.status = status.equals("ON") ? Status.ON : Status.OFF;
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
        this.connected = connected;
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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

//    public int getColor() {
//        return color;
//    }
//
//    public void setColor(int color) {
//        this.color = color;
//    }

//    public float getLightness() {
//        float[] hsv = new float[3];
//        Color.colorToHSV(color, hsv);
//        return hsv[2];
//    }
//
//    public void setLightness(float lightness) {
//        // Dirty hack to ensure lightness never falls to 0.
//        // (Otherwise, previous hue and value are lost when lightness increase again)
//        float correctedLightness = lightness==0 ? 0.01F : lightness;
//
//        float[] newHsvColor = new float[3];
//        Color.colorToHSV(color, newHsvColor);
//        newHsvColor[2] = correctedLightness;
//        color = Color.HSVToColor(newHsvColor);
//    }


    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getArgbColor() {
        float[] hsv = {hue, saturation, value};
        return Color.HSVToColor(hsv);
    }

    public int getColorWithMaxValue() {
        float[] hsv = {hue, saturation, 1};
        return Color.HSVToColor(hsv);
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
