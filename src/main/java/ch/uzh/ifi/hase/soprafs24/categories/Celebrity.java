package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.CelebrityAPI;

public class Celebrity extends Category {
    private static final String API_KEY = "7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e";
    private static final String BASE_URL = "https://celebrity-by-api-ninjas.p.rapidapi.com/v1/";

    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");

    }

    @Override
    public String fetchResultsFromApi(String celebrityName) {
        CelebrityAPI celebrityAPI = new CelebrityAPI(API_KEY, BASE_URL);
        return celebrityAPI.performRequest(celebrityName);
    }

    @Override
    public String getName() {
        return "Celebrity";
    }
}