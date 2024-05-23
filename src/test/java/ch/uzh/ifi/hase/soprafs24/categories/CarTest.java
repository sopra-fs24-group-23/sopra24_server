package ch.uzh.ifi.hase.soprafs24.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarTest {

    private Car car;

    @BeforeEach
    public void setup(){
        car = new Car();
    }

    @Test
    public void validateAnswer_invalidInput_returnTrue(){
        boolean result = car.validateAnswer("renault");

        assertTrue(result);
    }

    @Test
    public void validateAnswer_invalidInput_returnFalse(){
        boolean result = car.validateAnswer("speedy");

        assertFalse(result);
    }
}
