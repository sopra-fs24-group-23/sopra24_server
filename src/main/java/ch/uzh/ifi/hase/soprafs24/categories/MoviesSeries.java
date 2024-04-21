package ch.uzh.ifi.hase.soprafs24.categories;

import ch.uzh.ifi.hase.soprafs24.api.MoviesSeriesAPI;

public class MoviesSeries implements Category {
    private static final String API_KEY = "7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e";  // Use your actual API key
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
        return "Movie";
    }
}
