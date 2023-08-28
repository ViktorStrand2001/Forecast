package com.viktor.dag1.models;

import java.util.Date;
import java.util.UUID;

public class Forecast {

    // variabels
    private UUID id;
    private String date;
    private int hour;
    private float temperature;

// Getters Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }


}
