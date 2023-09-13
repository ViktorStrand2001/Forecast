package com.viktor.dag1.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.viktor.dag1.dto.AverageDTO;
import com.viktor.dag1.models.Forecast;
import com.viktor.dag1.repositories.IForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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

/*
    public Forecast getByIndex(int i) {
        return forecasts.get(i);
    }
*/

    public void updateFromApi (Forecast forecastFromUser){

        var forecastInList = getId(forecastFromUser.getId()).get();
        forecastInList.setPredictionDatum(forecastFromUser.getPredictionDatum());
        forecastInList.setPredictionHour(forecastFromUser.getPredictionHour());
        forecastInList.setPredictionTemperature(forecastFromUser.getPredictionTemperature());



        //    writeAllToFile(forecasts);
    }


    public Forecast update (Forecast forecast){
        iForecastRepository.save(forecast);
        return forecast;
        //    writeAllToFile(forecasts);
    }

    public Optional<Forecast> getId(UUID id){
        // Kollar alla forecasts 1 efter 1.
        // Kollar så att getId är lika med id och tar första som den hittar.
        // Där det står forecast -> forecast kan heta vad som t.ex c -> c
        return iForecastRepository.findById(id);
        // return getForecasts().stream().filter(c -> c.getId().equals(id)).findFirst();
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


/*

// file hantering
    private List<Forecast> readFromFile() throws IOException {
        if(!Files.exists(Path.of("predictions.json"))) return new ArrayList<Forecast>();
        ObjectMapper objectMapper = getObjectMapper();
        var jsonStr = Files.readString(Path.of("predictions.json"));
        return  new ArrayList(Arrays.asList(objectMapper.readValue(jsonStr, Forecast[].class ) ));
    }


    private void writeAllToFile(List<Forecast> weatherPredictions) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, weatherPredictions);

        Files.writeString(Path.of("predictions.json"), stringWriter.toString());

    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
*/

}
