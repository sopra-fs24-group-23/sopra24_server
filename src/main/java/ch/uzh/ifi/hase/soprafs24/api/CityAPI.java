package ch.uzh.ifi.hase.soprafs24.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class CityAPI extends APIManager {

    public CityAPI(String apiKey, String baseUrl) {
        setApiKey(apiKey);
        setBaseUrl("https://wft-geo-db.p.rapidapi.com");
    }

    public String performRequest(String cityName) {
        try {
            String parameters = "namePrefix=" + cityName;

            URL url = new URL(getBaseUrl() + "/v1/geo/cities?" + parameters);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("X-RapidAPI-Key", getApiKey());
            connection.setRequestProperty("X-RapidAPI-Host", "wft-geo-db.p.rapidapi.com");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray cities = jsonResponse.getJSONArray("data");
                for (int i = 0; i < cities.length(); i++) {
                    JSONObject city = cities.getJSONObject(i);
                    // Check for an exact match on the "name" or "city" field
                    if (city.optString("name").equalsIgnoreCase(cityName) || city.optString("city").equalsIgnoreCase(cityName)) {
                        return "true";
                    }
                }
                return "false";
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    public static void main(String[] args) {
        String apiKey = "7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e";
        CityAPI cityAPI = new CityAPI(apiKey, "");
        String cityName = "lucerne"; // Example city name
        String result = cityAPI.performRequest(cityName); // Pass the city name

        System.out.println("Is " + cityName + " a city? " + result);
    }
}
