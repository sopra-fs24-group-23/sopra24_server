package ch.uzh.ifi.hase.soprafs24.websocket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import ch.uzh.ifi.hase.soprafs24.categories.Category;
import ch.uzh.ifi.hase.soprafs24.controller.LobbyWebsocketController;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyStateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import java.util.Arrays;
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
    public void testAddPlayer() {
        Player host = new Player(1L, "dssdf", "eerferfe");
        Lobby lobby = new Lobby(host);
        String lobbyId = lobby.getId();
        lobby.setSettings(new GameSettings()); // Make sure settings are set to prevent NPE
        UserTokenDTO userTokenDTO = new UserTokenDTO(); // Set required fields
        User user = new User(); // Mocked conversion result
        when(lobbyService.addPlayer(eq(lobbyId), any(User.class))).thenReturn(new ArrayList<>());
        when(lobbyService.getLobbyById(lobbyId)).thenReturn(lobby); // Ensure this does not return null

        controller.addPlayer(lobbyId, userTokenDTO);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobbies/" + lobbyId + "/players"),
                any(List.class)
        );
        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobbies/" + lobbyId + "/settings"),
                any(GameSettingsDTO.class)
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
        Player host = new Player(1l,"csdc","dscs");
        when(lobbyService.getLobbyById(lobbyId)).thenReturn(new Lobby(host));

        // When
        controller.updateSettings(lobbyId, settings);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobbies/testLobbyId/settings"),
                any(GameSettingsDTO.class)
        );
    }
    @Test
    public void testJoinFullLobby() {
        String lobbyId = "fullLobbyId";
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        User user = new User(); // Mocked conversion result

        when(lobbyService.addPlayer(eq(lobbyId), any(User.class))).thenThrow(new RuntimeException("Lobby is full"));

        assertThrows(RuntimeException.class, () -> {
            controller.addPlayer(lobbyId, userTokenDTO);
        });

        verifyNoInteractions(messagingTemplate);
    }

    @Test
    public void testLeaveNonexistentUser() {
        String lobbyId = "testLobbyId";
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        User user = new User(); // Mocked conversion result

        when(lobbyService.removePlayer(eq(lobbyId), any(User.class))).thenThrow(new RuntimeException("User not found in lobby"));

        assertThrows(RuntimeException.class, () -> {
            controller.removePlayer(lobbyId, userTokenDTO);
        });

        verifyNoInteractions(messagingTemplate);
    }

    @Test
    public void testInvalidLobbyIdOnJoin() {
        String invalidLobbyId = "invalidLobbyId";
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        when(lobbyService.addPlayer(eq(invalidLobbyId), any(User.class)))
                .thenThrow(new NullPointerException());
        assertThrows(ResponseStatusException.class, () -> {
            controller.addPlayer(invalidLobbyId, userTokenDTO);
        });

        verifyNoInteractions(messagingTemplate);
    }

    @Test
    public void testUpdateLobbyState() {
        String lobbyId = "testLobbyId";
        controller.updateLobbyState(lobbyId, true, true);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobbies/" + lobbyId + "/settings"),
                any(LobbyStateDTO.class)
        );
    }
}

