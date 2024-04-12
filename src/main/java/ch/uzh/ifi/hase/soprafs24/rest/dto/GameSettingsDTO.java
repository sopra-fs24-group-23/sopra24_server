package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class GameSettingsDTO {
    private List<String> categories;
    private Integer maxRounds;
    private Integer votingDuration;
    private Integer maxRoundsDuration;
    private Integer scoreboardDuration;
    private Integer maxPlayers;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Integer getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(Integer maxRounds) {
        this.maxRounds = maxRounds;
    }

    public Integer getVotingDuration() {
        return votingDuration;
    }

    public void setVotingDuration(Integer votingDuration) {
        this.votingDuration = votingDuration;
    }

    public Integer getMaxRoundsDuration() {
        return maxRoundsDuration;
    }

    public void setMaxRoundsDuration(Integer maxRoundsDuration) {
        this.maxRoundsDuration = maxRoundsDuration;
    }

    public Integer getScoreboardDuration() {
        return scoreboardDuration;
    }

    public void setScoreboardDuration(Integer scoreboardDuration) {
        this.scoreboardDuration = scoreboardDuration;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
