package com.viktor.dag1.dto;

import java.time.LocalDate;

public class AverageDTO {

    private LocalDate date;
    private int hour;
    private float average;

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

    public float getAverage() {
        return average;
    }

    public void setAverage(float temperature) {
        this.average = temperature;
    }

}
