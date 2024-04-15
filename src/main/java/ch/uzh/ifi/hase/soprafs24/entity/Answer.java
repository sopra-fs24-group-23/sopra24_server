package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;

import javax.persistence.*;

public class Answer {
    private final Category category;
    private final String answer;
    private Boolean isDuplicate;
    private Boolean isJoker;
    private Boolean isDoubted;
    private Boolean isCorrect;

    public Answer(Category category, String answer) {
        this.category = category;
        this.answer = answer;
    }
    // TODO: add mechanism to punish wrongful doubting

    public int calculateScore() {
        int score = 0;
        // if the answer is a non-doubted joker
        if (this.isJoker && !this.isDoubted) {
            this.isCorrect = true;
        }
        else {
            this.isCorrect = category.validateAnswer(this.answer);
        }

        if (isCorrect) {
            score = isDuplicate ? 5 : 10;
        }

        return score;
    };

    public Boolean getDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        isDuplicate = duplicate;
    }

    public Boolean getJoker() {
        return isJoker;
    }

    public void setJoker(Boolean joker) {
        isJoker = joker;
    }

    public Boolean getDoubted() {
        return isDoubted;
    }

    public void setDoubted(Boolean doubted) {
        isDoubted = doubted;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

}
