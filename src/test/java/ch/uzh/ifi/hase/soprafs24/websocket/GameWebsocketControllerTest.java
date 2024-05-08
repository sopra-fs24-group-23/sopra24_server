package ch.uzh.ifi.hase.soprafs24.websocket;

import static ch.uzh.ifi.hase.soprafs24.constant.GamePhase.AWAITING_ANSWERS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;


import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebsocketController;
import ch.uzh.ifi.hase.soprafs24.entity.Answer;
import ch.uzh.ifi.hase.soprafs24.entity.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.events.GameStateChangeEvent;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
    public void testHandleGameStateChange() {
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
    public void testReceiveAnswers() {
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
    public void testUpdateClients() {
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
    public void testCloseInputs() {
        // Given
        String lobbyId = "lobbyId";

        // When
        controller.closeInputs(lobbyId);

        // Then
        verify(gameService).closeInputs(lobbyId);
    }

    //Todo Continue adding tests for other methods such as 'receivedDoubt' and more.

}
