package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.events.LobbyClosedEvent;
import ch.uzh.ifi.hase.soprafs24.events.PlayerListUpdateEvent;
import ch.uzh.ifi.hase.soprafs24.events.SettingsUpdateEvent;
import ch.uzh.ifi.hase.soprafs24.exceptions.LobbyLockedException;
import ch.uzh.ifi.hase.soprafs24.exceptions.UnauthorizedException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class LobbyService {

    private final UserRepository userRepository;
    private HashMap<String, Lobby> lobbies;
    private final ApplicationEventPublisher eventPublisher;
    private final Random random;

    public LobbyService(
            @Qualifier("userRepository") UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.userRepository = userRepository;
        this.lobbies = new HashMap<String, Lobby>();
        this.random = new Random();
    }

    public Lobby getLobbyById(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No lobby with the provided ID was found.");
        }
        return lobbies.get(lobbyId);
    }

    public Lobby createLobby(User hostToken) {
        // fetch host from DB and initialize player object
        User host = userRepository.findByToken(hostToken.getToken());
        Player hostPlayer = new Player(host.getId(), host.getUsername(), host.getToken(), host.getColor());
        hostPlayer.setIsHost(true);
        // create new lobby, store to lobby list
        Lobby newLobby = new Lobby(hostPlayer, generateId());
        this.lobbies.put(newLobby.getId(), newLobby);
        return newLobby;
    }

    public void deleteLobby(String lobbyId, User userToken) {
        Lobby lobby = this.lobbies.get(lobbyId);
        if (lobbies.get(lobbyId) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
        User host = userRepository.findByUsername(lobby.getHost().getUsername());


        if (host.getToken().equals(userToken.getToken())) {
            // delete lobby
            lobbies.remove(lobbyId);
            eventPublisher.publishEvent(new LobbyClosedEvent(this, lobbyId));
        }
    }

    public void updateClients(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        eventPublisher.publishEvent(new PlayerListUpdateEvent(this, lobbyId, lobby.getPlayers()));
        eventPublisher.publishEvent(new SettingsUpdateEvent(this, lobbyId, lobby.getSettings()));
    }

    // throws 404 if lobbyId is invalid, else do nothing.
    public void checkLobbyId(String lobbyId) {
        Lobby lobby = this.lobbies.get(lobbyId);

        if (lobby == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The lobby you are trying to join does not exist."
            );
        }
    }

    public List<Player> addPlayer(String lobbyId, User userToAdd) {
        // retrieve lobby from Service hashmap
        Lobby lobby = lobbies.get(lobbyId);
        
        // find user corresponding to received token
        User user = userRepository.findByToken(userToAdd.getToken());

        // create new player object from user and add to the lobby
        Player player = new Player(user.getId(), user.getUsername(), user.getToken(), user.getColor());

        try {
            lobby.addPlayer(player);
            eventPublisher.publishEvent(new PlayerListUpdateEvent(this, lobbyId, lobby.getPlayers()));
            eventPublisher.publishEvent(new SettingsUpdateEvent(this, lobbyId, lobby.getSettings()));
        }
        catch (LobbyLockedException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sorry, the lobby you wanted to join is full or the game is already in progress.");
        }

        return lobby.getPlayers();
    }

    public List<Player> removePlayer(String lobbyId, User userToRemove) {

        Lobby lobby = getLobbyById(lobbyId);
        Player host = lobby.getHost();

        // if the leaving player is the host: delete lobby
        if (userToRemove.getToken().equals(host.getToken())){
            this.deleteLobby(lobbyId, userToRemove);
        }

        // if the leaving player is not the host: just remove them
        else {
            Player removedPlayer = lobby.removePlayer(userToRemove.getToken());

            if (removedPlayer == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The player you tried to remove from the lobby was not found."
                );
            }
        }

        // delete lobby if empty
        if (lobby.getPlayers().isEmpty()) {
            lobbies.remove(lobbyId);
        }

        return lobby.getPlayers();
    }

    public List<Player> kickPlayer(String lobbyId, User host, String usernameToKick) {
        Lobby lobby = getLobbyById(lobbyId);

        try {
            lobby.kickPlayer(host.getToken(), usernameToKick);
        }
        catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("%s", e.getMessage()));
        }

        return lobby.getPlayers();
    }

    public Player getHost(String lobbyId) {
        Lobby lobby = getLobbyById(lobbyId);
        return lobby.getHost();
    }

    /* Getter for testing */
    public HashMap<String, Lobby> getLobbies() {
        return this.lobbies;
    }

    /* Helpers */
    private String generateId() {
        // try random 10 times
        for (int j = 0; j < 10; j++) {
            String id = getRandomString(5, 65, 90);
            if (!lobbies.containsKey(id)) {
                return id;
            }
        }

        // start increasing length until string is unique
        int length = 5;
        while (true) {
            String id = getRandomString(length, 65, 90);
            if (!lobbies.containsKey(id)) {
                return id;
            }
            else {
                length++;
            }
        }
    }

    private String getRandomString(int length, int rangeStart, int rangeEnd) {
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomInt = random.nextInt(rangeEnd - rangeStart); // 0-25 = 26 ints
            char c = (char) (rangeStart + randomInt);
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }
}
