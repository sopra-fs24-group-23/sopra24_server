package ch.uzh.ifi.hase.soprafs24.websocket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.controller.LobbyWebsocketController;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTokenDTO;
import ch.uzh.ifi.hase.soprafs24.events.LobbyClosedEvent;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class LobbyWebsocketControllerTest {

    @Mock
    private LobbyService lobbyService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @InjectMocks
    private LobbyWebsocketController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleLobbyClosedEvent() {
        // Given
        LobbyClosedEvent event = new LobbyClosedEvent(this, "testLobbyId");

        // When
        controller.handleLobbyClosedEvent(event);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobbies/testLobbyId/close"),
                eq("Lobby Closed.")
        );
    }

    @Test
    public void testRemovePlayer() {
        // Given
        String lobbyId = "testLobbyId";
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        User user = new User(); // Mocked conversion result
        when(lobbyService.removePlayer(eq(lobbyId), any(User.class))).thenReturn(new ArrayList<>());

        // When
        controller.removePlayer(lobbyId, userTokenDTO);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobbies/testLobbyId/players"),
                any(List.class)
        );
    }

    @Test
    public void testKickPlayer() {
        // Given
        String lobbyId = "testLobbyId";
        String usernameToKick = "userToKick";
        UserTokenDTO hostTokenDTO = new UserTokenDTO();
        User host = new User(); // Mocked conversion result
        when(lobbyService.kickPlayer(eq(lobbyId), any(User.class), eq(usernameToKick))).thenReturn(new ArrayList<>());

        // When
        controller.kickPlayer(lobbyId, usernameToKick, hostTokenDTO);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/queue/lobbies/testLobbyId/kick/userToKick"),
                eq("player kicked")
        );
        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobbies/testLobbyId/players"),
                any(List.class)
        );
    }

    @Test
    public void testUpdateSettings() {
        // Given
        String lobbyId = "testLobbyId";
        GameSettings settings = new GameSettings();
        Player host = new Player(1l,"csdc","dscs", "#000000");
        when(lobbyService.getLobbyById(lobbyId)).thenReturn(new Lobby(host));

        // When
        controller.updateSettings(lobbyId, settings);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobbies/testLobbyId/settings"),
                any(GameSettingsDTO.class)
        );
    }
}

