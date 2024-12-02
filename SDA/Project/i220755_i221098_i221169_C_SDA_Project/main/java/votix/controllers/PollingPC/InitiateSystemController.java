package votix.controllers.PollingPC;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;

public class InitiateSystemController {

    public ImageView logoImageView;

    @FXML
    private Label timerLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private ProgressIndicator loadingIndicator; // Add the ProgressIndicator reference

    private int hours = 0;
    private int minutes = 0;
    private int seconds = 5;

    private Stage primaryStage; // Reference to the primary stage

    private Runnable onCountdownComplete;

    private Timeline countdown; // Declare countdown as a field

    public void setOnCountdownComplete(Runnable onCountdownComplete) {
        this.onCountdownComplete = onCountdownComplete;
    }

    public void initialize() {
        // Create the countdown Timeline
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (seconds > 0) {
                seconds--;
            } else {
                // Perform actions when countdown reaches zero
                loadingIndicator.setStyle("-fx-accent: #FF5733;");
                loadingIndicator.setVisible(true);
                messageLabel.setText("Loading Polling Station's data...");
                timerLabel.setText("");

                // Notify that the countdown has completed
                if (onCountdownComplete != null) {
                    countdown.stop();
                    onCountdownComplete.run();
                }
            }
            timerLabel.setText(getFormattedTime());
        }));

        countdown.setCycleCount(seconds + 1);
        countdown.play();
    }

    private String getFormattedTime() {
        return String.format("%02d hours : %02d minutes : %02d seconds", hours, minutes, seconds);
    }

    private void transitionToPollingPC() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlFiles/PollingPC/PollingPC.fxml"));
            Scene pollingPCScene = new Scene(loader.load());

            PollingPcController pollingPcController = loader.getController();

            // Pass the Stage or EMS instance if needed
            if (pollingPcController != null) {
                System.out.println("Transitioning to Polling PC...");
                pollingPcController.setPrimaryStage(primaryStage); // Optional, if needed
            }

            primaryStage.setScene(pollingPCScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}

