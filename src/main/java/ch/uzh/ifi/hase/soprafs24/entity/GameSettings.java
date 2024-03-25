package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;

import java.util.List;

public class GameSettings {
    private List<Category> categories;
    private Integer maxRounds;
    private Integer timeLimit; // in seconds

    public GameSettings() {
        // TODO: set standard categories
        this.maxRounds = 5;
        this.timeLimit = 30;
    }

    public GameSettings(Integer maxRounds, Integer timeLimit) {
        this.maxRounds = maxRounds;
        this.timeLimit = timeLimit;
    }


    public Integer getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(Integer maxRounds) {
        this.maxRounds = maxRounds;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }
}
