package ch.uzh.ifi.hase.soprafs24.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class CityAPI {

    private final String citiesFilePath = "/Users/nili/Desktop/UZH/FS24/SOPRA/SOPRa_TEAM23/sopra24_server/src/main/java/ch/uzh/ifi/hase/soprafs24/api/cities500 copy 2.txt";
    private final Set<String> cityNames;

    public CityAPI() {
        // Load city names from the file into a set
        this.cityNames = loadCityNamesFromFile();
    }

    private Set<String> loadCityNamesFromFile() {
        Set<String> cityNames = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(citiesFilePath))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cityObject = jsonArray.getJSONObject(i);
                String cityName = cityObject.optString("name", "").toLowerCase();
                cityNames.add(cityName);
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
        String cityName = "florida"; // Example city name
        String result = cityAPI.performRequest(cityName.toLowerCase()); // Pass the city name (converted to lowercase)

        System.out.println("Is " + cityName + " a city? " + result);
    }
}
