package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;
import ch.uzh.ifi.hase.soprafs24.categories.CategoryFactory;
import ch.uzh.ifi.hase.soprafs24.categories.MoviesSeries;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.concurrent.CompletableFuture;

public class Answer {
    private final String category;
    private final String answer;
    private Boolean isUnique;
    private Boolean isJoker;
    private Boolean isDoubted;
    private Boolean isCorrect;
    private int score;

    public Answer(String category, String answer) {
        this.category = category;
        this.answer = answer;
        this.isUnique = false;
        this.isJoker = false;
        this.isDoubted = false;
        this.isCorrect = false;
    }

    public void calculateScore() {
        if (isCorrect) {
            score = isUnique ? 10 : 5;
        }

        if(!this.isJoker && this.isDoubted) {
            score += 5;
        }
    }
    public CompletableFuture<Boolean> checkAnswer(Category answerCategory, String currentLetter) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // only check answer if not a non-doubted joker
                if (isJoker && !isDoubted) {
                    return true;
                }

                // Trim the answer before performing checks
                String cleanAnswer = this.answer.toLowerCase().trim();

                // Check if the answer is empty after trimming
                if (cleanAnswer.isEmpty() || !cleanAnswer.substring(0, 1).toUpperCase().equals(currentLetter)) {
                    return false;
                } else {
                    return answerCategory.validateAnswer(cleanAnswer);
                }
            } catch (Exception e) {
                System.out.printf("There was an error while validating answers: %s \n", e.getMessage());
                return false;
            }
        });
    }


    public Boolean getIsUnique() {
        return isUnique;
    }

    @JsonSetter("isUnique")
    public void setIsUnique(Boolean duplicate) {
        isUnique = duplicate;
    }

    public Boolean getJoker() {
        return isJoker;
    }

    public void setIsJoker(Boolean joker) {
        isJoker = joker;
    }

    public Boolean getIsDoubted() {
        return isDoubted;
    }

    public void setIsDoubted(Boolean doubted) {
        isDoubted = doubted;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public String getCategory() {

        return category;
    }


    public String getAnswer() {
        return answer;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "category='" + category + '\'' +
                ", answer='" + answer + '\'' +
                ", isDoubted=" + isDoubted +
                ", isJoker=" + isJoker +
                ", isUnique=" + isUnique +
                ", isCorrect=" + isCorrect +
                '}';
    }
}
