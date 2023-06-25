package pac;

import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Project: PAC-MAN
 * @author tsiga
 * Date: 05/23/2023
 * Description: 2D PAC-MAN game written with JavaFX
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start the application.
     * 
     * @param primaryStage 
     */
    @Override
    public void start(Stage primaryStage) {
        Stage progressStage = new Stage();
        progressStage.initStyle(StageStyle.UNDECORATED);
        progressStage.setResizable(false);

        VBox progressRoot = new VBox();
        progressRoot.setAlignment(Pos.CENTER);
        progressRoot.setStyle("-fx-background-color: black;");
        Scene progressScene = new Scene(progressRoot, 600, 500, Color.BLACK);
        progressScene.setFill(Color.web("#2200ff"));
        progressStage.setScene(progressScene);

        ImageView brandingImage = new ImageView(new Image("file:resources/branding.png"));
        brandingImage.setOpacity(0);
        progressRoot.getChildren().add(brandingImage);

        // Fade in animation for branding image
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), brandingImage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(30);
        progressBar.setStyle("-fx-accent: #2200ff; -fx-control-inner-background: black; -fx-border-color: #2200ff; -fx-border-width: 3;");

        // Text inside the progress bar
        Text textPercent = new Text();
        textPercent.setFill(Color.web("WHITE"));
        textPercent.setFont(Font.loadFont("file:resources/pixelNes.otf", 20));

        // Text above the progress bar
        Text textLoading = new Text("LOADING");
        textLoading.setFill(Color.web("#2200ff"));
        textLoading.setFont(Font.loadFont("file:resources/pixelNes.otf", 20));

        VBox boxProgress = new VBox(progressBar, textPercent);
        boxProgress.setAlignment(Pos.CENTER);
        boxProgress.setSpacing(-28);

        VBox boxLoading = new VBox(textLoading, boxProgress);
        boxLoading.setAlignment(Pos.CENTER);
        boxLoading.setTranslateY(-50);
        boxLoading.setVisible(false);
        progressRoot.getChildren().add(boxLoading);

        AtomicReference<Double> progress = new AtomicReference<>(0.0);
        Timeline timeline = new Timeline();

        double duration = 3.0; // Total duration in seconds
        double increment = 1.0 / (duration * 30); // Increment for each 0.1 seconds

        for (double seconds = 0; seconds <= duration; seconds += 0.033) {
            double progressValue = Math.min(progress.get() + increment, 1.0);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(seconds), event -> {
                progressBar.setProgress(progressValue);
                textPercent.setText(String.format("%.0f%%", progressValue * 100)); // Update percentage text
            });
            timeline.getKeyFrames().add(keyFrame);
            progress.set(progressValue);
        }

        timeline.setOnFinished(event -> {
            progressStage.close();
            showMenu(primaryStage);
        });

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), brandingImage);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // Sequential transition to fade in branding, then fade it out, and show the loading bar
        SequentialTransition sequentialTransition = new SequentialTransition(fadeIn,
                new PauseTransition(Duration.seconds(1)), // Pause for 1 second before fading out
                fadeOut,
                new PauseTransition(Duration.seconds(1)) // Pause for 1 second before showing the loading bar
        );

        // After the branding animation is complete, make the stackProgress visible and start the timeline
        sequentialTransition.setOnFinished(event -> {
            boxLoading.setVisible(true);
            timeline.play();
        });

        sequentialTransition.play();
        progressStage.show();
    }

    /**
     * Create the selection menu.
     * 
     * @param primaryStage 
     */
    private void showMenu(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        primaryStage.setTitle("PACMAN");
        primaryStage.getIcons().add(new Image("file:resources/icon.png"));
        primaryStage.setScene(new Scene(root, 600, 500, Color.BLACK));
        primaryStage.setResizable(false);

        ImageView titleImageView = new ImageView(new Image("file:resources/title.png"));
        titleImageView.setFitWidth(250);
        titleImageView.setFitHeight(51.02);

        var text = new String[]{"PLAY", "LEVEL EDITOR", "HIGH SCORES", "TUTORIAL", "EXIT"};
        VBox boxLabel = new VBox(10);

        // Add the buttons to the selection menu
        for (int i = 0; i < text.length; i++) {
            Text button = createMenuText(text[i]);
            boxLabel.getChildren().add(button);
            
            switch (i) {
                case 0 -> button.setOnMouseClicked(event -> { // PLAY
                    new GameHandler().start(new Stage());
                    primaryStage.close();
                });
                case 3 -> button.setOnMouseClicked(event -> showInstructionScreen()); // TUTORIAL
                case 4 -> button.setOnMouseClicked(event -> System.exit(0)); // EXIT
            }
        }

        VBox menuVBox = new VBox(50, titleImageView, boxLabel);
        menuVBox.setAlignment(Pos.CENTER);
        root.getChildren().add(menuVBox);

        primaryStage.show();
    }

    /**
     * Format the text for the menu.
     * 
     * @param text
     * @return 
     */
    private Text createMenuText(String text) {
        Text menuText = new Text(text);
        menuText.setFill(Color.web("#2200ff"));
        menuText.setFont(Font.loadFont("file:resources/pixelNes.otf", 20));
        menuText.setTranslateX(205);
        return menuText;
    }

    /**
     * Display the instruction screen.
     */
    private void showInstructionScreen() {
        Stage instructionStage = new Stage();
        instructionStage.setTitle("HOW TO PLAY");
        instructionStage.getIcons().add(new Image("file:resources/icon.png"));
        instructionStage.setScene(new Scene(new Instruction().getRoot(), 600, 400));
        instructionStage.setResizable(false);
        instructionStage.show();
    }
}
