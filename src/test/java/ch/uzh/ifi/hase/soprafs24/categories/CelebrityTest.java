package ch.uzh.ifi.hase.soprafs24.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CelebrityTest {

    private Celebrity celebrity;

    @BeforeEach
    public void setup(){
        celebrity = new Celebrity();
    }

    @Test
    public void validateAnswer_validInput_returnTrue(){
        boolean result = celebrity.validateAnswer("Xi Jinping");

        assertTrue(result);
    }

    @Test
    public void validateAnswer_invalidInput_returnFalse(){
        boolean result = celebrity.validateAnswer("Tiger Wods");

        assertFalse(result);
    }
}
