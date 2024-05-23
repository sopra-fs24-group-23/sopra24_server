package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.MoviesSeriesAPI;

public class MoviesSeries extends Category {
    private static final String API_KEY = System.getenv("API_KEY_NINJA");
    private static final String BASE_URL = "https://imdb146.p.rapidapi.com";

    @Override
    public boolean validateAnswer(String answer) {
        return fetchResultsFromApi(answer).equals("True");
    }

    @Override
    public String fetchResultsFromApi(String movieTitle) {
        MoviesSeriesAPI moviesSeriesAPI = new MoviesSeriesAPI(API_KEY, BASE_URL);
        return moviesSeriesAPI.performRequest(movieTitle);
    }

    @Override
    public String getName() {
        return "Movie/Series";
    }
}
