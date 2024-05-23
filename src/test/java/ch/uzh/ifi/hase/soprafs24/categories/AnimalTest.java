package ch.uzh.ifi.hase.soprafs24.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnimalTest {

    private Animal animal;

    @BeforeEach
    public void setup() {
       animal = new Animal();
    }

    @Test
    public void validateAnswer_validInput_returnTrue(){
        boolean result = animal.validateAnswer("cat");

        assertTrue(result);
    }

    @Test
    public void validateAnswer_invalidInput_returnFalse(){
        boolean result = animal.validateAnswer("cake");

        assertFalse(result);
    }
}
