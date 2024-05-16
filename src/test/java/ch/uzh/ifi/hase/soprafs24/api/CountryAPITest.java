package ch.uzh.ifi.hase.soprafs24.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* This is an integration test */
public class CountryAPITest {

    private CountryAPI countryAPI;

    @BeforeEach
    public void setup(){
        countryAPI = new CountryAPI("7f9f1b12c5msh0ee2d0b9a2cbbb7p158dc9jsn62d752680b9e", "https://country-by-api-ninjas.p.rapidapi.com");
    }

    @Test
    public void testPerformRequestTrue() {
        String input = "Austria";
        String result = countryAPI.performRequest(input);

        assertEquals("True", result);
    }

    @Test
    public void testPerformRequestFalse() {
        String input = "Dreamland";
        String result = countryAPI.performRequest(input);

        assertEquals("False", result);
    }
}
