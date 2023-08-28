package com.viktor.dag1;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viktor.dag1.models.Forecast;
import com.viktor.dag1.models.Parameter;
import com.viktor.dag1.models.Predictions;
import com.viktor.dag1.models.TimeSeries;
import com.viktor.dag1.services.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@SpringBootApplication
public class Dag1Application implements CommandLineRunner {

	Scanner scan = new Scanner(System.in);

	@Autowired
	private ForecastService forecastService;
	private Forecast forecast;
	private TimeSeries timeSeries;

	String yyyy = "";
	String mm = "";
	String dd = "";

	public static void main(String[] args) {

		SpringApplication.run(Dag1Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		boolean runMenu = true;

		while(runMenu == true){
			System.out.println("--- PRESS ---");
			System.out.println("1. List all");
			System.out.println("2. Create");
			System.out.println("3. Update");
			System.out.println("4. Delete");
			System.out.println("5. SMHI Forecasts");
			System.out.println("9. Exit");

			int sel = scan.nextInt();

			switch (sel) {
				case 1 -> listPredictions();
				case 2 -> addPredictions();
				case 3 -> updatePrediction();
				case 4 -> deletePrediction();
				case 5 -> smhi();
				case 9 -> runMenu = false;
				default -> System.out.println("Press one of the instructed buttons!");
			}
		}
	}

	private void smhi() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		Predictions predictions = objectMapper.readValue(new URL("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/14.436469/lat/61.13366/data.json"), Predictions.class);

		int num = 1;
		int selectedNumber;

		for (TimeSeries timeSeries : predictions.getTimeSeries()) {
			String validTime = timeSeries.getValidTime();
			System.out.printf("%d) Valid Time: %s%n", num++, validTime);
		}

		do {
			System.out.println("Enter the number of the Valid Time:");
			
			selectedNumber = scan.nextInt();

			if (selectedNumber >= 1 && selectedNumber <= 24) {
				String targetValidTime = predictions.getTimeSeries().get(selectedNumber - 1).getValidTime();

				System.out.println("Selected Valid Time: " + targetValidTime);

				for (TimeSeries timeSeries : predictions.getTimeSeries()) {
					String validTime = timeSeries.getValidTime();

					if (validTime.equals(targetValidTime)) {

						for (Parameter parameter : timeSeries.getParameters()) {
							String paramName = parameter.getName();
							List paramValue = parameter.getValues();
							int paramLvl = parameter.getLevel();
							String paramLvlTyp = parameter.getLevelType();
							String paramUnit = parameter.getUnit();

							System.out.printf("%nLevel: %d %nParameter: %s  %nValue: %s%n", paramLvl, paramName, paramValue);

						}
					}
				}
			}
		} while (selectedNumber != predictions.getTimeSeries().size());
	}

	private void  listPredictions(){
		for (var prediction : forecastService.getForecasts()){
			System.out.printf("ID: %s Date: %s Hour: %d Temp: %f %n",
					prediction.getId(),
					prediction.getDate(),
					prediction.getHour(),
					prediction.getTemperature()
			);
		}
		System.out.println("Here's all the current forecasts ^");
	}

	private void addPredictions() throws IOException {

		Forecast forecast = new Forecast();

		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());

		System.out.printf("Is this the date you want to use: %s %n If YES press 1, if NO press 2", date);

		if (scan.nextInt() == 2){

			System.out.println("What date do you want to put in?");
			System.out.println("yyyy");
			yyyy = scan.next();
			System.out.println("mm");
			mm = scan.next();
			System.out.println("dd");
			dd = scan.next();

			String time = yyyy + "-" + mm + "-" + dd;

			forecast.setDate(time);
		}else{
			forecast.setDate(date);
		}

		System.out.println("---Set time of day:---");
		int hour = scan.nextInt();
		forecast.setHour(hour);

		System.out.println("---Set temperature---");
		float temp = scan.nextFloat();
		forecast.setTemperature(temp);

		forecast.setId(UUID.randomUUID());
		forecastService.add(forecast);
	}

	private void updatePrediction() throws IOException {

		boolean runUpdate = true;

		System.out.printf("Select a row number to update:%n");

		forIForecasts();

		int sel;
		do {
			sel = scan.nextInt();
			if (sel <= 0 || sel > forecastService.getForecasts().size()){
				System.out.printf("That prediction those not exist! Try a different number.%n");
				forIForecasts();
			}
		}while (sel <= 0 || sel > forecastService.getForecasts().size());

			var changeForecast = forecastService.getByIndex(sel -1);

		while(runUpdate == true){
			System.out.printf("What do you want to update?%n 1: Date%n 2: time of day%n 3: Temperature%n 9: Exit and save");
			switch (scan.nextInt()) {
				case 1 -> {
					System.out.println("---yyyy---");
					yyyy = scan.next();
					System.out.println("---MM---");
					mm = scan.next();
					System.out.println("---dd---");
					dd = scan.next();
					String time = yyyy + "-" + mm + "-" + dd;
					changeForecast.setDate(time);
				}
				case 2 -> {
					System.out.print("New time of day:");
					changeForecast.setHour(scan.nextInt());
				}
				case 3 -> {
					System.out.print("New temperature:");
					changeForecast.setTemperature(scan.nextFloat());

				}
				case 9 -> {
					forecastService.update(forecast);
					runUpdate = false;
				}
				default -> System.out.println(" Select between 1-3 or 9");
			}
		}
	}

	private void deletePrediction() throws IOException {

		forIForecasts();

		int sel = scan.nextInt();
		var selectedForecast = forecastService.getByIndex(sel -1);

			System.out.printf("Are you Sure you want to delete this forecast?%n 1: Yes%n 2: NO");
		switch (scan.nextInt()) {
			case 1 -> {
				System.out.printf("Forecast has been deleted%n");
                forecastService.deleted(selectedForecast);
				forecastService.update(selectedForecast);
			}
			case 2 -> {
				System.out.printf("Forecast was not deleted%n");
			}
			default -> {
				System.out.println("Invalid choice. Please press 1 for YES or 2 For NO");
			}
		}

	}

	private void forIForecasts(){
		int num = 1;
		for(var prediction : forecastService.getForecasts()){
			System.out.printf("%d) Date:%s  Kl:%d  Temp:%fC   %n"
					,num, prediction.getDate(),
					prediction.getHour(),
					prediction.getTemperature()
			);
			num++;
		}
	}



}


