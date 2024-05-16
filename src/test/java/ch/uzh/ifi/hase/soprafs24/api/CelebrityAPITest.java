package ch.uzh.ifi.hase.soprafs24.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* This is an integration test */
public class CelebrityAPITest {

    private CelebrityAPI celebrityAPI;

    @BeforeEach
    public void setup(){
        celebrityAPI = new CelebrityAPI("7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e", "https://celebrity-by-api-ninjas.p.rapidapi.com/v1/");
    }

    @Test
    public void testPerformRequestTrue() {
        String input = "Arnold Schwarzenegger"; // Example celebrity name to test
        String result = celebrityAPI.performRequest(input);

        assertEquals("True", result);
    }

    @Test
    public void testPerformRequestFalse() {
        String input = "Not Famous";
        String result = celebrityAPI.performRequest(input);

        assertEquals("False", result);
    }
}
