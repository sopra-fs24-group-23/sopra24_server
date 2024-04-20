package ch.uzh.ifi.hase.soprafs24.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.json.JSONArray;

public class MoviesSeriesAPI extends APIManager {

    public MoviesSeriesAPI(String apiKey,String baseUrl) {
        setApiKey(apiKey);
        setBaseUrl(baseUrl);
    }

    private String normalize(String input) {
        // Remove all non-alphanumeric characters (except spaces) and convert to lower case
        return input.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "").toLowerCase();
    }
//test
    public String performRequest(String query) {
        try {
            String normalizedQuery = normalize(query);
            String parameters = "query=" + query; // URL uses original query

            URL url = new URL(getBaseUrl() + "/v1/find/?" + parameters);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("X-RapidAPI-Key", getApiKey());
            connection.setRequestProperty("X-RapidAPI-Host", "imdb146.p.rapidapi.com");

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
                JSONObject titleResults = jsonResponse.optJSONObject("titleResults");
                if (titleResults != null) {
                    JSONArray results = titleResults.optJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject item = results.getJSONObject(i);
                        String titleNameText = normalize(item.optString("titleNameText", ""));
                        if (titleNameText.equals(normalizedQuery)) {
                            return "True";  // Title matches the normalized query exactly
                        }
                    }
                }
                return "False"; // No exact title match found
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
        String baseUrl = "https://imdb146.p.rapidapi.com";
       // MoviesSeriesAPI imdbAPI = new MoviesSeriesAPI(apiKey,baseUrl);
        String query = "spiderman "; // Example query
       // String result = imdbAPI.performRequest(query);

        //System.out.println("Does " + query + " exist? " + result);
    }
}
