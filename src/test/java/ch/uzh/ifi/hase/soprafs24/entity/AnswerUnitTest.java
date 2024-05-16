package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.categories.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnswerUnitTest {

    @Mock
    private City mockCategory;

    @BeforeEach
    public void setup() {
        mockCategory = mock(City.class);
    }

    @Test
    public void givenAnswer_whenCorrectOrUnique_returnCorrectPoints () {
        Answer uniqueAnswer = new Answer("City", "Rotterdam");
        uniqueAnswer.setIsCorrect(true);
        uniqueAnswer.setIsUnique(true);
        uniqueAnswer.setIsJoker(false);
        uniqueAnswer.setIsDoubted(false);
        uniqueAnswer.calculateScore();
        Integer uniqueScore = uniqueAnswer.getScore();

        Answer duplicateAnswer = new Answer("City", "Rotterdam");
        duplicateAnswer.setIsCorrect(true);
        duplicateAnswer.setIsUnique(false);
        duplicateAnswer.setIsJoker(false);
        duplicateAnswer.setIsDoubted(false);
        duplicateAnswer.calculateScore();
        Integer duplicateScore = duplicateAnswer.getScore();

        assertEquals(10, uniqueScore);
        assertEquals(5, duplicateScore);
    }

    @Test
    public void givenAnswer_whenEmpty_validateAnswerNotCalledAndReturnFalse() throws ExecutionException, InterruptedException {
        Answer emptyAnswer = new Answer("City", "");
        emptyAnswer.setIsJoker(false);
        emptyAnswer.setIsDoubted(false);

        Boolean result = emptyAnswer.checkAnswer(mockCategory, "A").get();

        assertEquals(false, result);
        Mockito.verify(mockCategory, never()).validateAnswer(any());
    }

    @Test
    public void givenAnswer_whenJokerAndUndoubted_validateAnswerNotCalledAndReturnTrue() throws ExecutionException, InterruptedException {
        Answer jokerAnswer = new Answer("City", "FuntimeWonderlandCity");
        jokerAnswer.setIsJoker(true);
        jokerAnswer.setIsDoubted(false);

        Boolean result = jokerAnswer.checkAnswer(mockCategory, "F").get();

        assertEquals(true, result);
        Mockito.verify(mockCategory, never()).validateAnswer(any());
    }

    @Test
    public void givenAnswer_whenJokerAndDoubted_validateAnswerCalled() throws ExecutionException, InterruptedException {
        Mockito.when(mockCategory.validateAnswer(any())).thenReturn(false);

        Answer jokerAnswer = new Answer("City", "AbsoluteDillyDallyBogusAnswer");
        jokerAnswer.setIsJoker(true);
        jokerAnswer.setIsDoubted(true);

        Boolean result = jokerAnswer.checkAnswer(mockCategory, "A").get();

        assertEquals(false, result);
        Mockito.verify(mockCategory, times(1)).validateAnswer(any());
    }


}
