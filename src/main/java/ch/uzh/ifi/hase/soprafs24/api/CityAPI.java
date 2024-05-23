package ch.uzh.ifi.hase.soprafs24.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;


public class CityAPI {

    private final String citiesFilePath = "src/main/resources/citynames_minpop_20k.json";
    private final Set<String> cityNames;

    public CityAPI() {
        // Load city names from the file into a set
        this.cityNames = loadCityNamesFromFile();
    }

    private Set<String> loadCityNamesFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> cityNames = new HashSet<>();
        try {
            cityNames = mapper.readValue(Paths.get(citiesFilePath).toFile(), Set.class);
        }
        catch (Exception e) {
            System.out.println("Issue while checking cities");
        }
        return cityNames;
    }

    public String performRequest(String cityName) {
        if (cityNames.contains(cityName.toLowerCase().trim())) {
            return "True";
        } else {
            return "False";
        }
    }
}
