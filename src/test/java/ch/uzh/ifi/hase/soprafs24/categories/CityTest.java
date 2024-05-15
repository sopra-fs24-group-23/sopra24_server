package ch.uzh.ifi.hase.soprafs24.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CityTest {

    private City city;

    @BeforeEach
    public void setup(){
        city = new City();
    }

    @Test
    public void testValidateAnswerTrue(){
        boolean result = city.validateAnswer("Paris");

        assertTrue(result);
    }

    @Test
    public void testValidateAnswerFalse(){
        boolean result = city.validateAnswer("Switzerland");

        assertFalse(result);
    }
}
