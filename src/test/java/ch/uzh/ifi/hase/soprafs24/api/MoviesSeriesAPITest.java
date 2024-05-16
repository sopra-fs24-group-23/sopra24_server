package ch.uzh.ifi.hase.soprafs24.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* This is an integration test */
public class MoviesSeriesAPITest {

    private MoviesSeriesAPI moviesSeriesAPI;

    @BeforeEach
    public void setup(){
        moviesSeriesAPI = new MoviesSeriesAPI("7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e", "https://imdb146.p.rapidapi.com");
    }

    @Test
    public void testPerformRequestTrue() {
        String input = "Game of Thrones";
        String result = moviesSeriesAPI.performRequest(input);

        assertEquals("True", result);
    }

    @Test
    public void testPerformRequestFalse() {
        String input = "Sleping beauty";
        String result = moviesSeriesAPI.performRequest(input);

        assertEquals("False", result);
    }
}
