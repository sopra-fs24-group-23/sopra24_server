package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;
import ch.uzh.ifi.hase.soprafs24.categories.City;
import ch.uzh.ifi.hase.soprafs24.categories.Country;
import ch.uzh.ifi.hase.soprafs24.categories.MoviesSeries;


import java.util.ArrayList;
import java.util.List;

public class GameSettings {
    private List<Category> categories;
    private Integer maxRounds;
    private Integer votingDuration;
    private Integer inputDuration;
    private Integer scoreboardDuration;
    private Integer maxPlayers;

    public GameSettings() {
        // setting standard values - TODO: set standard categories
        this.maxRounds = 2;
        this.votingDuration = 3;
        this.scoreboardDuration = 3;
        this.inputDuration = 3;
        this.maxPlayers = 5;
        // setting standard categories
        this.categories = new ArrayList<>();
        this.categories.add(new City());
        this.categories.add(new Country());
        this.categories.add(new MoviesSeries());

    }

    public Integer getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(Integer maxRounds) {
        this.maxRounds = maxRounds;
    }


    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getVotingDuration() {
        return votingDuration;
    }

    public void setVotingDuration(Integer votingDuration) {
        this.votingDuration = votingDuration;
    }

    public Integer getScoreboardDuration() {
        return scoreboardDuration;
    }

    public void setScoreboardDuration(Integer scoreboardDuration) {
        this.scoreboardDuration = scoreboardDuration;
    }

    public Integer getInputDuration() {
        return inputDuration;
    }

    public void setInputDuration(Integer inputDuration) {
        this.inputDuration = inputDuration;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
