package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;

import java.util.List;

public class GameSettings {
    private List<Category> categories;
    private Integer maxRounds;
    private Integer votingDuration;

    private Integer maxRoundsDuration;
    private Integer scoreboardDuration;
    private Integer maxPlayers;

    public GameSettings() {
        // setting standard values - TODO: set standard categories
        this.maxRounds = 5;
        this.votingDuration = 30;
        this.scoreboardDuration = 30;
        this.maxRoundsDuration = 500;
        this.maxPlayers = 4;
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

    public Integer getMaxRoundsDuration() {
        return maxRoundsDuration;
    }

    public void setMaxRoundsDuration(Integer maxRoundsDuration) {
        this.maxRoundsDuration = maxRoundsDuration;
    }
}
