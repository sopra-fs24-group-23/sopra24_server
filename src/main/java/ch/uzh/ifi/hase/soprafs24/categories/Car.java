package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.CarAPI;

public class Car extends Category {
    private static final String API_KEY = System.getenv("API_KEY_NINJA");
    private static final String BASE_URL = "https://cars-by-api-ninjas.p.rapidapi.com";

    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");
    }

    @Override
    public String fetchResultsFromApi(String make) {
        CarAPI carAPI = new CarAPI(API_KEY, BASE_URL);
        return carAPI.performRequest(make);
    }

    @Override
    public String getName() {
        return "Car";
    }
}
