package ch.uzh.ifi.hase.soprafs24.controller;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyIdDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTokenDTO;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;

import java.util.ArrayList;
import java.util.List;

public class LobbyControllerTest {

    @Mock
    private LobbyService lobbyService;
    @InjectMocks
    private LobbyController lobbyController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLobby() {
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        userTokenDTO.setToken("userToken");

        User host = new User();
        host.setId(1L);
        Player player = new Player(host.getId(),host.getUsername(),host.getToken(), host.getColor());
        Lobby lobby = new Lobby(player, "lobbyId");

        when(lobbyService.createLobby(any(User.class))).thenReturn(lobby);

        LobbyIdDTO result = lobbyController.createLobby(userTokenDTO);

        assertNotNull(result);
        assertEquals("lobbyId", result.getId());
        verify(lobbyService).createLobby(any(User.class));
    }

    @Test
    void testDeleteLobby() {
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        userTokenDTO.setToken("userToken");

        doNothing().when(lobbyService).deleteLobby(anyString(), any(User.class));

        assertDoesNotThrow(() -> lobbyController.deleteLobby("lobbyId", userTokenDTO));

        verify(lobbyService).deleteLobby(eq("lobbyId"), any(User.class));
    }

    @Test
    void testCheckLobbyId() {
        doNothing().when(lobbyService).checkLobbyId(anyString());

        assertDoesNotThrow(() -> lobbyController.checkLobbyId("lobbyId"));

        verify(lobbyService).checkLobbyId("lobbyId");
    }

    @Test
    void testGetHostName() {

        User host = new User();
        host.setId(1L);
        Player player = new Player(host.getId(),host.getUsername(),host.getToken(), host.getColor());

        when(lobbyService.getHost(anyString())).thenReturn(player);

        PlayerGetDTO result = lobbyController.getHostName("lobbyId");

        assertNotNull(result);
        assertEquals(host.getUsername(), result.getUsername());
        verify(lobbyService).getHost("lobbyId");
    }

    @Test
    void testAddPlayer() {
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        userTokenDTO.setToken("userToken");

        User userToAdd = new User();
        userToAdd.setToken(userTokenDTO.getToken());

        List<Player> players = new ArrayList<>();
        when(lobbyService.addPlayer(anyString(), any(User.class))).thenReturn(players);

        lobbyController.addPlayer("lobbyId", userTokenDTO);

        ArgumentCaptor<User> userCaptor = forClass(User.class);
        verify(lobbyService).addPlayer(eq("lobbyId"), userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(userToAdd.getToken(), capturedUser.getToken());
    }
}
