package ch.uzh.ifi.hase.soprafs24.api;

import ch.uzh.ifi.hase.soprafs24.api.APIManager;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class CityAPI extends APIManager {

    private final String citiesFilePath = "/Users/nili/Desktop/UZH/FS24/SOPRA/SOPRa_TEAM23/sopra24_server/src/main/java/ch/uzh/ifi/hase/soprafs24/api/cities500 copy 2.txt";
    private final Set<String> cityNames;

    public CityAPI() {
        // Load city names from the file into a set
        this.cityNames = loadCityNamesFromFile();
    }

    private Set<String> loadCityNamesFromFile() {
        Set<String> cityNames = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(citiesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Assuming each line contains a JSON object, parse the JSON and extract the "name" field
                int startIndex = line.indexOf("\"name\":\"") + 8;
                int endIndex = line.indexOf("\"", startIndex);
                if (startIndex >= 0 && endIndex >= 0) {
                    String cityName = line.substring(startIndex, endIndex);
                    cityNames.add(cityName.toLowerCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityNames;
    }

    public String performRequest(String cityName) {
        if (cityNames.contains(cityName.toLowerCase())) {
            return "True";
        } else {
            return "False";
        }
    }

    public static void main(String[] args) {
        CityAPI cityAPI = new CityAPI();
        String cityName = "andorra la vella"; // Example city name
        String result = cityAPI.performRequest(cityName.toLowerCase()); // Pass the city name (converted to lowercase)

        System.out.println("Is " + cityName + " a city? " + result);
    }
}