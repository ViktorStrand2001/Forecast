package com.viktor.dag1.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.viktor.dag1.models.Forecast;
import com.viktor.dag1.models.Predictions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class ForecastService{

    //Constructor
    public ForecastService(){
        try {
            forecasts = readFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Forecast> forecasts = new ArrayList<>();

    public List<Forecast> getForecasts(){
        return forecasts;
    }

    public void add(Forecast forecast) throws IOException {
        forecasts.add(forecast);
        writeAllToFile(forecasts);
    }

    public Forecast getByIndex(int i) {
        return forecasts.get(i);
    }

    public void updateFromApi (Forecast forecastFromUser) throws IOException {

        var forecastInList = getId(forecastFromUser.getId()).get();
        forecastInList.setDate(forecastFromUser.getDate());
        forecastInList.setHour(forecastFromUser.getHour());
        forecastInList.setTemperature(forecastFromUser.getTemperature());
        writeAllToFile(forecasts);
    }


    public void update (Forecast forecast) throws IOException {

        writeAllToFile(forecasts);
    }

    public Optional<Forecast> getId(UUID id){
       // Kollar alla forecasts 1 efter 1.
        // Kollar så att getId är lika med id och tar första som den hittar.
        // Där det står forecast -> forecast kan heta vad som t.ex c -> c
        return getForecasts().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public void deleted(Forecast forecast){
        forecasts.remove(forecast);
    }



// file hantering
    private List<Forecast> readFromFile() throws IOException, IOException {
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

}
