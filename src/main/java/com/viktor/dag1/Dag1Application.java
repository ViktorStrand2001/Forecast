package com.viktor.dag1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viktor.dag1.models.BlogPost;
import com.viktor.dag1.models.Forecast;
import com.viktor.dag1.services.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.util.Scanner;
import java.util.UUID;


@SpringBootApplication
public class Dag1Application implements CommandLineRunner {

	Scanner scan = new Scanner(System.in);

	@Autowired
	private ForecastService forecastService;
	private Forecast forecast;

	public static void main(String[] args) {

		SpringApplication.run(Dag1Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		var objectMapper = new ObjectMapper();

		BlogPost blogPost = objectMapper.readValue(new URL("https://jsonplaceholder.typicode.com/posts/1"), BlogPost.class);
		BlogPost []blogPost1 = objectMapper.readValue(new URL("https://jsonplaceholder.typicode.com/posts"), BlogPost[].class);

		// JSON string
		var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setDate(20230530);
		forecast.setHour(12);
		forecast.setTemperature(12);


		String json = objectMapper.writeValueAsString(forecast);
		System.out.println(json);

		String json1 = objectMapper.writeValueAsString(blogPost);
		Forecast forecast2 = objectMapper.readValue(json, Forecast.class);
		System.out.println(json1);
		


		boolean run = true;

		while(run == true){
			System.out.println("--- PRESS ---");
			System.out.println("1. List all");
			System.out.println("2. Create");
			System.out.println("3. Update");
			System.out.println("9. Exit");

			int sel = scan.nextInt();

			switch (sel){
				case 1: listPredictions();
				break;

				case 2: addPredictions();
				break;

				case 3: updatePrediction();
				break;

				case 9: run = false;
				break;
				default:
					System.out.println("Press one of the instructed buttons!");
			}
		}
	}

	private void addPredictions(){
		/*TODO*/
		// input på dag, hour, temp
		// anropa servicen - save
		 Forecast forecast = new Forecast();

		System.out.println("---Set yyyyMMdd---");
		int date = scan.nextInt();

		System.out.println("---Set time of day:---");
		int hour = scan.nextInt();

		System.out.println("---Set temperature---");
		float temp = scan.nextFloat();


		forecast.setId(UUID.randomUUID());
		forecast.setDate(date);
		forecast.setHour(hour);
		forecast.setTemperature(temp);

		forecastService.add(forecast);

	}
	private void  listPredictions(){
		for (var prediction : forecastService.getForecasts()){
			System.out.printf("ID: %s Date: %d Hour: %d Temp: %f %n",
					prediction.getId(),
					prediction.getDate(),
					prediction.getHour(),
					prediction.getTemperature()
			);
		}
	}

	private void updatePrediction() {

		boolean run = true;
		int num = 1;

		for(var prediction : forecastService.getForecasts()){
			System.out.printf("%d) Date:%d  Kl:%d  Temp:%fC   %n"
					,num, prediction.getDate(),
					prediction.getHour(),
					prediction.getTemperature()
			);
			num++;
		}

		System.out.print("Select a row number to update:");
		int sel = scan.nextInt();
			var changeForecast = forecastService.getByIndex(sel -1);

		while(run == true){
			System.out.println("What do you want to update?" + " 1 = Date" + " 2 = Hour" + " 3 = Temperature" + " 9 = Exit to main menu");
			switch (scan.nextInt()){
				case 1 : {
					System.out.print("New Date:");
					changeForecast.setDate(scan.nextInt());
				}break;
				case 2 : {
					System.out.print("New time of day:");
					changeForecast.setHour(scan.nextInt());
				}break;
				case 3 : {
					System.out.print("New temperature:");
					changeForecast.setTemperature(scan.nextFloat());
					forecastService.update(forecast);
				}break;
				case 9 : {
					run = false;
				}
				break;
				default: {
					System.out.println(" Select between 1-3 or 9");
				}
			}
		}
	}

	private void deletePrediction(){

		

	}

}


