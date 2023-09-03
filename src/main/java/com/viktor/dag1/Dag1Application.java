package com.viktor.dag1;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/*TODO- G
*  Matcha datum och tid istället för index i average.
*  */

@SpringBootApplication
public class Dag1Application implements CommandLineRunner {

	Scanner scan = new Scanner(System.in);

	@Autowired
	private ForecastService forecastService;
	private Forecast forecast;


	ObjectMapper objectMapper = new ObjectMapper();
	Predictions predictions;

	{
		try {
			predictions = objectMapper.readValue(new URL("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/14.436469/lat/61.13366/data.json"), Predictions.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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
			System.out.println("6. Average");
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



	// Show all SMHIs forecast in 24 hours
	private void smhi(){



/*
		int totalEntries = predictions.getTimeSeries().size();

		for (int num = 1; num <= 24; num++) {
			if (num <= totalEntries) {
				TimeSeries timeSeries = predictions.getTimeSeries().get(num - 1);
				String validTime = timeSeries.getValidTime();
				System.out.printf("%d) Valid Time: %s%n", num, validTime);
			} else {
				System.out.printf("%d) No data available%n", num);
			}
		}

		System.out.println("***********************************************");
		System.out.printf(" ApprovedTime: %s %n ReferenceTime: %s %n Geometry: %s, %s %n", predictions.getApprovedTime(), predictions.getReferenceTime(),predictions.getGeometry().getType(),predictions.getGeometry().getCoordinates());
		System.out.println("***********************************************");

		// Välja data baserat å date+time
		String selectedDate, selectedTime = "00";
		selectedDate = scan.next();
		do {

			String paramName;
			List paramValue = null;
			int paramLvl;
			String paramLvlType;
			String paramUnit;

			System.out.println("Enter the Valid Date (yyyy-MM-dd):");

			System.out.println("Enter the Valid Time (HH):");
			selectedTime = "00";

			String selectedDateTime = selectedDate + "T" + selectedTime + ":00:00Z";

			for (TimeSeries timeSeries : predictions.getTimeSeries()) {
				String validTime = timeSeries.getValidTime();

				try {
					// Omvandla strängen till ett heltal
					int num = Integer.parseInt(selectedTime);

					// Öka värdet med 1
					num = num + 1;

					// Konvertera det ökade värdet tillbaka till en sträng
					selectedTime = Integer.toString(num);

					// Skriv ut den ökade strängen
					System.out.println("Den ökade strängen: " + selectedTime);
				} catch (NumberFormatException e) {
					System.err.println("Strängen representerar inte ett giltigt heltal.");
				}

				if (validTime.equals(selectedDateTime)) {
					// Extract and process other data from timeSeries as needed
					for (Parameter parameter : timeSeries.getParameters()) {
						if (parameter.getName().equals("t")){
							paramName = parameter.getName();
							paramValue = parameter.getValues();
							paramLvl = parameter.getLevel();
							paramLvlType = parameter.getLevelType();
							paramUnit = parameter.getUnit();
							// Extract more parameter details as needed
							System.out.printf("\n Name: %s %n Level type: %s %n Level: %d %n Unit: %s %n Value: %s\n", paramName, paramLvlType, paramLvl, paramUnit, paramValue);
						}
					}

					// listPredictions();

					System.out.println("Enter Valid index to compare temp with: ");

					// Värdet från Admin CLI

					var selectedForecast = forecastService.getByIndex(0);
					float admin = selectedForecast.getTemperature();
					// Värdet från Smhi
					Object valueFromList = paramValue.get(0);
					float smhi = valueFromList.hashCode();
					// AVG-värdet från dessa två
					float sum = average(admin,smhi);

					System.out.println("------------------------------------------------------\n\t\tThe average temp of "
							+ selectedDate + " " + selectedTime + " is \n\t\t" + sum +
							" C\n------------------------------------------------------"); // paramValue.get(0)
				}
			}

		} while (selectedTime.equals("23")); // You can use any condition to exit the loop
*/



		// Ditt befintliga kodblock innan loopen
		// Exempel:
		String paramName;
		List paramValue = null;
		int paramLvl;
		String paramLvlType;
		String paramUnit;
		String selectedTime, selectedDate;
		int index = 0;

		int totalEntries = predictions.getTimeSeries().size();

		for (int num = 1; num <= 24; num++) {
			if (num <= totalEntries) {
				TimeSeries timeSeries = predictions.getTimeSeries().get(num - 1);
				String validTime = timeSeries.getValidTime();
				System.out.printf("%d) Valid Time: %s%n", num, validTime);
			} else {
				System.out.printf("%d) No data available%n", num);
			}
		}

		System.out.println("Enter the Valid Date (yyyy-MM-dd):");
		selectedDate = scan.next();

		for (int hour = 0; hour < 24; hour++) {
			selectedTime = String.format("%02d", hour);

			String selectedDateTime = selectedDate + "T" + selectedTime + ":00:00Z";

			for (TimeSeries timeSeries : predictions.getTimeSeries()) {
				String validTime = timeSeries.getValidTime();

				if (validTime.equals(selectedDateTime)) {
					// Ditt befintliga kodblock för att hämta temperaturvärden och annan data
					// Exempel:
					for (Parameter parameter : timeSeries.getParameters()) {
						if (parameter.getName().equals("t")) {
							paramName = parameter.getName();
							paramValue = parameter.getValues();
							paramLvl = parameter.getLevel();
							paramLvlType = parameter.getLevelType();
							paramUnit = parameter.getUnit();
							System.out.println(parameter.getValues());
							// Extract more parameter details as needed
						}
					}

					// Beräkna genomsnittstemperaturen per timme
					if (paramValue != null && !paramValue.isEmpty()) {

						listPredictions();
						var selectedForecast = forecastService.getByIndex(index ++);



						float smhi = Float.parseFloat(paramValue.get(0).toString());
						float sum = selectedForecast.getTemperature() + smhi;

						float average = sum / 2;

						System.out.println("------------------------------------------------------");
						System.out.println("The average temp for " + selectedDate + " " + selectedTime + " is:");
						System.out.println(average + " C");
						System.out.println("------------------------------------------------------");

					}
				}
			}
		}

		// Ditt befintliga kodblock efter loopen
		// Exempel:
		System.out.println("Other code after the loop...");




/*
		int selectedNumber;
		int totalEntries = predictions.getTimeSeries().size();

		for (int num = 1; num <= 24; num++) {
			if (num <= totalEntries) {
				TimeSeries timeSeries = predictions.getTimeSeries().get(num - 1);
				String validTime = timeSeries.getValidTime();
				System.out.printf("%d) Valid Time: %s%n", num, validTime);
			} else {
				System.out.printf("%d) No data available%n", num);
			}
		}

		do {

			String paramName;
			List paramValue = null;
			int paramLvl;
			String paramLvlTyp;
			String paramUnit;

			System.out.println("Enter the number of the Valid Time:");
			selectedNumber = scan.nextInt();

			System.out.println();


			if (selectedNumber >= 1 && selectedNumber <= 24) {
				String targetValidTime = predictions.getTimeSeries().get(selectedNumber - 1).getValidTime();
				System.out.println("Selected Valid Time: " + targetValidTime);

				for (TimeSeries timeSeries : predictions.getTimeSeries()) {
					String validTime = timeSeries.getValidTime();

					if (validTime.equals(targetValidTime)) {
						for (Parameter parameter : timeSeries.getParameters()) {
							if (parameter.getName().equals("t")){

								paramName = parameter.getName();
								paramValue = parameter.getValues();
								paramLvl = parameter.getLevel();

								System.out.printf("%nLevel: %d %nParameter: %s  %nValue: %s%n", paramLvl, paramName, paramValue);
							}
						}

						// Går räknar ut average temp mellan bestämda dagar och timme
						listPredictions();
						int sel = scan.nextInt();
						var selectedForecast = forecastService.getByIndex(sel -1);

						Object valueFromList = paramValue.get(0);

						float sum = selectedForecast.getTemperature() + valueFromList.hashCode();

						float average = sum / 2;

						System.out.println("The average " + average + "C");
					}
				}
			}
		} while (selectedNumber < 1 || selectedNumber >= 24);
*/



	}

	private float average(float a, float b) {
		return (a+b)/2;
	}

	// LIST ALL
	private void  listPredictions(){
		int num = 1;

		Collections.sort(forecastService.getForecasts(), new Comparator<Forecast>() {
			@Override
			public int compare(Forecast forecast1, Forecast forecast2) {
				return forecast1.getDate().compareTo(forecast2.getDate());
			}
		});

		for(var forecast : forecastService.getForecasts()) {
			System.out.printf("\t%d) %n\tId: %s %n\tDag: %s Tid: %d:00 %n\tTemp: %.1f %n %n",
					num++,
					forecast.getId(),
					forecast.getDate(),
					forecast.getHour(),
					forecast.getTemperature()
			);
		}
	}

	// LIST Create
	private void addPredictions() throws IOException {

		Forecast forecast = new Forecast();

		System.out.println("year");
		int year = scan.nextInt();
		System.out.println("mouth");
		int mouth = scan.nextInt();
		System.out.println("day");
		int day = scan.nextInt();;


		LocalDate date = LocalDate.of(year,mouth,day);

		forecast.setDate(date);

		System.out.println("---- Set time of day: ----");
		int hour = scan.nextInt();
		forecast.setHour(hour);

		System.out.println("---- Set temperature ----");
		float temp = scan.nextFloat();
		forecast.setTemperature(temp);

		forecast.setId(UUID.randomUUID());
		forecastService.add(forecast);
	}

	// LIST UPDATE
	private void updatePrediction() throws IOException {

		boolean runUpdate = true;

		System.out.printf("Select a row number to update:%n");

		listPredictions();

		int sel;
		do {
			sel = scan.nextInt();
			if (sel <= 0 || sel > forecastService.getForecasts().size()){
				System.out.printf("That prediction those not exist! Try a different number.%n");
				listPredictions();
			}
		}while (sel <= 0 || sel > forecastService.getForecasts().size());

			var changeForecast = forecastService.getByIndex(sel -1);

		while(runUpdate == true){
			System.out.printf("What do you want to update?%n 1: Date%n 2: time of day%n 3: Temperature%n 9: Exit and save");
			switch (scan.nextInt()) {
				case 1 -> {

					System.out.println("yyyy");
					int year = scan.nextInt();
					System.out.println("MM");
					int mouth = scan.nextInt();
					System.out.println("dd");
					int day = scan.nextInt();

					LocalDate date = LocalDate.of(year,mouth,day);
					LocalDate data = LocalDate.parse(date.toString());

					changeForecast.setDate(data);
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

	// LIST DELETE
	private void deletePrediction() throws IOException {

		listPredictions();

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
}


