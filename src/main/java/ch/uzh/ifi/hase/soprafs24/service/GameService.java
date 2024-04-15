package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class GameService {

    @Autowired
    private GameSettingsService gameSettingsService;

    private Game currentGame;

    private HashMap<Long, Game> games = new HashMap<>();
    private final LobbyService lobbyService;

    public GameService(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    public Game getGameById(Long GameId) {
        return games.get(GameId);
    }

    public Map<Long, Game> getGames() {
        return games;
    }

    public Game createGame(Lobby lobby) {
        Game newGame = new Game(lobby);
        games.put(newGame.getGameId(), newGame);
        return newGame;
    }

    public void setGame(Game game) {
        this.currentGame = game;
    }


    public void startGame(Long gameId) {
        Game game = games.get(gameId);
        Lobby lobby = lobbyService.getLobbyById(game.getLobby().getId());

        if (lobby.getPlayers().size() < 2) {
            throw new IllegalStateException("Cannot start game: Not enough players.");
        }
        if (lobby.getIsGameRunning()) {
            throw new IllegalStateException("Game is already running.");
        }
        lobby.setIsGameRunning(true);
        // Initialize game rounds etc.
        game.startNextRound();
    }

    // This method checks if the timer of maxRoundsDuration has ended
    @Scheduled(fixedRate = 1000) // checks every second (1000 miliseconds)
    public void checkRoundEnd() {
        if (currentGame != null) {
            // Get the current game settings
            GameSettings settings = currentGame.getSettings();

            // Check if the round duration has exceeded the maximum
            if (settings.getMaxRoundsDuration() <= 0) {
                // End the round
                endRound();
            }
            else {
                // Decrease the round duration
                settings.setMaxRoundsDuration(settings.getMaxRoundsDuration() - 1);
                GameSettingsDTO settingsDTO = gameSettingsService.convertEntityToDTO(settings);
                // Get the lobbyId from the current game
                String lobbyId = currentGame.getLobby().getId();
                gameSettingsService.updateSettings(lobbyId, settingsDTO);
            }
        }
    }

    public void endRound() {
        if (currentGame != null) {
            // End the round
            currentGame.getRounds()[currentGame.getCurrentRound()].endRound();

            // Check if there are more rounds
            if (currentGame.hasMoreRounds()) {
                // Start the next round
                currentGame.startNextRound();
            }
            else {
                // End the game
                endGame(currentGame.getGameId());
            }
        }
    }

    private void resetRoundData(Game game) {

    }

    private void notifyPlayers(Long gameId, String message) {
        // Send a message to all players in the game
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