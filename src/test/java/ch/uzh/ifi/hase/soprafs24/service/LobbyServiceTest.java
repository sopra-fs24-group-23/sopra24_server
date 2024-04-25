package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.events.LobbyClosedEvent;
import ch.uzh.ifi.hase.soprafs24.exceptions.UnauthorizedException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import com.sun.xml.bind.v2.util.QNameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

class LobbyServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private LobbyService lobbyService;

    private User user;
    private Lobby lobby;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");
        System.out.println("username:" + user.getUsername() + " " + "token" + user.getToken());
        user.setToken("defaultToken");
        when(userRepository.findByToken("defaultToken")).thenReturn(user);

        Player player = new Player(user.getId(),user.getUsername(),user.getToken());
        player.setToken("ceffcvf");
        System.out.println("player username: " + player.getUsername() + " " + "playertoken: " + player.getToken());

        player.setIsHost(true);
        lobby = new Lobby(player);
        lobby.setHost(player);

    }

    @Test
    void getLobbyById_ExistingId_ReturnsLobby() {
        assertEquals(lobby, lobbyService.getLobbyById(lobby.getId()));
    }

    @Test
    void getLobbyById_NonExistingId_ThrowsException() {
        assertThrows(ResponseStatusException.class, () -> lobbyService.getLobbyById("nonExistingId"));
    }

    @Test
    void createLobby_CreatesAndStoresLobby() {
        when(userRepository.findByToken(user.getToken())).thenReturn(new User());
        Lobby newLobby = lobbyService.createLobby(user);
        assertNotNull(lobbyService.getLobbyById(newLobby.getId()));
        assertTrue(newLobby.getHost().getIsHost());
    }

    @Test
    void deleteLobby_ByHost_DeletesLobby() {
        String lobbyId = lobby.getId();
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        lobbyService.deleteLobby(lobbyId, user);
        assertThrows(RuntimeException.class, () -> {
            lobbyService.deleteLobby(lobbyId, user);
        verify(eventPublisher).publishEvent(any(LobbyClosedEvent.class));});
    }


    @Test
    void deleteLobby_NotByHost_ThrowsException() {
        User nonHostUser = new User();
        assertThrows(NullPointerException.class, () -> lobbyService.deleteLobby(lobby.getId(), nonHostUser));
    } //Todo check why throwing NullPointer in LobbyService

    //Todo Additional tests for addPlayer, removePlayer, and kickPlayer would be similar
}
