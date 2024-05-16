package ch.uzh.ifi.hase.soprafs24.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* This is an integration test */
public class FoodAPITest {

    private FoodAPI foodAPI;

    @BeforeEach
    public void setup(){
        foodAPI = new FoodAPI("7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e", "https://nutrition-by-api-ninjas.p.rapidapi.com");
    }

    @Test
    public void testPerformRequestTrue() {
        String input = "pancake";
        String result = foodAPI.performRequest(input);

        assertEquals("True", result);
    }

    @Test
    public void testPerformRequestFalse() {
        String input = "laundry";
        String result = foodAPI.performRequest(input);

        assertEquals("False", result);
    }


}
