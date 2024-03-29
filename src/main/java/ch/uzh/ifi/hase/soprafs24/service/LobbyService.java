package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.controller.LobbyWebsocketController;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class LobbyService {

    private final UserRepository userRepository;
    private final LobbyWebsocketController lobbyWsController;
    private HashMap<Long, Lobby> lobbies;

    public LobbyService(
            @Qualifier("userRepository") UserRepository userRepository,
            LobbyWebsocketController lobbyWsController
    ) {
        this.userRepository = userRepository;
        this.lobbies = new HashMap<Long, Lobby>();
        this.lobbyWsController = lobbyWsController;
    }

    public void addPlayer(Long lobbyId, User userToAdd) {
        // retrieve lobby from Service hashmap
        Lobby lobby = lobbies.get(lobbyId);

        // find user corresponding to received token
        User user = userRepository.findByToken(userToAdd.getToken());

        // create new player object from user and add to the lobby
        Player player = new Player(user.getId(), user.getUsername());
        lobby.addPlayer(player);

        // update clients via websocket
        lobbyWsController.updatePlayerList(lobbyId, lobby.getPlayers());
    }
}
