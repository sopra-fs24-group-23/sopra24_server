package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ColorRequirement;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
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
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        checkIfUsernameTaken(newUser);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser.setStatus(UserStatus.ONLINE);
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User updateUser(User userInput, Long userId) {
        User persistedUser = findById(userId);

        // Check if the username is empty
        if (userInput.getUsername() == null || userInput.getUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The username cannot be empty. Please try again.");
        }
        // check if input color is empty
        else if (userInput.getColor() == null || userInput.getColor().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The color attribute cannot be empty. Please provide a valid value.");
        }

        // Check if username is already taken ONLY, if user didn't input their current username
        if (!userInput.getUsername().equals(persistedUser.getUsername())) {
            checkIfUsernameTaken(userInput);
        }

        // Check if user has required score to set color
        String colorCode = userInput.getColor();
        Integer totalScore = persistedUser.getTotalScore();
        try {
            ColorRequirement requirement = ColorRequirement.getByColorCode(colorCode);
            if (totalScore < requirement.getScoreRequirement()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The user does not have the total score required for this color.");
            }
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The provided color code is invalid.");
        }

        // update attributes
        persistedUser.setColor(userInput.getColor());
        persistedUser.setUsername(userInput.getUsername());

        // save to DB
        userRepository.saveAndFlush(persistedUser);
        return persistedUser;
    }

    public User loginUser(String username, String password) {
        User persistedUser = userRepository.findByUsername(username);
        checkIfUserExists(persistedUser);

        if (persistedUser.getStatus() == UserStatus.ONLINE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already logged in from another session.");
        }

        if (!persistedUser.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password.");
        }
        persistedUser.setToken(UUID.randomUUID().toString()); // set new session token
        persistedUser.setStatus(UserStatus.ONLINE);
        userRepository.saveAndFlush(persistedUser);

        return persistedUser;
    }

    public User logout(String token) {
        User persistedUser = userRepository.findByToken(token);
        System.out.println("token:" + token);

        if (persistedUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User you were trying to log-out does not exist.");
        }

        persistedUser.setToken(null);
        persistedUser.setStatus(UserStatus.OFFLINE);
        userRepository.saveAndFlush(persistedUser);

        return persistedUser;
    }

    public List<User> getGamesPlayedRanking(Long range) {
        List<User> users = userRepository.findAll();

        // sort by games played
        users.sort((u1, u2) -> u2.getGamesPlayed().compareTo(u1.getGamesPlayed()));

        return users.subList(0, Math.min(users.size(), range.intValue()));
    }

    public List<User> getTotalScoreRanking(Long range) {
        List<User> users = userRepository.findAll();

        users.sort((u1, u2) -> u2.getTotalScore().compareTo(u1.getTotalScore()));

        return users.subList(0, Math.min(users.size(), range.intValue()));
    }

    public List<User> getGamesWonRanking(Long range) {
        List<User> users = userRepository.findAll();

        users.sort((u1, u2) -> u2.getGamesWon().compareTo(u1.getGamesWon()));

        return users.subList(0, Math.min(users.size(), range.intValue()));
    }



    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see Player
     */
    private void checkIfUsernameTaken(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage, "username", "is"));
        }
    }

    private void checkIfUserExists(User user) {
        User userByUsername = userRepository.findByUsername(user.getUsername());

        if (userByUsername == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested user was not found.");
        }
    }

    public User findById(Long userId) {
        return this.userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The user with the provided ID doesn't exist.")
        );
    }

    public User getUser(Long userId, String token) {
        User user = findById(userId);
        if(user.getToken().equals(token)) {
            return user;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token mismatch.");
    }
}