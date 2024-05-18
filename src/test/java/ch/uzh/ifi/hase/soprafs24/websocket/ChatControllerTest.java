package ch.uzh.ifi.hase.soprafs24.websocket;

import ch.uzh.ifi.hase.soprafs24.controller.ChatController;
import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatController chatController;

    private final String lobbyId = "testId";

    private ChatMessage message;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        chatController.setMessagingTemplate(messagingTemplate);

        message = new ChatMessage();
        message.setContent("TestBody");
        message.setSender("TestUser");
        message.setTimestamp("TestTime");
        message.setColor("#000000");
    }

    @Test
    public void testSendMessage() {
        chatController.sendMessage(message, lobbyId);

        Mockito.verify(messagingTemplate, Mockito.times(1)).convertAndSend(
                String.format("/topic/chat/%s", lobbyId), message);
    }

    @Test
    public void testSendMessage_nullMessage() {
        message.setContent("");

        assertThrows(IllegalArgumentException.class, () -> {
            chatController.sendMessage(message, lobbyId);
        });
    }
}
