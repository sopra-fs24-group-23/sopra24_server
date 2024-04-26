package ch.uzh.ifi.hase.soprafs24.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.events.GameStateChangeEvent;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

@SpringBootTest
public class GameServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private GameSettings settings;
    private List<Player> players;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        settings = new GameSettings();
        User user = new User();
        user.setToken("cwdc");
        user.setUsername("user1");
        user.setId(1L);
        User user2 = new User();
        user2.setToken("ec");
        user2.setUsername("user2");
        user2.setId(2L);
        Player player1 = new Player(user.getId(),user.getUsername(),user.getToken());
        Player player2 = new Player(user2.getId(),user2.getUsername(),user.getToken());
        players = List.of( player2,player1);
        Game game1 = new Game(settings,players);
        Lobby lobby = new Lobby(player1);
        lobby.setId("fvvfd");
        gameService.runGame(lobby.getId(),settings,players);
    }
    @Test
    public void testRunGame() {
        // Arrange
        settings = new GameSettings();
        User user = new User();
        user.setToken("cwdc");
        user.setUsername("user1");
        user.setId(1L);
        User user2 = new User();
        user2.setToken("ec");
        user2.setUsername("user2");
        user2.setId(2L);
        Player player1 = new Player(user.getId(),user.getUsername(),user.getToken());
        Player player2 = new Player(user2.getId(),user2.getUsername(),user.getToken());
        players = List.of( player2,player1);
        Game game1 = new Game(settings,players);
        Lobby lobby = new Lobby(player1);
        lobby.setId("fvvfd");
        gameService.runGame(lobby.getId(),settings,players);
        when(gameService.getGameById(lobby.getId())).thenReturn(game);
        when(gameService.getGameState(lobby.getId())).thenReturn(game.getState());

        // Act
        gameService.runGame(lobby.getId(), settings, players);

        // Assert
        verify(eventPublisher).publishEvent(any(GameStateChangeEvent.class));
    }

    @Test
    public void testCloseInputs() {
        // Setup
        settings = new GameSettings();
        User user = new User();
        user.setToken("cwdc");
        user.setUsername("user1");
        user.setId(1L);
        User user2 = new User();
        user2.setToken("ec");
        user2.setUsername("user2");
        user2.setId(2L);
        Player player1 = new Player(user.getId(),user.getUsername(),user.getToken());
        Player player2 = new Player(user2.getId(),user2.getUsername(),user.getToken());
        players = List.of( player2,player1);
        Game game1 = new Game(settings,players);
        Lobby lobby = new Lobby(player1);
        lobby.setId("fvvfd");
        gameService.runGame(lobby.getId(),settings,players);

        // Act
        gameService.closeInputs(lobby.getId());

        // Assert
        assertTrue(gameService.getGameById(lobby.getId()).getPlayerHasAnswered());
    }

}
