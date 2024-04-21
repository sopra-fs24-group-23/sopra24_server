package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;

public class Answer {
    private final String category;
    private final String answer;
    private Boolean isDuplicate;
    private Boolean isJoker;
    private Boolean isDoubted;
    private Boolean isCorrect;

    public Answer(String category, String answer) {
        this.category = category;
        this.answer = answer;
    }
    // TODO: add mechanism to punish wrongful doubting

    public int calculateScore() {
        int score = 0;

        // only check answer if it is not a non-doubted joker
        if (this.isJoker && !this.isDoubted) {
            this.isCorrect = true;
        }
        else {
            //this.isCorrect = category.validateAnswer(this.answer);
        }

        // set score according to uniqueness
        if (isCorrect) {
            score = isDuplicate ? 5 : 10;
        }

        // award extra points if wrongfully doubted
        if(!this.isJoker && this.isDoubted) {
            score += 5;
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

    public String getCategory() {
        return category;
    }

}
