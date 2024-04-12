package ch.uzh.ifi.hase.soprafs24.service;

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
import java.util.List;

@Service
@Transactional
public class LobbyService {

    private final UserRepository userRepository;
    private HashMap<String, Lobby> lobbies;

    public LobbyService(
            @Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
        this.lobbies = new HashMap<String, Lobby>();
    }
    public Lobby getLobbyById(String lobbyId) {
        return lobbies.get(lobbyId);
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

    public void deleteLobby(String lobbyId, User userToken) {
        Lobby lobby = this.lobbies.get(lobbyId);
        User host = userRepository.findByUsername(lobby.getHost().getUsername());

        // if request was made by host
        if (host.getToken().equals(userToken.getToken())) {
            // delete lobby
            lobbies.remove(lobbyId);
        }
    }

    public List<Player> addPlayer(String lobbyId, User userToAdd) {
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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The lobby you are trying to join is full.");
        }

        return lobby.getPlayers();
    }

    public List<Player> removePlayer(String lobbyId, User userToRemove) {
        Lobby lobby = getLobbyById(lobbyId);

        if (lobby != null) {
            lobby.removePlayer(userToRemove.getUsername());
        } else {
            throw new IllegalArgumentException("Lobby not found");
        }

        return lobby.getPlayers();
    }
    public void leaveLobby(String lobbyId, User user) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found.");
        }

        // Check if the user is part of the lobby
        boolean isPlayerInLobby = lobby.getPlayers().stream()
                .anyMatch(player -> player.getId().equals(user.getId()));
        if (!isPlayerInLobby) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not part of this lobby.");
        }

        // Remove player from the lobby
        lobby.removePlayer(user.getUsername());
    }


}
