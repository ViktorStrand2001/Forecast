package com.viktor.dag1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viktor.dag1.dto.AverageDTO;
import com.viktor.dag1.models.*;
import com.viktor.dag1.repositories.IForecastRepository;
import com.viktor.dag1.services.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@SpringBootApplication
public class Dag1Application implements CommandLineRunner {

	Scanner scan = new Scanner(System.in);

	@Autowired
	private ForecastService forecastService;
	@Autowired
	private IForecastRepository iForecastRepository;


	public static void main(String[] args) {
		SpringApplication.run(Dag1Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		deleteAllDummys();
		dummyAdd();
		smhi();
		System.out.println("---Removing all date and fetching new data for today---");

		boolean runMenu = true;

		while (runMenu) {
			System.out.println("1. List all");
			System.out.println("2. Create");
			System.out.println("3. Update");
			System.out.println("4. Delete");
			System.out.println("5. SMHI Forecasts");
			System.out.println("6. dummyAdd");
			System.out.println("7. deleteAllDummys");
			System.out.println("8. calculateAverage");
			System.out.println("9. Exit");

			String sel = scan.next();

			switch (sel) {
				case "1" -> {
					if (forecastService.getForecasts().size() <= 0){
						System.out.println("Not data in database");
					}else {
						listPredictions();
					}
				}
				case "2" -> addPredictions();
				case "3" -> {
					if (forecastService.getForecasts().size() <= 0){
						System.out.println("Not data to database");
					}else {
						updatePrediction();
					}
				}
				case "4" -> {
					if (forecastService.getForecasts().size() <= 0){
						System.out.println("Not data in database");
					}else {
						deletePrediction();
					}
				}
				case "5" -> smhi();
				case "6" -> dummyAdd();
				case "7" -> deleteAllDummys();
				case "8" ->  {
					if (forecastService.getForecasts().size() <= 0){
						System.out.println("Not data in database");
					}else {
						calculateAverage();
					}
				}
				case "9" -> runMenu = false;
				default -> System.out.println("Press one of the instructed buttons!");
			}
		}
	}

	private void dummyAdd() {
		for (int i = 0; i < 24; i++) {
			var forecast1 = new Forecast();
			forecast1.setId(UUID.randomUUID());
			forecast1.setPredictionTemperature(i);
			forecast1.setPredictionHour(i);
			LocalDate date = LocalDate.now();
			LocalDate data = LocalDate.parse(date.toString());
			forecast1.setPredictionDatum(data);
			forecast1.setCreated(data);
			forecast1.setDataSource(DataSource.ADMIN);
			forecastService.add(forecast1);
		}
	}

	private void deleteAllDummys() {
		for (var forecast : forecastService.getForecasts()) {
			forecastService.deleted(forecast);
		}
	}

	private void calculateAverage() {
		var dag = LocalDate.now();
		List<AverageDTO> dtos = forecastService.calculateAverage(dag);

		 // for (int i = 0; i < dtos.size(); i++)
			for (AverageDTO dto : dtos) {
			System.out.printf("%s Average: %s KL: %s %n", dto.getDate(), dto.getAverage(), dto.getHour());
		}
	}

	// Show all SMHIs forecast in 24 hours
	private void smhi() throws IOException {

		String paramName;
		List paramValue = null;
		int paramLvl;
		String paramLvlType;
		String paramUnit;

		var objectMapper = new ObjectMapper();

		Predictions predictions = objectMapper.readValue(new URL("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/16.158/lat/58.5812/data.json"), Predictions.class);

		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.HOUR_OF_DAY, 24);
		Date tomorrow = calendar.getTime(); // 13:00 till 13:00 nästa dag

		int nummer = 0;
		for (TimeSeries timeSeries : predictions.getTimeSeries()) {
			Date validTime = timeSeries.getValidTime();
			System.out.printf("%d) Valid Time: %s%n", nummer++, validTime);
		}

		System.out.println("***********************************************");
		System.out.printf(" ApprovedTime: %s %n ReferenceTime: %s %n Geometry: %s, %s %n", predictions.getApprovedTime(), predictions.getReferenceTime(), predictions.getGeometry().getType(), predictions.getGeometry().getCoordinates());
		System.out.println("***********************************************");

		for (TimeSeries timeSeries : predictions.getTimeSeries()) {

			Date validTime = timeSeries.getValidTime();


			if (validTime != null) {

				// omvandla till local tid i variabel som ska sparas till databasen
				LocalDate validLocalDate = validTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalTime validLocalTime = validTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

				int hourOfDay = validLocalTime.getHour();

				calendar.setTime(validTime);

				Forecast forecast = new Forecast();

				if (validTime.after(today) && validTime.before(tomorrow)) {
					for (Parameter parameter : timeSeries.getParameters()) {
						List<Float> values = parameter.getValues();

						for (Float parameterValue : values)
							if (parameter.getName().equals("t")) {
								paramName = parameter.getName();
								paramValue = parameter.getValues();
								paramLvl = parameter.getLevel();
								paramLvlType = parameter.getLevelType();
								paramUnit = parameter.getUnit();

								System.out.println("------------------------------------------------\n");
								System.out.println("\t\tTemperature info:");
								System.out.printf("\n Name: %s %n Level type: %s %n Level: %d %n Unit: %s %n Value: %s\n",
										paramName,
										paramLvlType,
										paramLvl,
										paramUnit,
										paramValue);

								forecast.setPredictionHour(hourOfDay);
								forecast.setPredictionDatum(validLocalDate);
								forecast.setPredictionTemperature(parameterValue);
								forecast.setDataSource(DataSource.SMHI);

							} else if (parameter.getName().equals("pcat")) {
								System.out.println("\t\tNederbörd (precipitation) info:");
								System.out.printf("\n Name: %s %n Level type: %s %n Level: %d %n Unit: %s %n Value: %s\n____________________\n",
										parameter.getName(),
										parameter.getLevelType(),
										parameter.getLevel(),
										parameter.getUnit(),
										parameter.getValues());


								if (parameterValue == 0.0) {
									System.out.println("pcat value: " + parameterValue);
									System.out.println("No precipitation");
									forecast.setRainOrSnow(false);

								} else if (parameterValue == 1.0) {
									System.out.println("pcat value: " + parameterValue);
									System.out.println("Snow");
									forecast.setRainOrSnow(true);

								} else if (parameterValue == 2.0) {
									System.out.println("pcat value: " + parameterValue);
									System.out.println("Snow and rain");
									forecast.setRainOrSnow(true);

								} else if (parameterValue == 3.0) {
									System.out.println("pcat value: " + parameterValue);
									System.out.println("Rain");
									forecast.setRainOrSnow(true);

								}
							System.out.println("\n------------------------------------------------\n");
						}
					}
					iForecastRepository.save(forecast);
				}
			}
		}
	}

	// LIST ALL
	private void listPredictions() {
		int num = 1;

		Collections.sort(forecastService.getForecasts(), new Comparator<Forecast>() {
			@Override
			public int compare(Forecast forecast1, Forecast forecast2) {
				return forecast1.getPredictionDatum().compareTo(forecast2.getPredictionDatum());
			}
		});

		for (var forecast : forecastService.getForecasts()) {
			System.out.printf("\t%d) %n\tId: %s %n\tDag: %s Tid: %d:00 %n\tTemp: %.1f \t%n It will rain or snow: %b %n %n",
					num++,
					forecast.getId(),
					forecast.getPredictionDatum(),
					forecast.getPredictionHour(),
					forecast.getPredictionTemperature(),
					forecast.isRainOrSnow()
			);
		}
	}

	// LIST Create
	private void addPredictions() {

		Forecast forecast = new Forecast();

		int year = 0;
		int mouth = 0;
		int day = 0;

		boolean validUpdateInput = false;

		System.out.println("yyyy");
		while (!validUpdateInput) {
			if (scan.hasNextInt()) {
				year = scan.nextInt();
				if ((year <= 9999 && year > 999)){
					validUpdateInput = true;
				}
			} else {
				System.out.println("yyyy");
				scan.next();
			}
		}

		validUpdateInput = false;

		System.out.println("MM");
		while (!validUpdateInput) {
			if (scan.hasNextInt()) {
				mouth = scan.nextInt();
				if ((mouth <= 12 && mouth > 0)){
					validUpdateInput = true;
				}
			} else {
				System.out.println("MM");
				scan.next();
			}
		}

		validUpdateInput = false;

		System.out.println("dd");
		while (!validUpdateInput) {
			if (scan.hasNextInt()) {
				day = scan.nextInt();
				if (day <= 31 && day > 0){
					validUpdateInput = true;
				}
			} else {
				System.out.println("dd");
				scan.next();
			}
		}

		LocalDate date = LocalDate.of(year, mouth, day);

		validUpdateInput = false;
		int hour = 0;

		System.out.print("Set time of day:");
		while (!validUpdateInput) {
			if (scan.hasNextInt()) {
				hour = scan.nextInt();
				if (hour <= 23 && hour >= 0){
					validUpdateInput = true;
				}
			} else {
				System.out.print("Set time of day:");
				scan.next();
			}
		}

		validUpdateInput = false;
		float temp = 0;

		System.out.print("Set temperature:");
		while(!validUpdateInput){
			if (scan.hasNextInt()){
				temp = scan.nextInt();
				validUpdateInput = true;
			}else{
				System.out.print("Set temperature:");
				scan.next();
			}
		}

		validUpdateInput = false;
		System.out.print("Set Rain or Snow: 1 = false | 2 = true");
		while (!validUpdateInput){
			switch (scan.next()){
				case "1" -> {
					forecast.setRainOrSnow(false);
					validUpdateInput = true;
				}
				case "2" -> {
					forecast.setRainOrSnow(true);
					validUpdateInput = true;
				}
				default -> System.out.print("Set Rain or Snow: 1 = false | 2 = true");
			}
		}

		forecast.setCreated(date);
		forecast.setPredictionDatum(date);
		forecast.setPredictionHour(hour);
		forecast.setPredictionTemperature(temp);
		forecast.setDataSource(DataSource.ADMIN);
		forecast.setId(UUID.randomUUID());
		forecastService.add(forecast);
	}

	// LIST UPDATE
	private void updatePrediction() {

		boolean runUpdate = true;



		listPredictions();

		System.out.printf("Select a row number to update:%n");

		int sel = 0;
		boolean validInput = false;

		while(!validInput){
			if (scan.hasNextInt()){

				sel = scan.nextInt();
					if (sel <= 0 || sel > forecastService.getForecasts().size()){
						System.out.printf("That prediction those not exist! Try a different number.%n");
						listPredictions();
					}else{
						validInput = true;
					}
			}else{
				System.out.println("Not an valid input");
				scan.next();
			}
		}

			var changeForecast = forecastService.getByIndex(sel -1);

		while(runUpdate){
			System.out.printf("What do you want to update?%n 1: Date%n 2: time of day%n 3: Temperature%n 4: save%n 9: Exit");
			switch (scan.next()) {
				case "1" -> {

					int year = 0;
					int mouth = 0;
					int day = 0;

					boolean validUpdateInput = false;

					System.out.println("yyyy");
					while(!validUpdateInput){
						if (scan.hasNextInt()){
							year = scan.nextInt();
							if ((year <= 9999 && year > 999)){
								validUpdateInput = true;
							}
						}else{
							System.out.println("yyyy");
							scan.next();
						}
					}

					validUpdateInput = false;

					System.out.println("MM");
					while (!validUpdateInput) {
						if (scan.hasNextInt()) {
							mouth = scan.nextInt();
							if ((mouth <= 12 && mouth > 0)){
								validUpdateInput = true;
							}
						} else {
							System.out.println("MM");
							scan.next();
						}
					}

					validUpdateInput = false;

					System.out.println("dd");
					while (!validUpdateInput) {
						if (scan.hasNextInt()) {
							day = scan.nextInt();
							if (day <= 31 && day > 0){
								validUpdateInput = true;
							}
						} else {
							System.out.println("dd");
							scan.next();
						}
					}

					LocalDate date = LocalDate.of(year,mouth,day);
					LocalDate data = LocalDate.parse(date.toString());

					changeForecast.setPredictionDatum(data);
				}
				case "2" -> {
					boolean validUpdateInput = false;
					int hour = 0;

					System.out.print("Set time of day:");
					while (!validUpdateInput) {
						if (scan.hasNextInt()) {
							hour = scan.nextInt();
							if (hour <= 23 && hour >= 0){
								validUpdateInput = true;
							}
						} else {
							System.out.print("Set time of day:");
							scan.next();
						}
					}
					changeForecast.setPredictionHour(hour);
				}
				case "3" -> {
					boolean validUpdateInput = false;
					float temp = 0;

					System.out.print("New temperature:");
					while(!validUpdateInput){
						if (scan.hasNextInt()){
							temp = scan.nextInt();
							validUpdateInput = true;
						}else{
							System.out.print("New temperature:");
							scan.next();
						}
					}
					changeForecast.setPredictionTemperature(temp);
				}
				case "4" -> {
					changeForecast.setUpdated(LocalDate.now());
					forecastService.update(changeForecast);
				}
				case "9" -> runUpdate = false;

				default -> System.out.println(" Select between 1-3 or 9");
			}
		}
	}

	// LIST DELETE
	private void deletePrediction(){

		listPredictions();

		System.out.println("Wish one do you want to delete?");

		int sel = 0;

		boolean validInput = false;
		while(!validInput){
			if (scan.hasNextInt()){
				sel = scan.nextInt();
				if (sel <= 0 || sel > forecastService.getForecasts().size()){
					System.out.printf("That prediction those not exist! Try a different number.%n");
					listPredictions();
				}else{
					validInput = true;
				}
			}else{
				System.out.println("Not an valid input");
				scan.next();
			}
		}

		var selectedForecast = forecastService.getByIndex(sel -1);

			System.out.printf("Are you Sure you want to delete this forecast?%n 1: Yes%n 2: NO");

			boolean validDelete = true;

			while (validDelete){
				switch (scan.next()) {
					case "1" -> {
						System.out.printf("Forecast has been deleted%n");
						forecastService.deleted(selectedForecast);
						validDelete = false;
					}
					case "2" -> {
						System.out.printf("Forecast was not deleted%n");
						validDelete = false;
					}default -> {
						System.out.println("Invalid choice. Please press 1 for YES or 2 For NO");
				}
			}
		}
	}
}


