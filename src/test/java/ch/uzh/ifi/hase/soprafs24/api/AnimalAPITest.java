package ch.uzh.ifi.hase.soprafs24.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* This is an integration test */

public class AnimalAPITest {

    private AnimalAPI animalAPI;

    @BeforeEach
    public void setup() {
        animalAPI = new AnimalAPI("7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e", "https://animals-by-api-ninjas.p.rapidapi.com/v1/");
    }

    @Test
    public void testPerformRequest() throws Exception {

        String input = "fox"; // Example animal name to test
        String result = animalAPI.performRequest(input);

        assertEquals("True", result);
    }
}
