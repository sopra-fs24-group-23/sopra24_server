package ch.uzh.ifi.hase.soprafs24.events;

import ch.uzh.ifi.hase.soprafs24.entity.GameState;
import org.springframework.context.ApplicationEvent;

public class LobbyClosedEvent extends ApplicationEvent {
    private final String lobbyId;

    public LobbyClosedEvent(Object source, String lobbyId) {
        super(source);
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}
