package ch.uzh.ifi.hase.soprafs24.exceptions;

public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
