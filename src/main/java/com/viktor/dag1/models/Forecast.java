package com.viktor.dag1.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Forecast {

    // variabels
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    private LocalDate created;
    private LocalDate updated;
    private float longitude;
    private float latitude;
    private LocalDate predictionDatum;
    private int predictionHour; //8
    private float predictionTemperature;
    private boolean rainOrSnow;
    private DataSource dataSource;

    public Forecast(UUID id) {
        this.id = id;
        this.created = LocalDate.now();
    }

    public Forecast() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public LocalDate getPredictionDatum() {
        return predictionDatum;
    }

    public void setPredictionDatum(LocalDate predictionDatum) {
        this.predictionDatum = predictionDatum;
    }

    public int getPredictionHour() {
        return predictionHour;
    }

    public void setPredictionHour(int predictionHour) {
        this.predictionHour = predictionHour;
    }

    public float getPredictionTemperature() {
        return predictionTemperature;
    }

    public void setPredictionTemperature(float predictionTemperature) {
        this.predictionTemperature = predictionTemperature;
    }

    public boolean isRainOrSnow() {
        return rainOrSnow;
    }

    public void setRainOrSnow(boolean rainOrSnow) {
        this.rainOrSnow = rainOrSnow;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public LocalDate getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDate updated) {
        this.updated = updated;
    }
}
