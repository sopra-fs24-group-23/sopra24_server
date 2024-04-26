package ch.uzh.ifi.hase.soprafs24.events;

import ch.uzh.ifi.hase.soprafs24.entity.GameSettings;
import org.springframework.context.ApplicationEvent;

public class SettingsUpdateEvent extends ApplicationEvent {
    private final String lobbyId;
    private final GameSettings settings;

    public SettingsUpdateEvent(Object source, String lobbyId, GameSettings settings) {
        super(source);
        this.lobbyId = lobbyId;
        this.settings = settings;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public GameSettings getSettings() {
        return settings;
    }
}
