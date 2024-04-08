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
        Game newGame = new Game(lobby.getSettings(), lobby);
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
    }

    public void endGame(Long gameId) {
        Game game = games.get(gameId);
        Lobby lobby = lobbyService.getLobbyById(game.getLobby().getId());
        lobby.setIsGameRunning(false);
        // Additional logic to handle the end of the game
    }

    public void addPlayer(Long gameId, Player player) {
        Game game = games.get(gameId);
       // game.addPlayer(player);
    }

    public void removePlayer(Long gameId, Player player) {
        Game game = games.get(gameId);
      //  game.removePlayer(player);
    }
}