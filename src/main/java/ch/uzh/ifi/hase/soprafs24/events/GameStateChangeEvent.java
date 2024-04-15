package ch.uzh.ifi.hase.soprafs24.events;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.entity.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class GameStateChangeEvent extends ApplicationEvent {
    private final GameState gameState;
    private final String gameId;

    public GameStateChangeEvent(Object source, GameState gameState, String gameId) {
        super(source);
        this.gameState = gameState;
        this.gameId = gameId;
    }

    public GameState getGameState() {
        return gameState;
    }

    public String getGameId() {
        return this.gameId;
    }
}
