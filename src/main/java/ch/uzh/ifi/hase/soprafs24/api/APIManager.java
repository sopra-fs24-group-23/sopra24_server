package ch.uzh.ifi.hase.soprafs24.api;

public abstract class APIManager {
    private String apiKey;
    private String baseUrl;

    public abstract String performRequest(String answer);

    // Inside your APIManager class
    protected void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    protected void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected String getApiKey() {
        return this.apiKey;
    }

    protected String getBaseUrl() {
        return this.baseUrl;
    }



}
