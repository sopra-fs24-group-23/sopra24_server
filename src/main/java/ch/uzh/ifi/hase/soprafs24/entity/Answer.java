package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.Category;
import ch.uzh.ifi.hase.soprafs24.categories.CategoryFactory;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Answer {
    private final String category;
    private final String answer;
    private Boolean isUnique;
    private Boolean isJoker;
    private Boolean isDoubted;
    private Boolean isCorrect;

    public Answer(String category, String answer) {
        this.category = category;
        this.answer = answer;
    }

    public int calculateScore(String currentLetter) {

        // if answer starts with wrong letter, abort.
        if (!answer.substring(0,1).toUpperCase().equals(currentLetter)) {
            return 0;
        }

        // if answer starts with correct letter, make API call
        try {
            this.isCorrect = checkAnswer();
        }
        catch (ExecutionException e ) {
            System.out.printf(
                    "There was an execution-exception trying to fetch results for answer %s category %s \n",
                    answer, category);
        }
        catch (InterruptedException e) {
            System.out.printf(
                    "There was an interrupted-exception trying to fetch results for answer %s category %s \n",
                    answer, category);
        }

        // with all attributes set, calculate the score
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
            score = isUnique ? 5 : 10;
        }

        // award extra points if wrongfully doubted
        if(!this.isJoker && this.isDoubted) {
            score += 5;
        }

        return score;
    };

    public boolean checkAnswer() throws ExecutionException, InterruptedException {
        Category answerCategory = CategoryFactory.createCategory(this.category);

        // Perform the API call asynchronously
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            return answerCategory.validateAnswer(this.answer);
        });

        // Wait for the result before returning
        return future.get();
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
