package ch.uzh.ifi.hase.soprafs24.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

public class FoodAPI {
    private String apiKey;
    private String baseUrl;

    public FoodAPI(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public String performRequest(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return "False";
            }

            // Trim and URL encode the query
            query = query.trim().toLowerCase();
            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            String parameters = "query=" + encodedQuery;
            URL url = new URL(baseUrl + "/v1/nutrition?" + parameters);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-RapidAPI-Key", apiKey);
            connection.setRequestProperty("X-RapidAPI-Host", "nutrition-by-api-ninjas.p.rapidapi.com");

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

                JSONArray nutritionDataArray = new JSONArray(response.toString());
                for (int i = 0; i < nutritionDataArray.length(); i++) {
                    JSONObject nutritionData = nutritionDataArray.getJSONObject(i);
                    String foodName = nutritionData.optString("name", "").toLowerCase();

                    if (foodName.equals(query)) { // Check for exact match
                        return "True";
                    }
                }
                return "False"; // No exact match found
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
