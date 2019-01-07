package com.example.paulbreugnot.lightroom.light;

public class JsonColor {
    private float hue;
    private float saturation;
    private float value;
    private int argb;

    public JsonColor(float hue, float saturation, float value, int argb) {
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
        this.argb = argb;
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getValue() {
        return value;
    }

    public int getArgb() {
        return argb;
    }

}

