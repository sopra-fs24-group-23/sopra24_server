package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashMap;

@Service
@Transactional
public class GameService {

    private HashMap<Long, Game> games = new HashMap<>();
    private final LobbyService lobbyService;

    public GameService(LobbyService lobbyService) {
        this.lobbyService = lobbyService;

    }

    public Game createGame(Lobby lobby) {
        Game newGame = new Game(lobby);
        games.put(newGame.getGameId(), newGame);
        return newGame;
    }


    public void startGame(Long gameId) {
        Game game = games.get(gameId);
        if (game.getPlayers().size() < 2) {
            throw new IllegalStateException("Cannot start game: Not enough players.");
        }
        Lobby lobby = lobbyService.getLobbyById(game.getLobby().getId());
        if (lobby.getIsGameRunning()) {
            throw new IllegalStateException("Game is already running.");
        }
        lobby.setIsGameRunning(true);
        // Initialize game rounds etc.
        game.startNextRound();
    }

    public void advanceRound(Long gameId) {
        Game game = games.get(gameId);
        boolean roundStarted = game.startNextRound();
        if (!roundStarted) {
            // Handle end-of game scenario
            endGame(gameId);
        }
        else {
            // Reset or update round-specific data
        //resetRoundData(game);

        // Notify players about the new round
        //notifyPlayers(gameId, "Round " + game.getCurrentRound() + " is starting!");

        // Execute any additional round initialization logic
        //initializeNewRound(game);
        }
    }

    private void resetRoundData(Game game) {

    }

    private void notifyPlayers(Long gameId, String message) {
        // Example: Send a message to all players in the game
        // This could involve updating a UI element, sending a push notification, etc.
    }

    private void initializeNewRound(Game game) {
        // set round timers, etc.
    }

    public void endGame(Long gameId) {
        Game game = games.get(gameId);
        Lobby lobby = lobbyService.getLobbyById(game.getLobby().getId());
        lobby.setIsGameRunning(false);
        // Additional logic to handle the end of the game
    }

    public void removePlayer(Long gameId, Player player) {
        Game game = games.get(gameId);
        game.removePlayer(player);
    }
}