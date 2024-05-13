package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.events.LobbyClosedEvent;
import ch.uzh.ifi.hase.soprafs24.exceptions.LobbyLockedException;
import ch.uzh.ifi.hase.soprafs24.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs24.exceptions.UnauthorizedException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTokenDTO;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import com.sun.xml.bind.v2.util.QNameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.Arrays;
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

        Player player = new Player(user.getId(),user.getUsername(),user.getToken(), user.getColor());
        player.setToken("ceffcvf");
        System.out.println("player username: " + player.getUsername() + " " + "playertoken: " + player.getToken());

        player.setIsHost(true);
        lobby = new Lobby(player, "testId");
        lobby.setHost(player);

    }

    @Test
    void getLobbyById_ExistingId_ReturnsLobby() {
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");
        System.out.println("username:" + user.getUsername() + " " + "token" + user.getToken());
        user.setToken("defaultToken");


        lobby = lobbyService.createLobby(user);

        assertEquals(lobby, lobbyService.getLobbyById(lobby.getId()));
    }

    @Test
    void getLobbyById_NonExistingId_ThrowsException() {
        assertThrows(ResponseStatusException.class, () -> lobbyService.getLobbyById("No lobby with the provided ID was found."));
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
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");
        user.setToken("defaultToken");
        Player player = new Player(user.getId(),user.getUsername(),user.getToken(), user.getColor());



        lobby = lobbyService.createLobby(user) ;
        player.setIsHost(true);
        lobby.setHost(player);
        String lobbyId = lobby.getId();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        lobbyService.deleteLobby(lobbyId, user);
        assertThrows(RuntimeException.class, () -> {
            lobbyService.deleteLobby(lobbyId, user);
        verify(eventPublisher).publishEvent(any(LobbyClosedEvent.class));});
    }


    @Test
    void deleteLobby_NotByHost_ThrowsException() {
        User nonHostUser = new User();
        assertThrows(ResponseStatusException.class, () -> lobbyService.deleteLobby(lobby.getId(), nonHostUser));
    }

    @Test
    void checkLobbyId_NonExistentId(){

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            lobbyService.checkLobbyId("nonExistentId");
        });

        assertTrue(exception.getMessage().contains("The lobby you are trying to join does not exist."));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void checkLobbyId_IdExists() {
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");

        when(userRepository.findByToken(user.getToken())).thenReturn(user);

        Lobby newLobby = lobbyService.createLobby(user);

        assertDoesNotThrow(() -> lobbyService.checkLobbyId(newLobby.getId()));
    }

    @Test
    void checkPlayerAddedToLobbySuccess() {
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");

        User userToAdd = new User();
        userToAdd.setUsername("user2");
        userToAdd.setToken("akakaka5");

        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        Lobby newLobby = lobbyService.createLobby(user);

        // find user corresponding to received token
        when(userRepository.findByToken(userToAdd.getToken())).thenReturn(userToAdd);

        assertDoesNotThrow(() -> lobbyService.addPlayer(newLobby.getId(), userToAdd));

    }

    @Test
    void checkPlayerAddedToLobbyFailed() {
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");

        User userToAdd = new User();
        userToAdd.setUsername("user2");
        userToAdd.setToken("akakaka5");

        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        Lobby newLobby = lobbyService.createLobby(user);

        // Create a GameSettings object and set maxPlayers to 1
        GameSettings settings = new GameSettings();
        settings.setMaxPlayers(1);

        newLobby.setSettings(settings);

        // find user corresponding to received token
        when(userRepository.findByToken(userToAdd.getToken())).thenReturn(userToAdd);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            lobbyService.addPlayer(newLobby.getId(), userToAdd);
        });

        assertTrue(exception.getMessage().contains("Sorry, the lobby you wanted to join is full or the game is already in progress."));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void checkRemovePlayerFromLobbySuccess(){
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");

        User userToAdd = new User();
        userToAdd.setUsername("user2");
        userToAdd.setToken("akakaka5");

        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        Lobby newLobby = lobbyService.createLobby(user);

        // find user corresponding to received token
        when(userRepository.findByToken(userToAdd.getToken())).thenReturn(userToAdd);

        // Add the player to the lobby
        lobbyService.addPlayer(newLobby.getId(), userToAdd);

        // Remove the player again and check that no exception is thrown
        assertDoesNotThrow(() -> lobbyService.removePlayer(newLobby.getId(), userToAdd));

    }

    @Test
    void checkRemovePlayerFromLobbyFailed(){
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");

        User notAddedUser = new User();
        notAddedUser.setUsername("user2");
        notAddedUser.setToken("abcdefg1");

        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        Lobby newLobby = lobbyService.createLobby(user);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            lobbyService.removePlayer(newLobby.getId(), notAddedUser);
        });

        assertTrue(exception.getMessage().contains("The player you tried to remove from the lobby was not found."));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void checkKickPlayerFromLobbySuccess(){
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");

        User userToKick = new User();
        userToKick.setUsername("user2");
        userToKick.setToken("akakaka5");

        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        Lobby newLobby = lobbyService.createLobby(user);

        // find user corresponding to received token
        when(userRepository.findByToken(userToKick.getToken())).thenReturn(userToKick);

        // Add the player to the lobby
        lobbyService.addPlayer(newLobby.getId(), userToKick);

        assertDoesNotThrow(() -> lobbyService.kickPlayer(newLobby.getId(), user, userToKick.getUsername()));
    }

    @Test
    void checkKickPlayerFromLobbyFailed(){
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");

        User unauthorizedUser = new User();
        unauthorizedUser.setUsername("user2");
        unauthorizedUser.setToken("akakaka5");

        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        Lobby newLobby = lobbyService.createLobby(user);

        // find user corresponding to received token
        when(userRepository.findByToken(unauthorizedUser.getToken())).thenReturn(unauthorizedUser);

        // Add the player to the lobby
        lobbyService.addPlayer(newLobby.getId(), unauthorizedUser);

        // The unauthorized user tries to kick the host
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            lobbyService.kickPlayer(newLobby.getId(), unauthorizedUser, user.getUsername());
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());

    }

    @Test
    void testRemoveHostDeletesLobby(){
        user = new User();
        user.setUsername("user1");
        user.setToken("ceffcvf");

        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        Lobby newLobby = lobbyService.createLobby(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        lobbyService.removePlayer(newLobby.getId(), user);

        assertFalse(lobbyService.getLobbies().containsKey(newLobby.getId()));
    }

}
