package com.viktor.dag1.controller;

import com.viktor.dag1.models.Forecast;
import com.viktor.dag1.services.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// client anropar /api/forecasts GET
// Spring kollar vilken funktion som hanterar detta /api/forecasts
// Spring anropar den funktionen
// VAR KOD KÖRS
// spring tar det som funktionen returner och gör till json
// spring skickar tillbaka Json till client

@RestController
public class ForecastController {

    @Autowired
    private ForecastService forecastService;

    @GetMapping("/api/forecasts")
    public ResponseEntity<List<Forecast>> GetAll() {
        return new ResponseEntity<>(forecastService.getForecasts(), HttpStatus.OK);

    }

    @GetMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Get(@PathVariable UUID id) {
        Optional<Forecast> forecast = forecastService.getId(id);
        if (forecast.isPresent()) return ResponseEntity.ok(forecast.get());
        return ResponseEntity.notFound().build();

    }

    @PutMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Update(@PathVariable UUID id, @RequestBody Forecast forecast) throws IOException {
            forecastService.updateFromApi(forecast);
            return ResponseEntity.ok(forecast);
    }

}
