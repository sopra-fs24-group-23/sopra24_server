package ch.uzh.ifi.hase.soprafs24.events;

import org.springframework.context.ApplicationEvent;

public class LobbyStateChangeEvent extends ApplicationEvent {
    private boolean isGameRunning;
    private boolean isLobbyFull;
    public LobbyStateChangeEvent(Object source, boolean isGameRunning, boolean isLobbyFull) {
        super(source);
        this.isGameRunning = isGameRunning;
        this.isLobbyFull = isLobbyFull;
    }
}
