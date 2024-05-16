package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        System.out.println("Received Message:" + message);
        return message;
    }

    @PostMapping("/send")
    public void send(@RequestBody ChatMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}
