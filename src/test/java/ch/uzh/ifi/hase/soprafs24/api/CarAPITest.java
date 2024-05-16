package ch.uzh.ifi.hase.soprafs24.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* This is an integration test */
public class CarAPITest {

    private CarAPI carAPI;

    @BeforeEach
    public void setup(){
        carAPI = new CarAPI("7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e", "https://cars-by-api-ninjas.p.rapidapi.com");
    }

    @Test
    public void testPerformRequestTrue() {
        String input = "ferrari"; // Example car name to test
        String result = carAPI.performRequest(input);

        assertEquals("True", result);
    }

    @Test
    public void testPerformRequestFalse() {
        String input = "shooting star";
        String result = carAPI.performRequest(input);

        assertEquals("False", result);
    }
}
