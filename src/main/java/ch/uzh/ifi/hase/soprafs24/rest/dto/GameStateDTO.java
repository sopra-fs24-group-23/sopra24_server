package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GamePhase;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import java.util.List;

public class GameStateDTO {
    private GamePhase gamePhase;
    private List<PlayerGetDTO> players;

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
    }

    public List<PlayerGetDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerGetDTO> players) {
        this.players = players;
    }
}
