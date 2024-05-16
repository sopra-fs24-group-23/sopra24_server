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
    public void testValidateAnswerTrue(){
        boolean result = moviesSeries.validateAnswer("Bad Boys");

        assertTrue(result);
    }

    @Test
    public void testValidateAnswerFalse(){
        boolean result = moviesSeries.validateAnswer("star of wars");

        assertFalse(result);
    }

}
