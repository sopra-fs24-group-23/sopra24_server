package ch.uzh.ifi.hase.soprafs24.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class CountryAPI extends APIManager {

    public CountryAPI(String apiKey, String baseUrl) {
        setApiKey(apiKey);
        setBaseUrl("https://country-by-api-ninjas.p.rapidapi.com");
    }

    public String performRequest(String countryName) {
        try {
            String parameters = "name=" + countryName.replace(" ", "%20");

            URL url = new URL(getBaseUrl() + "/v1/country?" + parameters);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("X-RapidAPI-Key", getApiKey());
            connection.setRequestProperty("X-RapidAPI-Host", "country-by-api-ninjas.p.rapidapi.com");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONArray countries = new JSONArray(response.toString());
                if (countries.length() > 0) {
                    JSONObject country = countries.getJSONObject(0);
                    if (country.getString("name").equalsIgnoreCase(countryName)) {
                        return "True";
                    }
                }
                return "False";
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "False";
    }

    public static void main(String[] args) {
        String apiKey = "7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e";
        CountryAPI countryAPI = new CountryAPI(apiKey, "");
        String countryName = "South Africa"; // Example country name
        String result = countryAPI.performRequest(countryName); // Pass the country name

        System.out.println("Is " + countryName + " a country? " + result);
    }
}
