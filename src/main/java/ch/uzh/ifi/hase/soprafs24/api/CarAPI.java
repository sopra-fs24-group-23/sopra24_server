package ch.uzh.ifi.hase.soprafs24.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

public class CarAPI {
    private String apiKey;
    private String baseUrl;

    public CarAPI(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public String performRequest(String make) {
        try {
            if (make == null || make.trim().isEmpty()) {
                return "False";
            }

            // Trim the input and convert to lower case
            make = make.trim().toLowerCase();

            String encodedMake = URLEncoder.encode(make, "UTF-8");
            String parameters = "make=" + encodedMake;
            URL url = new URL(baseUrl + "/v1/cars?" + parameters);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-RapidAPI-Key", apiKey);
            connection.setRequestProperty("X-RapidAPI-Host", "cars-by-api-ninjas.p.rapidapi.com");

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

                JSONArray cars = new JSONArray(response.toString());
                for (int i = 0; i < cars.length(); i++) {
                    JSONObject car = cars.getJSONObject(i);
                    String carMake = car.optString("make", "").toLowerCase();

                    if (carMake.equals(make)) { // Check for exact match
                        return "True";
                    }
                }
                return "False";
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String inputLine;
                StringBuilder errorResponse = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
                in.close();
                System.out.println("Error Response: " + errorResponse.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "False";
    }

}
