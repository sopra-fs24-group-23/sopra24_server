package ch.uzh.ifi.hase.soprafs24.events;

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
