package com.viktor.dag1.dto;

import java.time.LocalDate;

public class NewForecastDTO {
    // Data transfer object = DTO - labba med egenskaper
    private LocalDate date;
    private int hour;
    private float temperature;

    public NewForecastDTO(LocalDate date, int hour, float temperature) {
        this.date = date;
        this.hour = hour;
        this.temperature = temperature;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
