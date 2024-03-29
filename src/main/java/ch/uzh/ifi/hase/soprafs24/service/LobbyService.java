package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.controller.LobbyWebsocketController;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.exceptions.LobbyFullException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashMap;

@Service
@Transactional
public class LobbyService {

    private final UserRepository userRepository;
    private final LobbyWebsocketController lobbyWsController;
    private HashMap<String, Lobby> lobbies;

    public LobbyService(
            @Qualifier("userRepository") UserRepository userRepository,
            LobbyWebsocketController lobbyWsController
    ) {
        this.userRepository = userRepository;
        this.lobbies = new HashMap<String, Lobby>();
        this.lobbyWsController = lobbyWsController;
    }

    public Lobby createLobby(User hostToken) {
        // fetch host from DB and initialize player object
        User host = userRepository.findByToken(hostToken.getToken());
        Player hostPlayer = new Player(host.getId(), host.getUsername());
        // create new lobby, store to lobby list
        Lobby newLobby = new Lobby(hostPlayer);
        this.lobbies.put(newLobby.getId(), newLobby);
        return newLobby;
    }

    public void addPlayer(String lobbyId, User userToAdd) {
        // retrieve lobby from Service hashmap
        Lobby lobby = lobbies.get(lobbyId);

        // find user corresponding to received token
        User user = userRepository.findByToken(userToAdd.getToken());

        // create new player object from user and add to the lobby
        Player player = new Player(user.getId(), user.getUsername());

        try {
            lobby.addPlayer(player);
        }
        catch (LobbyFullException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The lobby are trying to join is full.");
        }

        // update clients via websocket
        lobbyWsController.updatePlayerList(lobbyId, lobby.getPlayers());
    }
}
