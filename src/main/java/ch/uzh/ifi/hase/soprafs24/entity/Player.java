package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal Player Representation
 * A Player is a User in-game, this entity is NOT PERSISTED
 */

public class Player {

    private final Long id;
    private final String username;
    private Integer currentScore;
    private List<Answer> currentAnswers;

    public Player(Long id, String username) {
        this.id = id;
        this.username = username;
        this.currentScore = 0;
        this.currentAnswers = new ArrayList<Answer>();
    }

    public Long getId() {
        return id;
    }


    public String getUsername() {
        return username;
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
