package ch.uzh.ifi.hase.soprafs24.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

public class CelebrityAPI {
    private String apiKey;
    private String baseUrl;

    public CelebrityAPI(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public String performRequest(String answer) {
        try {
            if (answer == null || answer.trim().isEmpty()) {
                return "False";
            }

            // Trim the input and convert to lowercase
            answer = answer.trim().toLowerCase();

            // URL Encode the answer to handle spaces and special characters
            String encodedAnswer = URLEncoder.encode(answer, "UTF-8");
            String parameters = "name=" + encodedAnswer;
            URL url = new URL(baseUrl + "celebrity?" + parameters);
            System.out.println("Requesting URL: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-RapidAPI-Key", apiKey);
            connection.setRequestProperty("X-RapidAPI-Host", "celebrity-by-api-ninjas.p.rapidapi.com");

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

                JSONArray celebrities = new JSONArray(response.toString());
                for (int i = 0; i < celebrities.length(); i++) {
                    JSONObject celebrity = celebrities.getJSONObject(i);
                    String celebrityName = celebrity.optString("name", "").toLowerCase();

                    if (celebrityName.contains(answer)) {
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
