package com.viktor.dag1.services;

import com.viktor.dag1.dto.AverageDTO;
import com.viktor.dag1.models.Forecast;
import com.viktor.dag1.repositories.IForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ForecastService{

    @Autowired
    IForecastRepository iForecastRepository;

    //Constructor
/*
    public ForecastService(){
        try {
            forecasts = readFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
*/
    public ForecastService(){

    }
    public Forecast add(Forecast forecast) {
        iForecastRepository.save(forecast);
        return forecast;
    }

    public Forecast getByIndex(int i) {
        return getForecasts().get(i);
    }

    // private static List<Forecast> forecasts = new ArrayList<>();

    public List<Forecast> getForecasts(){
    //   return forecasts;
        return iForecastRepository.findAll();


    }
/*
    public Forecast add(Forecast forecast) throws IOException {
        forecast.setId(UUID.randomUUID());
        forecasts.add(forecast);
        writeAllToFile(forecasts);
        return forecast;
    }
*/

    /*public void updateFromApi (Forecast forecastFromUser){

        var forecastInList = getId(forecastFromUser.getId()).get();
        forecastInList.setPredictionDatum(forecastFromUser.getPredictionDatum());
        forecastInList.setPredictionHour(forecastFromUser.getPredictionHour());
        forecastInList.setPredictionTemperature(forecastFromUser.getPredictionTemperature());



        //    writeAllToFile(forecasts);
    }*/


    public void update (Forecast forecast){
        iForecastRepository.save(forecast);
    }

    public Optional<Forecast> getId(UUID id){
        return iForecastRepository.findById(id);
    }

    public Forecast deleted(Forecast forecast){
        iForecastRepository.deleteById(forecast.getId());
        return forecast;
    }

    public List<AverageDTO> calculateAverage(LocalDate dag) {
        var resultList = new ArrayList<AverageDTO>();

        var allPredictionsForDay = iForecastRepository.findAllByPredictionDatum(dag);

        for (int timme = 0; timme < 24; timme++) {

            var averageDto = new AverageDTO();
            averageDto.setHour(timme);
            averageDto.setDate(dag);
            float antal = 0;
            float summa = 0;

            for(Forecast forecast : allPredictionsForDay){
                if (forecast.getPredictionHour() == timme){
                    antal++;
                    summa += forecast.getPredictionTemperature();
                }
            }
            if (antal > 0){
                averageDto.setAverage(summa / antal);
                resultList.add(averageDto);
            }
        }
        return resultList;
    }
}
