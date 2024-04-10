package ch.uzh.ifi.hase.soprafs24.service;

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
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User updateUser(User userInput, Long userId) {
        User persistedUser = findById(userId);

        // Check if the username is empty
        if (userInput.getUsername() == null || userInput.getUsername().trim().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The username cannot be empty. Please try again.");
        }

        // Check if username is already
        User userByUsername = userRepository.findByUsername(userInput.getUsername());
        if(userByUsername != null && !userByUsername.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This username is already taken. Please choose a different username.");
        }

        // update attributes
        persistedUser.setUsername(userInput.getUsername());
        // save to DB
        userRepository.saveAndFlush(persistedUser);
        return persistedUser;
    }
    public User loginUser(String username, String password){
        User persistedUser = userRepository.findByUsername(username);
        checkIfUserExists(persistedUser);

        if (!persistedUser.getPassword().equals(password)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Incorrect password.");
        }
        persistedUser.setToken(UUID.randomUUID().toString()); // set new session token
        userRepository.saveAndFlush(persistedUser);

        return persistedUser;
    }

    public User logout(String token){
        User persistedUser = userRepository.findByToken(token);
        System.out.println("token:" + token);

        if (persistedUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User you were trying to log-out does not exist.");
        }

        persistedUser.setToken(null);

        userRepository.saveAndFlush(persistedUser);

        return persistedUser;
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
    public User findById(Long userId){
        return this.userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"The user with the provided ID doesn't exist.")
        );
    }
}
