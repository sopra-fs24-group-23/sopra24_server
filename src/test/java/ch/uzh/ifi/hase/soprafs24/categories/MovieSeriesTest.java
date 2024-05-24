package ch.uzh.ifi.hase.soprafs24.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MovieSeriesTest {

    private MoviesSeries moviesSeries;

    @BeforeEach
    public void setup(){
        moviesSeries = new MoviesSeries();
    }

    @Test
    public void validateAnswer_validInput_returnTrue(){
        boolean result = moviesSeries.validateAnswer("X-Men: The Last Stand");

        assertTrue(result);
    }

    @Test
    public void validateAnswer_invalidInput_returnFalse(){
        boolean result = moviesSeries.validateAnswer("star of wars");

        assertFalse(result);
    }

}
