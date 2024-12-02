package votix.services;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SSEJavaFXExample extends Application {

    private Label label = new Label("Waiting for events...");

    @Override
    public void start(Stage primaryStage) {
        // Set up the JavaFX UI
        primaryStage.setScene(new Scene(label, 400, 200));
        primaryStage.setTitle("SSE Example");
        primaryStage.show();

        // Start receiving SSE events in a new thread
        new Thread(this::receiveSSE).start();
    }

    private void receiveSSE() {
        try {
            URL url = new URL("http://100.91.228.86:8080/events"); // Change to your server's URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            // Read from the SSE stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Handle incoming SSE events
                // Update the UI with the new event data (this must be done on the JavaFX application thread)
                // Debug log for received message
                System.out.println("Received SSE Event: " + line);
                final String event = line;
                Platform.runLater(() -> label.setText(event));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
