package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long GameId;

    private String currentLetter;

    @Transient
    private Lobby lobby;

    @Transient
    private List<Player> players = new ArrayList<>();
    @Transient
    private GameSettings settings;

    // no-argument constructor (required by JPA)
    public Game() {
    }

    public Game(GameSettings settings, Lobby lobby) {
        this.settings = settings;
        this.lobby = lobby;
    }

    public void startGame() throws IllegalStateException {
        if (this.players.size() < 2 ) {
            throw new IllegalStateException("Cannot start game: Not enough players.");
        }
        if (this.lobby.getIsGameRunning()) {
            throw new IllegalStateException("Game is already running.");
        }
        // Start the game logic
        this.lobby.setIsGameRunning(true);
        // Initialize game rounds etc.
    }

    public void endGame() {

    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }
}
