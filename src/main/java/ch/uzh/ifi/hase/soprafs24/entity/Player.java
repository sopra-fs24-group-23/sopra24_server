package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal Player Representation
 * A Player is a User in-game, this entity is NOT PERSISTED
 */

public class Player {

    private Long id;
    private String color;
    private String token;
    private String username;
    private Integer currentScore;
    private List<Answer> currentAnswers = new ArrayList<>();
    private boolean hasAnswered;
    private boolean hasVoted;
    private boolean isHost;

    public Player(Long id, String username, String token, String color) {
        this.color = color;
        this.id = id;
        this.token = token;
        this.username = username;
        this.currentScore = 0;
        this.currentAnswers = new ArrayList<>();
    }

    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    public List<Answer> getCurrentAnswers() {
        return currentAnswers;
    }

    public void setCurrentAnswers(List<Answer> currentAnswers) {
        this.currentAnswers = currentAnswers;
    }

    public boolean getHasAnswered() {
        return hasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }

    public boolean getHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getIsHost() {
        return isHost;
    }

    public void setIsHost(boolean host) {
        isHost = host;
    }
}
