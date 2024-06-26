package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserAuthenticationDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(ch.uzh.ifi.hase.soprafs24.controller.UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setId(1L);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].id", is(user.getId().intValue())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setPassword("testPassword");
    user.setToken("1");

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("testPassowrd");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.gamesPlayed", is(user.getGamesPlayed())))
        .andExpect(jsonPath("$.gamesWon", is (user.getGamesWon())))
        .andExpect(jsonPath("$.totalScore", is (user.getTotalScore())));
  }

  @Test
  public void loginUser_validInput_userLoggedIn() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setPassword("testPassword");
      user.setToken("1");

      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setUsername("testUsername");
      userPostDTO.setPassword("testPassword");

      given(userService.loginUser(userPostDTO.getUsername(), userPostDTO.getPassword())).willReturn(user);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(postRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.username", is(user.getUsername())));
  }

    @Test
    public void getUser_validInput_userRetrieved() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");

        given(userService.getUser(user.getId(), user.getToken())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId())
                .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }


    @Test
    public void updateUser_validInput_userUpdated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("updatedUsername");
        user.setPassword("updatedPassword");
        user.setToken("1");

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("updatedUsername");

        given(userService.updateUser(Mockito.any(), Mockito.anyLong())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }
  @Test
  public void logoutUser_validInput_userLoggedOut() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setPassword("testPassword");
      user.setToken("1");

      UserAuthenticationDTO userAuthenticationDTO = new UserAuthenticationDTO();
      userAuthenticationDTO.setToken("1");

      given(userService.logout(Mockito.anyString())).willReturn(user);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/logout")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userAuthenticationDTO));

      // then
      mockMvc.perform(postRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.username", is(user.getUsername())));
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}