package ch.uzh.ifi.hase.soprafs24.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FoodTest {

    private Food food;

    @BeforeEach
    public void setup(){
        food = new Food();
    }

    @Test
    public void validateAnswer_invalidInput_returnTrue(){
        boolean result = food.validateAnswer("fruit");

        assertTrue(result);
    }

    @Test
    public void validateAnswer_invalidInput_returnFalse(){
        boolean result = food.validateAnswer("book");

        assertFalse(result);
    }
}
