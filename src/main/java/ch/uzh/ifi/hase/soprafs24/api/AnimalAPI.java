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
                    String name = animal.getString("name").toLowerCase();
                    if (name.contains(animalName.toLowerCase())) {
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
   // public static void main(String[] args) {
       // AnimalAPI animalAPI = new AnimalAPI("7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e", "https://animals-by-api-ninjas.p.rapidapi.com/v1/");
        //String input = "horse"; // Example animal name to test
        //String result = animalAPI.performRequest(input);
        //System.out.println("Result: " + result);
    //}
}
