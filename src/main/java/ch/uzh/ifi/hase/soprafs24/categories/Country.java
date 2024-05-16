package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.CountryAPI;

public class Country extends Category {
    private static final String API_KEY = "7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e";
    private static final String BASE_URL = "https://country-by-api-ninjas.p.rapidapi.com";
    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");
    }
    @Override
    public String fetchResultsFromApi(String countryName) {
        CountryAPI countryAPI = new CountryAPI(API_KEY, BASE_URL);
        return countryAPI.performRequest(countryName);
    }

    @Override
    public String getName() {
        return "Country";
    }
}

