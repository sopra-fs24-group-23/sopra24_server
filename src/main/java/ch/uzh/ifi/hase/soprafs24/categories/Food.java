package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.FoodAPI;

public class Food extends Category {
    private static final String API_KEY = "7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e";
    private static final String BASE_URL = "https://nutrition-by-api-ninjas.p.rapidapi.com";

    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");
    }

    @Override
    public String fetchResultsFromApi(String query) {
        FoodAPI foodAPI = new FoodAPI(API_KEY, BASE_URL);
        return foodAPI.performRequest(query);
    }

    @Override
    public String getName() {
        return "Food";
    }
}
