package ch.uzh.ifi.hase.soprafs24.websocket;

import static ch.uzh.ifi.hase.soprafs24.constant.GamePhase.AWAITING_ANSWERS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;


import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebsocketController;
import ch.uzh.ifi.hase.soprafs24.events.GameStateChangeEvent;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

public class GameWebsocketControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameWebsocketController controller;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        controller.setMessagingTemplate(messagingTemplate);
    }

    @Test
    public void handleGameStateChange_validInputs_sendsMessageToClients() {
        // Given
        Player player1 = new Player(1L,"player1","qsdwq", "#000000");
        Player player2 =  new Player(2L,"player2","fre", "#000000");

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        GameState gameState = new GameState("a",AWAITING_ANSWERS,players,1);
        GameStateChangeEvent event = new GameStateChangeEvent(this,gameState, "gameId");

        // When
        controller.handleGameStateChange(event);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/games/gameId/state"),
                any(GameStateDTO.class)
        );
    }

    @Test
    public void receiveAnswers_validInputs_setsAnswersThroughGameService() {
        // Given
        String lobbyId = "lobbyId";
        String username = "username";
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("country","albania"));

        // When
        controller.receiveAnswers(lobbyId, username, answers);

        // Then
        verify(gameService).setAnswers(lobbyId, username, answers);
    }

    @Test
    public void updateClients_validInputs_informsClients() {
        // Given
        Player player1 = new Player(1L,"player1","qsdwq", "#000000");
        Player player2 =  new Player(2L,"player2","fre", "#000000");

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        String gameId = "gameId";
        GameState gameState = new GameState("a",AWAITING_ANSWERS, players,3);
        when(gameService.getGameState(gameId)).thenReturn(gameState);

        // When
        controller.updateClients(gameId);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/games/gameId/state"),
                any(GameStateDTO.class)
        );
    }

    @Test
    public void closeInput_validInputs_callsGameService() {
        // Given
        String lobbyId = "lobbyId";

        // When
        controller.closeInputs(lobbyId);

        // Then
        verify(gameService).closeInputs(lobbyId);
    }

    @Test
    public void handleGameStateChange_validInputs_sendsRelevantData() {

        // initialize players & answers
        Player player1 = new Player(1L,"player1","qsdwq", "#000000");
        Answer answer1 = new Answer("City", "Paris");
        List<Answer> answerList1 = new ArrayList<>();
        answerList1.add(answer1);
        player1.setCurrentAnswers(answerList1);
        player1.setCurrentScore(100);

        Player player2 =  new Player(2L,"player2","fre", "#000000");
        Answer answer2 = new Answer("City", "Pittsburgh");
        List<Answer> answerList2 = new ArrayList<>();
        answerList2.add(answer2);
        player2.setCurrentAnswers(answerList2);
        player2.setCurrentScore(300);

        // initialize game
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        GameSettings settings = new GameSettings();

        Game newGame = new Game(settings, players);
        newGame.setCurrentLetter("P");

        GameStateChangeEvent changeEvent = new GameStateChangeEvent(newGame, newGame.getState(), "testId");

        ArgumentCaptor<GameStateDTO> captor = ArgumentCaptor.forClass(GameStateDTO.class);

        controller.handleGameStateChange(changeEvent);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/games/testId/state"),
                captor.capture()
        );

        GameStateDTO sentDTO = captor.getValue();

        assertEquals("P", sentDTO.getCurrentLetter());
        assertEquals("Paris", sentDTO.getPlayers().get(0).getCurrentAnswers().get(0).getAnswer());
        assertEquals("Pittsburgh", sentDTO.getPlayers().get(1).getCurrentAnswers().get(0).getAnswer());
        for (PlayerGetDTO dto : sentDTO.getPlayers()) {
            assertTrue(dto.getUsername().equals("player1") || dto.getUsername().equals("player2"));
            assertTrue(
                    dto.getCurrentAnswers().get(0).getAnswer().equals("Paris") ||
                            dto.getCurrentAnswers().get(0).getAnswer().equals("Pittsburgh")
            );
            assertTrue(dto.getCurrentScore() == 100 || dto.getCurrentScore() == 300);
        }
    }

}
