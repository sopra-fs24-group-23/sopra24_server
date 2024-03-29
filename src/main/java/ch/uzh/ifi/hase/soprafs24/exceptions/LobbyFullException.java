package ch.uzh.ifi.hase.soprafs24.exceptions;

public class LobbyFullException extends Exception {
    public LobbyFullException(String errorMessage) {
        super(errorMessage);
    }
}
