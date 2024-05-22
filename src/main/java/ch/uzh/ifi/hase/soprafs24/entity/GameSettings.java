package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;
import ch.uzh.ifi.hase.soprafs24.categories.City;
import ch.uzh.ifi.hase.soprafs24.categories.Country;
import ch.uzh.ifi.hase.soprafs24.categories.MoviesSeries;


import java.util.ArrayList;
import java.util.List;

public class GameSettings {
    private List<Category> categories;
    private boolean isRandom;
    private Integer maxRounds;
    private Integer votingDuration;
    private Integer inputDuration;
    private Integer scoreboardDuration;
    private Integer maxPlayers;

    private static final int MAX_ROUNDS_LIMIT = 10;
    private static final int MAX_VOTING_DURATION_LIMIT = 90;
    private static final int MAX_INPUT_DURATION_LIMIT = 90;
    private static final int MAX_SCOREBOARD_DURATION_LIMIT = 60;
    private static final int MAX_PLAYERS_LIMIT = 10;

    public GameSettings() {
        // setting standard values - TODO: set standard categories
        this.maxRounds = 5;
        this.isRandom = false;
        this.votingDuration = 30;
        this.scoreboardDuration = 15;
        this.inputDuration = 30;
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
        if (maxRounds == null || maxRounds > MAX_ROUNDS_LIMIT) {
            throw new IllegalArgumentException("Max Rounds exceeds allowed limits. Please enter a value that does not exceed " + MAX_ROUNDS_LIMIT + ".");
        } else {
            this.maxRounds = maxRounds;
        }
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        if (maxPlayers < 2) {
            throw new IllegalArgumentException("Max Players cannot be less than 2.");
        } else if (maxPlayers > MAX_PLAYERS_LIMIT) {
            throw new IllegalArgumentException("Max Players exceeds allowed limits. Please enter a value that does not exceed " + MAX_PLAYERS_LIMIT + ".");
        } else {
            this.maxPlayers = maxPlayers;
        }
    }

    public Integer getVotingDuration() {
        return votingDuration;
    }

    public void setVotingDuration(Integer votingDuration) {
        if (votingDuration == null || votingDuration > MAX_VOTING_DURATION_LIMIT) {
            throw new IllegalArgumentException("Voting Duration exceeds allowed limits. Please enter a value that does not exceed " + MAX_VOTING_DURATION_LIMIT + ".");
        } else {
            this.votingDuration = votingDuration;
        }
    }

    public Integer getScoreboardDuration() {
        return scoreboardDuration;
    }

    public void setScoreboardDuration(Integer scoreboardDuration) {
        if (scoreboardDuration == null || scoreboardDuration > MAX_SCOREBOARD_DURATION_LIMIT) {
            throw new IllegalArgumentException("Scoreboard Duration exceeds allowed limits. Please enter a value that does not exceed " + MAX_SCOREBOARD_DURATION_LIMIT + ".");
        } else {
            this.scoreboardDuration = scoreboardDuration;
        }
    }

    public Integer getInputDuration() {
        return inputDuration;
    }

    public void setInputDuration(Integer inputDuration) {
        if (inputDuration == null || inputDuration > MAX_INPUT_DURATION_LIMIT) {
            throw new IllegalArgumentException("Input Duration exceeds allowed limits. Please enter a value that does not exceed " + MAX_INPUT_DURATION_LIMIT + ".");
        } else {
            this.inputDuration = inputDuration;
        }
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public boolean getIsRandom() {
        return isRandom;
    }

    public void setIsRandom(boolean random) {
        isRandom = random;
    }
}
