package ch.uzh.ifi.hase.soprafs24.websocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private TaskScheduler messageBrokerTaskScheduler;

    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
        this.messageBrokerTaskScheduler = taskScheduler;
    }

    @Override
    public void registerStompEndpoints (StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        .setAllowedOrigins(
                "http://localhost:3000/",
                "https://sopra-fs24-group-23-client.oa.r.appspot.com/"
        )
        .setAllowedOriginPatterns("*");
    }

    /** This message configures websocket endpoint
     * - It sets /app as the PREFIX for endpoints that are handled directly by the application
     * i.e. for @MessageMappings in controller classes. (Client -> Server messages)
     * - It sets /topic as the PREFIX for endpoints that clients can subscribe to
     * i.e. for template.convertAndSend("/topic/sometopic", Message) or @SendTo
     * Server -> Client messaging, one-to-many broadcast
     * - It can also register a prefix for specific user destinations by using
     * setUserDestinationPrefixes; Server -> Client messaging, point-to-point
     * **/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[] {10000, 10000})
                .setTaskScheduler(this.messageBrokerTaskScheduler);
    }
}

