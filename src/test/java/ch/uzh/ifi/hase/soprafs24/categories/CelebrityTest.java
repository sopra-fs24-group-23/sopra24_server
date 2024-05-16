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
    public void testValidateAnswerTrue(){
        boolean result = celebrity.validateAnswer("Mike Tyson");

        assertTrue(result);
    }

    @Test
    public void testValidateAnswerFalse(){
        boolean result = celebrity.validateAnswer("Tiger Wods");

        assertFalse(result);
    }
}
