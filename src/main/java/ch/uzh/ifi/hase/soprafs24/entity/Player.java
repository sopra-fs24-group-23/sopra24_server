package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;

/**
 * Internal Player Representation
 * A Player is a User in-game, this entity is NOT PERSISTED
 */

public class Player {

    private Long id;
    private String username;
    private Integer currentScore;
    private List<Answer> currentAnswers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
