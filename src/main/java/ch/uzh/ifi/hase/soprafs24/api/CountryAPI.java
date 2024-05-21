package ch.uzh.ifi.hase.soprafs24.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

public class CountryAPI extends APIManager {

    private final String citiesFilePath = "src/main/java/ch/uzh/ifi/hase/soprafs24/constant/countries.json";
    private final Set<String> countryNames;

    /*
    public CountryAPI(String apiKey, String baseUrl) {
        this.countryNames = loadCountriesFromFile();
        setApiKey(apiKey);
        setBaseUrl("https://country-by-api-ninjas.p.rapidapi.com");
    }
    */

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
