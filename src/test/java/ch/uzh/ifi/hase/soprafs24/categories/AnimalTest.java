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
    public void testValidateAnswerTrue(){
        boolean result = animal.validateAnswer("cat");

        assertTrue(result);
    }

    @Test
    public void testValidateAnswerFalse(){
        boolean result = animal.validateAnswer("cake");

        assertFalse(result);
    }
}
