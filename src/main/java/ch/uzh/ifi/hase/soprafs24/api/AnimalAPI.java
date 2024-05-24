package ch.uzh.ifi.hase.soprafs24.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnimalAPI {
    private String apiKey;
    private String baseUrl;

    public AnimalAPI(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public String performRequest(String animalName) {
        if (animalName == null || animalName.trim().length() < 3) { // Minimum length for valid input
            return "False";
        }

        animalName = animalName.trim(); // Trim the input

        try {
            String encodedName = URLEncoder.encode(animalName, "UTF-8");
            URL url = new URL(baseUrl + "animals?name=" + encodedName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-RapidAPI-Key", apiKey);
            connection.setRequestProperty("X-RapidAPI-Host", "animals-by-api-ninjas.p.rapidapi.com");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONArray animals = new JSONArray(response.toString());
                for (int i = 0; i < animals.length(); i++) {
                    JSONObject animal = animals.getJSONObject(i);
                    String name = animal.getString("name").toLowerCase().trim();

                    // Split name into words
                    String[] words = name.split("\\s+");

                    // Check if any word matches animalName exactly
                    boolean matchFound = false;
                    for (String word : words) {
                        if (word.equals(animalName)) {
                            matchFound = true;
                            break;
                        }
                    }

                    if (matchFound) {
                        return "True";
                    }
                }
                return "False";
            } else {
                return "False"; // Simplified handling for non-200 responses
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "False";
        }

    }
    
}
