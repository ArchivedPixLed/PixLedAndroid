package com.example.paulbreugnot.lightroom.light;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Color2Json {
    /*
    Class used by the Jackson ObjectMapper to generate JSON to send in change color queries.
     */
    private Color color;
    private ObjectMapper jsonMapper;

    public Color2Json(float hue, float saturation, float value, int argb) {
        color = new Color(hue, saturation, value, argb);
        jsonMapper = new ObjectMapper();
    }

    public String JsonString() {
        String json = "";
        try {
            json = jsonMapper.writeValueAsString(color);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }


    private class Color {
        private float hue;
        private float saturation;
        private float value;
        private int argb;

    public Color(float hue, float saturation, float value, int argb) {
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
}
