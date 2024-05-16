package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.Answer;

import java.util.List;

public class PlayerGetDTO {

    private Long id;
    private String username;
    private Integer currentScore;
    private List<Answer> currentAnswers;
    private boolean hasAnswered;
    private boolean hasVoted;
    private boolean isHost;
    private String color;

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

    public boolean isHasAnswered() {
        return hasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public boolean getIsHost() {
        return isHost;
    }

    public void setIsHost(boolean host) {
        isHost = host;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
