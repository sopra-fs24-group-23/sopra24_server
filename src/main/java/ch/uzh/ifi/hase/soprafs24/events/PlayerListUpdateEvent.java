package ch.uzh.ifi.hase.soprafs24.events;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class PlayerListUpdateEvent extends ApplicationEvent {

    private final String lobbyId;
    private final List<Player> playerList;

    public PlayerListUpdateEvent(Object source, String lobbyId, List<Player> playerList) {
        super(source);
        this.playerList = playerList;
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }
}
