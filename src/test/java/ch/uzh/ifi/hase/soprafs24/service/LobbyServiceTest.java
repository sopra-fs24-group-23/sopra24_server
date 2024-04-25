package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.events.LobbyClosedEvent;
import ch.uzh.ifi.hase.soprafs24.exceptions.UnauthorizedException;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

class LobbyServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private LobbyService lobbyService;

    private User user;
    private Lobby lobby;

    @BeforeEach
    void setUp() {
        user = new User();
        lobby = new Lobby(new Player(user.getId(), user.getUsername(), user.getToken()));
        UserRepository userRepository = new UserRepository() {
            @Override
            public User findByUsername(String username) {
                return null;
            }

            @Override
            public User findByToken(String token) {
                return null;
            }

            @Override
            public List<User> findAll() {
                return null;
            }

            @Override
            public List<User> findAll(Sort sort) {
                return null;
            }

            @Override
            public List<User> findAllById(Iterable<Long> longs) {
                return null;
            }

            @Override
            public <S extends User> List<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends User> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public void deleteInBatch(Iterable<User> entities) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public User getOne(Long aLong) {
                return null;
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example) {
                return null;
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
                return null;
            }

            @Override
            public Page<User> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> S save(S entity) {
                return null;
            }

            @Override
            public Optional<User> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(User entity) {

            }

            @Override
            public void deleteAll(Iterable<? extends User> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public <S extends User> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends User> boolean exists(Example<S> example) {
                return false;
            }
        };

        HashMap<String, Lobby> lobbies = new HashMap<>();
        lobbies.put(lobby.getId(), lobby);

        when(userRepository.findByToken(anyString())).thenReturn(user);
    }

    @Test
    void getLobbyById_ExistingId_ReturnsLobby() {
        assertEquals(lobby, lobbyService.getLobbyById(lobby.getId()));
    }

    @Test
    void getLobbyById_NonExistingId_ThrowsException() {
        assertThrows(ResponseStatusException.class, () -> lobbyService.getLobbyById("nonExistingId"));
    }

    @Test
    void createLobby_CreatesAndStoresLobby() {
        when(userRepository.findByToken(user.getToken())).thenReturn(new User());
        Lobby newLobby = lobbyService.createLobby(user);
        assertNotNull(lobbyService.getLobbyById(newLobby.getId()));
        assertTrue(newLobby.getHost().getIsHost());
    }

    @Test
    void deleteLobby_ByHost_DeletesLobby() {
        lobbyService.deleteLobby(lobby.getId(), user);
        //assertFalse(lobbyService.lobbies.containsKey(lobby.getId()));
        verify(eventPublisher).publishEvent(any(LobbyClosedEvent.class));
    }

    @Test
    void deleteLobby_NotByHost_ThrowsException() {
        User nonHostUser = new User();
        assertThrows(UnauthorizedException.class, () -> lobbyService.deleteLobby(lobby.getId(), nonHostUser));
    }

    // Additional tests for addPlayer, removePlayer, and kickPlayer would be similar
}
