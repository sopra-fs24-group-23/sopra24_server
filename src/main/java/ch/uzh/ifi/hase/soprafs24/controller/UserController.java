package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
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
    public UserGetDTO updateUser(@PathVariable Long userId, @RequestBody UserPutDTO userPutDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User updatedUser = userService.updateUser(userInput, userId);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
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
    public UserTokenDTO loginUser(@RequestBody UserPostDTO userPostDTO){
        // authenticate user
        User user = userService.loginUser(userPostDTO.getUsername(), userPostDTO.getPassword());
        // convert internal representation back to API
        return DTOMapper.INSTANCE.convertUserToUserTokenDTO(user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO logout(@RequestBody UserTokenDTO userTokenDTO){
        User userInput = DTOMapper.INSTANCE.convertUserTokenDTOtoEntity(userTokenDTO);
        User user = userService.logout(userInput.getToken());
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }
}

