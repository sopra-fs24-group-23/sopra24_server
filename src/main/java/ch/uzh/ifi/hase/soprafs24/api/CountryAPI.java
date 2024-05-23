package ch.uzh.ifi.hase.soprafs24.api;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CountryAPI extends APIManager {

    private final String citiesFilePath = "src/main/resources/countries.json";
    private final Set<String> countryNames;

    public CountryAPI() {
        this.countryNames = loadCountriesFromFile();
    }

    private Set<String> loadCountriesFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> countryNames = new HashSet<>();
        try {
            countryNames = mapper.readValue(Paths.get(citiesFilePath).toFile(), Set.class);
        }
        catch (Exception e) {
            System.out.println("Issue while loading Countries");
        }
        return countryNames;
    }

    public String performRequest(String countryName) {
        if (countryNames.contains(countryName.toLowerCase().trim())) {
            return "True";
        }
        else {
            return "False";
        }
    }
}
