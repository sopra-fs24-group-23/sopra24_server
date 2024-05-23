package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.AnimalAPI;

public class Animal extends Category {
    private static final String API_KEY = System.getenv("API_KEY_NINJA");
    private static final String BASE_URL = "https://animals-by-api-ninjas.p.rapidapi.com/v1/";

    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");
    }

    @Override
    public String fetchResultsFromApi(String animalName) {
        AnimalAPI animalAPI = new AnimalAPI(API_KEY, BASE_URL);
        return animalAPI.performRequest(animalName);
    }

    @Override
    public String getName() {
        return "Animal";
    }
}