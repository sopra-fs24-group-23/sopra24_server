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
        setBaseUrl(baseUrl);
    }

    // Adjust the method to take a country name as a parameter
    public String performRequest(String answer) {
        try {
            // Adjust parameters to fetch all countries without limit
            // Note: Depending on the API, you may need to adjust this query to properly fetch or search for countries
            String parameters = "fields=name";

            URL url = new URL(getBaseUrl() + "/v1/country?" + parameters);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Set API key header
            connection.setRequestProperty("X-RapidAPI-Key", getApiKey());
            connection.setRequestProperty("X-RapidAPI-Host", "referential.p.rapidapi.com");

            // Check the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the response to check if the country exists
                JSONArray countries = new JSONArray(response.toString());
                System.out.println(countries);
                for (int i = 0; i < countries.length(); i++) {
                    JSONObject country = countries.getJSONObject(i);
                    String countryValue = country.optString("value");
                    if (countryValue.toLowerCase().contains(answer.toLowerCase())) {
                        return "correct";
                    }
                }
                return "false";
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false"; // Return "false" or an appropriate error message if an exception occurs
    }

    public static void main(String[] args) {
        // Use your actual API key and the base URL for the API
        String apiKey = "7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e";
        String baseUrl = "https://referential.p.rapidapi.com";

       // CountryAPI countryAPI = new CountryAPI(apiKey, baseUrl);
       // String answer = "china"; // Example country name
      //  String result = countryAPI.performRequest(answer); // Pass the country name

      //  System.out.println("Is " + answer + " a country? " + result);
    }
}
