package pac;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import static pac.EvtHandler.STATE;

/**
 * Project: PAC-MAN
 * @author tsiga
 * Date: 06/24/2023
 * Description: 
 */
public class DisplayHandler {
    private static final Font fontBig = Font.loadFont("file:resources/pixelNes.otf", 25);
    private static final Font fontMedium = Font.loadFont("file:resources/pixelNes.otf", 20);

    public static void displayElements(Stage displayStage) {
        switch (STATE) {
            case "BRANDING" -> {
                VBox boxBranding = new VBox();
                boxBranding.setAlignment(Pos.CENTER);
                boxBranding.setStyle("-fx-background-color: black;");
                Scene sceneBranding = new Scene(boxBranding, 600, 500);

                ImageView brandingImage = new ImageView(new Image("file:resources/branding.png"));
                brandingImage.setOpacity(0);
                boxBranding.getChildren().add(brandingImage);

                // Fade in animation for branding image
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), brandingImage);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);

                // Sequential transition to fade in branding, then fade it out, and show the loading bar
                SequentialTransition sequentialTransition = new SequentialTransition(
                        fadeIn,
                        new PauseTransition(Duration.seconds(1)) // Pause for one second
                );

                displayStage.setScene(sceneBranding);
                sequentialTransition.play();

                // After the branding animation is complete, change the state
                sequentialTransition.setOnFinished(event -> {
                    EvtHandler.setState("LOADING");
                    displayElements(displayStage);
                });
            }

            case "LOADING" -> {
                VBox rootProgress = new VBox();
                rootProgress.setAlignment(Pos.CENTER);
                rootProgress.setStyle("-fx-background-color: black;");
                Scene sceneProgress = new Scene(rootProgress, 600, 500, Color.BLACK);
                sceneProgress.setFill(Color.web("#2200ff"));

                ProgressBar progressBar = new ProgressBar();
                progressBar.setPrefWidth(300);
                progressBar.setPrefHeight(32);
                progressBar.setStyle("-fx-accent: #2200ff; -fx-control-inner-background: black; -fx-border-color: #2200ff; -fx-border-width: 3;");

                // Text inside the progress bar
                Text textPercent = new Text();
                textPercent.setFill(Color.web("WHITE"));
                textPercent.setFont(fontMedium);

                // Text above the progress bar
                Text textLoading = new Text("LOADING");
                textLoading.setFill(Color.web("#2200ff"));
                textLoading.setFont(fontMedium);

                VBox boxProgress = new VBox(progressBar, textPercent);
                boxProgress.setAlignment(Pos.CENTER);
                boxProgress.setSpacing(-29);

                VBox boxLoading = new VBox(textLoading, boxProgress);
                boxLoading.setAlignment(Pos.CENTER);
                boxLoading.setTranslateY(0);
                rootProgress.getChildren().add(boxLoading);

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

                displayStage.setScene(sceneProgress);
                timeline.play();

                timeline.setOnFinished(event -> {
                    EvtHandler.setState("MENU");
                    displayElements(displayStage);
                });
            }

            case "MENU" -> {
                ImageView viewTitle = new ImageView(new Image("file:resources/title.png"));
                viewTitle.setFitWidth(250);
                viewTitle.setFitHeight(51.02);

                var text = new String[]{"PLAY", "LEVEL EDITOR", "HIGH SCORES", "TUTORIAL", "EXIT"};
                VBox boxLabel = new VBox(10);

                // Add the buttons to the selection menu
                for (int i = 0; i < text.length; i++) {
                    Text selection = new Text(text[i]);
                    selection.setFill(Color.web("#2200ff"));
                    selection.setFont(fontMedium);
                    selection.setTranslateX(205);
                    boxLabel.getChildren().add(selection);
                    
                    switch (i) {
                        case 0 -> selection.setFill(Color.WHITE);
                    }
                }

                VBox boxMenu = new VBox(50, viewTitle, boxLabel);
                boxMenu.setAlignment(Pos.CENTER);
                boxMenu.setStyle("-fx-background-color: black;");

                Scene scene = new Scene(boxMenu, 600, 500, Color.BLACK);

                // Iterate through the menu options
                EvtHandler eventHandler = new EvtHandler(boxLabel);
                scene.setOnKeyPressed(eventHandler);

                displayStage.setScene(scene);
            }

            case "GAME" -> {} // TODO

            case "EDITOR", "SCORES" -> {
                VBox box = new VBox();
                box.setStyle("-fx-background-color: black;");

                Text textInfo = new Text("COMING SOON");
                textInfo.setFill(Color.YELLOW);
                textInfo.setFont(fontBig);
                textInfo.setTranslateX(30);
                textInfo.setTranslateY(30);
                box.getChildren().add(textInfo);

                Scene scene = new Scene(box, 600, 500);
                displayStage.setScene(scene);
            }

            case "TUTORIAL" -> {
                VBox box = new VBox();
                box.setStyle("-fx-background-color: black;");
                box.setTranslateY(20);

                // Extract the lines from the instructions file and display it
                try (BufferedReader reader = new BufferedReader(new FileReader("resources/instructions.txt"))) {
                    String line;
                    int lineNumber = 0;

                    while ((line = reader.readLine()) != null) {
                        Text text = new Text(line);
                        if (lineNumber == 0 || lineNumber == 11) {
                            text.setFont(fontBig);
                            text.setFill(Color.YELLOW);
                            text.setTranslateX(20);
                        } else {
                            text.setFont(fontMedium);
                            text.setFill(Color.WHITE);
                            text.setTranslateX(30);
                        }

                        box.getChildren().add(text);
                        lineNumber++;
                    }
                } catch (IOException ex) {}

                Scene scene = new Scene(box, 700, 600);
                scene.setFill(Color.BLACK);

                displayStage.setScene(scene);
            }
        }

        displayStage.setTitle("PACMAN");
        displayStage.getIcons().add(new Image("file:resources/icon.png"));
        displayStage.setResizable(false);
        displayStage.show();
    }
}
