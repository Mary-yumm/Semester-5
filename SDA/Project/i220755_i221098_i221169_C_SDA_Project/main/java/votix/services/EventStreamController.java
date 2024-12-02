package votix.services;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
public class EventStreamController {

    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    // SSE Endpoint
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents() {
        return sink.asFlux();
    }

    // Endpoint to trigger a new event
    @PostMapping("/notify")
    public void notifyClients(@RequestParam("message") String message) {
        if (message != null && !message.isEmpty()) {
            boolean success = sink.tryEmitNext(message).isSuccess();
            if (success) {
                // Log successful message emission
                System.out.println("Message successfully sent: " + message);
            } else {
                // Log failure to send the message
                System.out.println("Failed to send message: " + message);
            }
        } else {
            // Handle the case where the message is null or empty
            System.out.println("Received null or empty message. Skipping event emission.");
        }
    }

}
