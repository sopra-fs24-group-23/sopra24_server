package ch.uzh.ifi.hase.soprafs24.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountryTest {

    private Country country;

    @BeforeEach
    public void setup(){
        country = new Country();
    }

    @Test
    public void testValidateAnswerTrue(){
        boolean result = country.validateAnswer("Japan");

        assertTrue(result);
    }

    @Test
    public void testValidateAnswerFalse(){
        boolean result = country.validateAnswer("Wonderland");

        assertFalse(result);
    }
}
