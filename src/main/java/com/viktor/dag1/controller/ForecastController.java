package com.viktor.dag1.controller;

import com.viktor.dag1.dto.ForecastListDTO;
import com.viktor.dag1.dto.NewForecastDTO;
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
import java.util.stream.Collectors;


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

/*       var ret = new ArrayList<ForecastListDTO>();
        for(var c : forecastService.getForecasts()){
            var forecastListDTO = new ForecastListDTO();
            forecastListDTO.Id = c.getId();
            forecastListDTO.Date = c.getDate();
            forecastListDTO.Temperature = c.getTemperature();
            forecastListDTO.Hour = c.getHour();
            ret.add(forecastListDTO);
        }
        return ret;
*/

        return new ResponseEntity<List<ForecastListDTO>>(forecastService.getForecasts().stream().map(forecast->{
            var forecastListDTO = new ForecastListDTO();
            forecastListDTO.Id = forecast.getId();
            forecastListDTO.Date = forecast.getDate();
            forecastListDTO.Temperature = forecast.getTemperature();
            forecastListDTO.Hour = forecast.getHour();
            return forecastListDTO;
        }).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Get(@PathVariable UUID id) {
        Optional<Forecast> forecast = forecastService.getId(id);
        if (forecast.isPresent()) return ResponseEntity.ok(forecast.get());
        return ResponseEntity.notFound().build();

    }

    @PutMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Update(@PathVariable UUID id, @RequestBody NewForecastDTO newForecastDTO) throws IOException {

       // mappar från DTO till entitet
        var forecast = new Forecast();
        forecast.setId(id);
        forecast.setDate(newForecastDTO.date);
        forecast.setHour(newForecastDTO.hour);
        forecast.setTemperature(newForecastDTO.temperature);
        forecastService.updateFromApi(forecast);
        return ResponseEntity.ok(forecast);
    }

    @PostMapping("/api/forecasts")
    public ResponseEntity<Forecast> New(@RequestBody Forecast forecast) throws IOException {
    var newCreated = forecastService.add(forecast);
        return ResponseEntity.ok(newCreated); // mer REST ful = created (204) samt url till produkten
    }

    /*
    @DeleteMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Delete(@PathVariable UUID id) {
        Optional<Forecast> forecast = forecastService.getId(id);
        if (forecast.isPresent()) return ResponseEntity.ok(forecast.get());
        return ResponseEntity.notFound().build();
    }
     */
}
