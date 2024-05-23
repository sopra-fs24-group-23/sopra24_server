package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ColorRequirement;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("testPassword");
    testUser.setTotalScore(100);

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
  }

    @Test
    public void getUsers_returnsAllUsers() {
        // given
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testUsername1");
        user1.setPassword("testPassword1");
        user1.setToken("1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testUsername2");
        user2.setPassword("testPassword2");
        user2.setToken("2");

        List<User> expectedUsers = Arrays.asList(user1, user2);

        Mockito.when(userRepository.findAll()).thenReturn(expectedUsers);

        // when
        List<User> actualUsers = userService.getUsers();

        // then
        assertEquals(expectedUsers, actualUsers);
    }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertNotNull(createdUser.getToken());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }
    @Test
    public void updateUser_updatesUserWithValidInfo_success() {
        // Setup existing user in the database
        Mockito.when(userRepository.findByUsername("newUsername")).thenReturn(null); // Ensure no user with new username exists

        // New input for the user
        User updatedInput = new User();
        updatedInput.setUsername("newUsername");
        updatedInput.setPassword("newPassword");

        // Execute the update
        User updatedUser = userService.updateUser(updatedInput, testUser.getId());

        // Verify that saveAndFlush is called instead of save
        Mockito.verify(userRepository).saveAndFlush(testUser);
        assertEquals("newUsername", updatedUser.getUsername());
    }


    @Test
    public void updateUser_usernameAlreadyExists_throwsException() {
        // Simulate that the new username is already taken
        User newUserWithSameUsername = new User();
        newUserWithSameUsername.setId(2L); // Ensure this user is different by giving a different ID
        Mockito.when(userRepository.findByUsername("newUsername")).thenReturn(newUserWithSameUsername);

        // New input for the user
        User updatedInput = new User();
        updatedInput.setUsername("newUsername");

        // Additional check to ensure values are not null before method call
        assertNotNull(userRepository.findByUsername("newUsername"), "Mocked findByUsername returned null");
        assertNotNull(userRepository.findById(testUser.getId()), "Mocked findById returned null");

        // Execute and verify that exception is thrown
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(updatedInput, testUser.getId()));
    }

    @Test
    public void updateUser_inputOwnUsername_passes() {
        User userInput = new User();
        userInput.setColor("#000000");
        userInput.setUsername("testUser");

        assertDoesNotThrow(() -> userService.updateUser(userInput, testUser.getId()));
    }

    @Test
    public void updateUser_invalidColorCode_throwsBadRequest() {
      User userInput = new User();
      userInput.setColor("#NOTACOLORCODE_12345");
      userInput.setUsername("newUsername");

      ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUser(userInput, testUser.getId()));
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void updateUser_wrongColorCode_throwsBadRequest() {
        User userInput = new User();
        userInput.setColor("#FFFFFF"); // white color
        userInput.setUsername("newUsername");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUser(userInput, testUser.getId()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void updateUser_emptyColor_throwsBadRequest() {
        User userInput = new User();
        userInput.setColor(""); // white color
        userInput.setUsername("newUsername");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUser(userInput, testUser.getId()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void updateUser_insufficientTotalScore_throwsUnauthorized() {
        User userInput = new User();
        userInput.setColor(ColorRequirement.PURPLE.getColorCode()); // required score: 5000
        userInput.setUsername("newUsername");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUser(userInput, testUser.getId()));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    public void loginUser_withCorrectCredentials_returnsUser() {
        // Setup
        Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

        // Action
        User loggedInUser = userService.loginUser("testUsername", "testPassword");

        // Assert
        assertNotNull(loggedInUser.getToken());
        assertEquals("testUsername", loggedInUser.getUsername());
    }

    @Test
    public void loginUser_withIncorrectPassword_throwsException() {
        // Setup
        Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

        // Action & Assert
        assertThrows(ResponseStatusException.class, () -> userService.loginUser("testUsername", "wrongPassword"));
    }
    @Test
    public void logout_validToken_userLoggedOut() {
        // Setup
        testUser.setToken("someToken");
        Mockito.when(userRepository.findByToken("someToken")).thenReturn(testUser);

        // Action
        User loggedOutUser = userService.logout("someToken");

        // Assert
        assertNull(loggedOutUser.getToken());
        Mockito.verify(userRepository).saveAndFlush(testUser);
    }
    @Test
    public void getGamesPlayedRanking_returnsCorrectOrder() {
        // Setup
        User user1 = new User();
        user1.setGamesPlayed(10);
        User user2 = new User();
        user2.setGamesPlayed(5);

        // Use a mutable list such as ArrayList
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Mockito.when(userRepository.findAll()).thenReturn(users);

        // Action
        List<User> rankedUsers = userService.getGamesPlayedRanking(2L);

        // Assert
        assertEquals(2, rankedUsers.size());
        assertEquals(10, rankedUsers.get(0).getGamesPlayed());
    }

    @Test
    public void getTotalScoreRanking_returnsGivenRange() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setGamesPlayed(10);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setGamesPlayed(5);

        User user3 = new User();
        user3.setUsername("user3");
        user3.setGamesPlayed(7);

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<User> topUsers = userService.getGamesPlayedRanking(2L);

        assertEquals(2, topUsers.size());
        assertEquals(topUsers.get(0).getUsername(), "user1");
        assertEquals(topUsers.get(1).getUsername(), "user3");
    }

}
