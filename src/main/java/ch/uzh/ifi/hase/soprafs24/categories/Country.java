package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.CountryAPI;

public class Country extends Category {
    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");
    }
    @Override
    public String fetchResultsFromApi(String countryName) {
        // CountryAPI countryAPI = new CountryAPI(API_KEY, BASE_URL);
        CountryAPI countryAPI = new CountryAPI();
        return countryAPI.performRequest(countryName);
    }

    @Override
    public String getName() {
        return "Country";
    }
}

