package com.viktor.dag1.dto;

import com.viktor.dag1.models.DataSource;

import java.time.LocalDate;
import java.util.UUID;

public class ForecastListDTO {
    public UUID Id;
    public LocalDate predictionDatum;
    public int predictionHour; //8
    public float predictionTemperature;
    public boolean rainOrSnow;
    public DataSource dataSource;
}
