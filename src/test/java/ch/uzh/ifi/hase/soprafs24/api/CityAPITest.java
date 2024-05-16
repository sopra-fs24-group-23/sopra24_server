package ch.uzh.ifi.hase.soprafs24.api;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CityAPITest {
    private CityAPI cityApi;

    @BeforeEach
    public void setup(){
        cityApi = new CityAPI();
    }

    @Test
    public void testPerformRequestTrue() {
        String input = "Luzern";
        String result = cityApi.performRequest(input);

        assertEquals("True", result);
    }

    @Test
    public void testPerformRequestFalse() {
        String input = "FuntimeWonderCity";
        String result = cityApi.performRequest(input);

        assertEquals("False", result);
    }
}
