package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.CountryAPI;

public class Country implements Category {
    private static final String API_KEY = "X-RapidAPI-Key";
    private static final String BASE_URL = "https://referential.p.rapidapi.com";
    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("true");
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

