package ch.uzh.ifi.hase.soprafs24.api;

public abstract class APIManager {
    private String apiKey;
    private String baseUrl;

    public abstract String performRequest(String parameters);
}
