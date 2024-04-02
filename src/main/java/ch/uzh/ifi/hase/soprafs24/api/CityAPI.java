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
        setBaseUrl(baseUrl);
    }

    public String performRequest(String answer) {
        try {
            String parameters = "fields=value&lang=en&name=" + answer + "&limit=250";
            URL url = new URL(getBaseUrl() + "/v1/city?" + parameters);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("X-RapidAPI-Key", getApiKey());
            connection.setRequestProperty("X-RapidAPI-Host", "referential.p.rapidapi.com");

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

                JSONArray cities = new JSONArray(response.toString());
                for (int i = 0; i < cities.length(); i++) {
                    JSONObject city = cities.getJSONObject(i);
                    if (city.optString("value").equalsIgnoreCase(answer)) {
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
        String baseUrl = "https://referential.p.rapidapi.com";

        CityAPI cityAPI = new CityAPI(apiKey, baseUrl);
        String cityName = "luzern"; // Example city name
        String result = cityAPI.performRequest(cityName); // Pass the city name

        System.out.println("Is " + cityName + " a city? " + result);
    }
}
