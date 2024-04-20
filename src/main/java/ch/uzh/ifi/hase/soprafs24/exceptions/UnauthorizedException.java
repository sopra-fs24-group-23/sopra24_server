package ch.uzh.ifi.hase.soprafs24.exceptions;

public class UnauthorizedException extends Exception {
    public UnauthorizedException(String errorMessage) {
        super(errorMessage);
    }
}
