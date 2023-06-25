package pac;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Project: PAC-MAN
 * @author tsiga
 * Date: 05/23/2023
 * Description: 2D PAC-MAN game written with JavaFX
 */
public class Main extends Application {
    public static Stage stage;

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
        stage = primaryStage; // Make the main stage obtainable globally

        EvtHandler.setState("BRANDING");
        DisplayHandler.displayElements(stage);
    }
}