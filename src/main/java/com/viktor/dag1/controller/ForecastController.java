package com.viktor.dag1.controller;

import com.viktor.dag1.dto.AverageDTO;
import com.viktor.dag1.dto.ForecastListDTO;
import com.viktor.dag1.dto.NewForecastDTO;
import com.viktor.dag1.models.Forecast;
import com.viktor.dag1.services.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NO_CONTENT;


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
    public ResponseEntity<List<ForecastListDTO>> getAll(){

        return new ResponseEntity<List<ForecastListDTO>>(forecastService.getForecasts().stream().map(forecast->{
            var forecastListDTO = new ForecastListDTO();
            forecastListDTO.Id = forecast.getId();
            forecastListDTO.Date = forecast.getPredictionDatum();
            forecastListDTO.Temperature = forecast.getPredictionTemperature();
            forecastListDTO.Hour = forecast.getPredictionHour();
            return forecastListDTO;
        }).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Get(@PathVariable UUID id) {
        Optional<Forecast> forecast = forecastService.getId(id);
        if (forecast.isPresent()) return ResponseEntity.ok(forecast.get());
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/api/average/{date}")
    public ResponseEntity<List<AverageDTO>> getAvg(@PathVariable String date){

        try {
            LocalDate realDate = LocalDate.parse(date);
            List<AverageDTO> result = forecastService.calculateAverage(realDate);

            if (result.isEmpty()) {
                return new ResponseEntity<>(result, NO_CONTENT);
            }

            return new ResponseEntity<>(result, HttpStatus.OK);

        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Update(@PathVariable UUID id, @RequestBody NewForecastDTO newForecastDTO){

       // mappar från DTO till entitet
        var forecast = new Forecast(UUID.randomUUID());
        forecast.setId(id);
        forecast.setPredictionDatum(newForecastDTO.getDate());
        forecast.setPredictionHour(newForecastDTO.getHour());
        forecast.setPredictionTemperature(newForecastDTO.getTemperature());
        forecastService.updateFromApi(forecast);
        return ResponseEntity.ok(forecast);
    }

    @PostMapping("/api/forecasts")
    public ResponseEntity<Forecast> New(@RequestBody Forecast forecast) throws IOException {
    var newCreated = forecastService.add(forecast);
        return ResponseEntity.ok(newCreated); // mer REST ful = created (204) samt url till produkten
    }
}
