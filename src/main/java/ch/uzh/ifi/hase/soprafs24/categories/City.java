package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.CityAPI;

public class City implements Category {
    private static final String API_KEY = "X-RapidAPI-Key";
    private static final String BASE_URL = "https://wft-geo-db.p.rapidapi.com";
    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");

    }
    @Override
    public String fetchResultsFromApi(String cityName) {
       CityAPI cityAPI = new CityAPI(API_KEY, BASE_URL);
       return cityAPI.performRequest(cityName);
    }

    @Override
    public String getName() {
        return "City";
    }
}
