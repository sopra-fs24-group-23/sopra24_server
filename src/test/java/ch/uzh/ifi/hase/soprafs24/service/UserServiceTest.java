package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

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

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
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
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(java.util.Optional.of(testUser));
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
        // Setup existing user in the database
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(java.util.Optional.of(testUser));
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


}
