package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class LeaveLobbyResponseDTO {
    private String message;

    public LeaveLobbyResponseDTO(String message) {
        this.message = message;
    }

    // Getter and Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

