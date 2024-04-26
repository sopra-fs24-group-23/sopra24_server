package ch.uzh.ifi.hase.soprafs24.exceptions;

public class LobbyLockedException extends Exception {
    public LobbyLockedException(String errorMessage) {
        super(errorMessage);
    }
}
