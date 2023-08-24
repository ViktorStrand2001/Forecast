package com.viktor.dag1.services;

import com.viktor.dag1.models.Forecast;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ForecastService{

    private static List<Forecast> forecasts = new ArrayList<>();

    public List<Forecast> getForecasts(){
        return forecasts;
    }

    public void add(Forecast forecast) {
        forecasts.add(forecast);
    }

    public Forecast getByIndex(int i) {
        return forecasts.get(i);
    }

    public void update (Forecast forecast) {
    }

}
