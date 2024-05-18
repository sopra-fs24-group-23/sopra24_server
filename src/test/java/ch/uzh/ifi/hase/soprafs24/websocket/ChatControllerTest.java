package ch.uzh.ifi.hase.soprafs24.websocket;

import ch.uzh.ifi.hase.soprafs24.controller.ChatController;
import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

public class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatController chatController;

    private String lobbyId;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        chatController = new ChatController(messagingTemplate);
        lobbyId = "testLobbyId";
    }

    @Test
    public void testSendMessage() {
        // Given
        ChatMessage message = new ChatMessage();
        message.setSender("user1");
        message.setContent("Hello, world!");
        message.setTimestamp("2024-05-14T10:15:30");

        // When
        //ChatMessage response = chatController.sendMessage(message, lobbyId);
        chatController.sendMessage(message, lobbyId);

        // Then
        //assertEquals("user1", response.getSender());
        //assertEquals("Hello, world!", response.getContent());
        // assertEquals("2024-05-14T10:15:30", response.getTimestamp());
        verify(messagingTemplate).convertAndSend(String.format("/topic/chat/%s", lobbyId), message);
    }

    @Test
    public void testSend() {
        // Given
        ChatMessage message = new ChatMessage();
        message.setSender("user1");
        message.setContent("Hello, world!");
        message.setTimestamp("2024-05-14T10:15:30");

        // When
        chatController.sendMessage(message, lobbyId);

        // Then
        verify(messagingTemplate).convertAndSend(String.format("/topic/chat/%s", lobbyId), message);
    }

    @Test
    public void testSendMessage_NullMessage() {
        // Given
        ChatMessage message = null;

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            chatController.sendMessage(message, lobbyId);
        });
        assertEquals("Message cannot be null", thrown.getMessage());
    }

    @Test
    public void testSendMessage_EmptyMessage() {
        // Given
        ChatMessage message = new ChatMessage();
        message.setSender("user1");
        message.setContent("");
        message.setTimestamp("2024-05-14T10:15:30");

        // When
        //ChatMessage response = chatController.sendMessage(message);
        chatController.sendMessage(message, lobbyId);

        // Then
        //assertEquals("user1", response.getSender());
        //assertEquals("", response.getContent());
        //assertEquals("2024-05-14T10:15:30", response.getTimestamp());
        verify(messagingTemplate).convertAndSend(String.format("/topic/chat/%s", lobbyId), message);
    }

    @Test
    public void testSend_RestEndpoint() {
        // Given
        ChatMessage message = new ChatMessage();
        message.setSender("user1");
        message.setContent("Hello, world!");
        message.setTimestamp("2024-05-14T10:15:30");

        // When
        chatController.sendMessage(message, lobbyId);

        // Then
        verify(messagingTemplate).convertAndSend(String.format("/topic/chat/%s", lobbyId), message);
    }

    @Test
    public void testSend_EmptyMessage_RestEndpoint() {
        // Given
        ChatMessage message = new ChatMessage();
        message.setSender("user1");
        message.setContent("");
        message.setTimestamp("2024-05-14T10:15:30");

        // When
        chatController.sendMessage(message, lobbyId);

        // Then
        verify(messagingTemplate).convertAndSend(String.format("/topic/chat/%s", lobbyId), message);
    }

    @Test
    public void testSend_NullMessage_RestEndpoint() {
        // Given
        ChatMessage message = null;

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            chatController.sendMessage(message, lobbyId);
        });
        assertEquals("Message cannot be null", thrown.getMessage());
    }
}
