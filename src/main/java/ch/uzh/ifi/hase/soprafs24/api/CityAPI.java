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
        setBaseUrl("https://city-by-api-ninjas.p.rapidapi.com");
    }

    public String performRequest(String cityName) {
        try {
            String parameters = "name=" + cityName + "&country=US&limit=1";
            URL url = new URL(getBaseUrl() + "/v1/city?" + parameters);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("X-RapidAPI-Key", getApiKey());
            connection.setRequestProperty("X-RapidAPI-Host", "city-by-api-ninjas.p.rapidapi.com");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Raw Response: " + response.toString()); // Log raw response
            if (responseCode == HttpURLConnection.HTTP_OK && response.length() > 0) {
                JSONArray cities = new JSONArray(response.toString());
                if (cities.length() > 0) {
                    JSONObject city = cities.getJSONObject(0);
                    if (city.getString("name").equalsIgnoreCase(cityName)) {
                        return "True";
                    }
                }
                return "False";
            }
            else {
                System.out.println("GET request not worked or empty response");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "False";
    }

 //   public static void main(String[] args) {
   //     String apiKey = "7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e";
     //   CityAPI cityAPI = new CityAPI(apiKey, "");
       // String cityName = "New York"; // Example city name
        //String result = cityAPI.performRequest(cityName); // Pass the city name

       // System.out.println("Is " + cityName + " a city? " + result);
   // }
}
