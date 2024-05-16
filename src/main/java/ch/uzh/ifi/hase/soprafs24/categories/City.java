package ch.uzh.ifi.hase.soprafs24.categories;
import ch.uzh.ifi.hase.soprafs24.api.CityAPI;

public class City extends Category {

    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");

    }
    @Override
    public String fetchResultsFromApi(String cityName) {
       CityAPI cityAPI = new CityAPI();
       return cityAPI.performRequest(cityName);
    }

    @Override
    public String getName() {
        return "City";
    }
}
