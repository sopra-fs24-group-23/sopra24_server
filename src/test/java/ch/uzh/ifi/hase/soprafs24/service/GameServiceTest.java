package ch.uzh.ifi.hase.soprafs24.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs24.categories.Category;
import ch.uzh.ifi.hase.soprafs24.categories.City;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.events.GameStateChangeEvent;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class GameServiceTest {

    @Qualifier("userRepository")
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GameService gameService;
    private GameSettings settings;
    private List<Player> players;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        List<Category> categories = new ArrayList<>();
        categories.add(new City());

        settings = new GameSettings();
        settings.setMaxPlayers(2);
        settings.setMaxRounds(2);
        settings.setCategories(categories);
        settings.setInputDuration(1);
        settings.setVotingDuration(1);
        settings.setScoreboardDuration(1);

        Player playerOne = new Player(1L, "Player1", "ABCD");
        Player playerTwo = new Player(2L, "Player2", "EFGH");
        players = new ArrayList<>();
        players.add(playerOne);
        players.add(playerTwo);
    }

    @Test
    public void testCloseInputs() {
        String gameId = "ABCD";

        gameService.runGame(gameId, settings, players);

        gameService.closeInputs(gameId);

        assertTrue(gameService.getGameById(gameId).getPlayerHasAnswered());
    }

    @Test
    public void testUpdateStatistics() {
        User mockUser1 = new User();
        mockUser1.setGamesPlayed(1);
        mockUser1.setGamesWon(2);
        mockUser1.setTotalScore(100);

        User mockUser2 = new User();
        mockUser2.setGamesPlayed(5);
        mockUser2.setGamesWon(6);
        mockUser2.setTotalScore(300);

        players.get(0).setCurrentScore(150);
        players.get(1).setCurrentScore(300);

        String gameId = "ABCD";
        Game game = mock(Game.class);

        Mockito.when(userRepository.findByUsername("Player1")).thenReturn(mockUser1);
        Mockito.when(userRepository.findByUsername("Player2")).thenReturn(mockUser2);

        Mockito.when(game.getPlayers()).thenReturn(players);

        gameService.endGameLoop(gameId, game);

        assertEquals(2, mockUser1.getGamesPlayed());
        assertEquals(2, mockUser1.getGamesWon());
        assertEquals(250, mockUser1.getTotalScore());

        assertEquals(6, mockUser2.getGamesPlayed());
        assertEquals(7, mockUser2.getGamesWon());
        assertEquals(600, mockUser2.getTotalScore());
    }

}
