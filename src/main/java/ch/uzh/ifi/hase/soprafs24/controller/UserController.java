package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOS;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        // create user
        User createdUser = userService.createUser(userInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserAuthenticationDTO updateUser(@PathVariable Long userId, @RequestBody UserPutDTO userPutDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User updatedUser = userService.updateUser(userInput, userId);
        return DTOMapper.INSTANCE.convertEntityToUserAuthenticationDTO(updatedUser);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserGetDTO getUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO){
        // authenticate user
        User user = userService.loginUser(userPostDTO.getUsername(), userPostDTO.getPassword());
        // convert internal representation back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO logout(@RequestBody UserAuthenticationDTO userAuthenticationDTO){
        User userInput = DTOMapper.INSTANCE.convertUserAuthenticationDTOtoEntity(userAuthenticationDTO);
        User user = userService.logout(userInput.getToken());
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }
    @PostMapping("/users/{userId}/leaveLobby/{lobbyId}") //not too sure if this is how we defined the endpoint in m2
    public ResponseEntity<LeaveLobbyResponseDTO> leaveLobby(@PathVariable Long userId, @PathVariable String lobbyId) {
        try {
            userService.userLeaveLobby(userId, lobbyId);
            return ResponseEntity.ok(new LeaveLobbyResponseDTO("User successfully left the lobby."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LeaveLobbyResponseDTO("Error: " + e.getMessage()));
        }
    }


}

