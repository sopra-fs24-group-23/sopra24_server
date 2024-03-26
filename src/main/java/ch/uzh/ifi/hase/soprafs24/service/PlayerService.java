package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class PlayerService {

  private final Logger log = LoggerFactory.getLogger(PlayerService.class);

  private final PlayerRepository playerRepository;

  @Autowired
  public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  public List<Player> getUsers() {
    return this.playerRepository.findAll();
  }

  public Player createUser(Player newPlayer) {
    newPlayer.setToken(UUID.randomUUID().toString());
    newPlayer.setStatus(PlayerStatus.OFFLINE);
    checkIfUserExists(newPlayer);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newPlayer = playerRepository.save(newPlayer);
    playerRepository.flush();

    log.debug("Created Information for User: {}", newPlayer);
    return newPlayer;
  }
    public Player loginPlayer(String username, String password){
        Player loggedInUser = playerRepository.findByUsername(username);
        if (loggedInUser==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesnt exist");
        }
        if (!loggedInUser.getPassword().equals(password)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"incorrect password");
        }
        if (PlayerStatus.ONLINE.equals(loggedInUser.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already logged in");
        }
        loggedInUser.setStatus(PlayerStatus.ONLINE);
        loggedInUser.setToken(UUID.randomUUID().toString());
        playerRepository.flush();
        return loggedInUser;
    }
    public Player logout(String token){
        Player loggedOutUser = playerRepository.findByToken(token);
        System.out.println("token:"+ token);
        if (loggedOutUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with provided token does not exist");

        }
        loggedOutUser.setToken(null);
        loggedOutUser.setStatus(PlayerStatus.OFFLINE);
        loggedOutUser = playerRepository.save(loggedOutUser);
        playerRepository.flush();
        return loggedOutUser;
    }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param playerToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see Player
   */
  private void checkIfUserExists(Player playerToBeCreated) {
    Player playerByUsername = playerRepository.findByUsername(playerToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (playerByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username", "is"));
    }
  }
    public Player findById(Long userId){
        return this.playerRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"The user with the provided ID doesn't exist.")); }
}
