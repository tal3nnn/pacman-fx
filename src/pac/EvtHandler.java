package pac;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static pac.Main.stage;

/**
 * Project: PAC-MAN
 * @author tsiga
 * Date: 06/24/2023
 * Description: Handles events within the game
 */
public class EvtHandler implements EventHandler<KeyEvent> {
    public static String STATE = "BRANDING";
    public static String OLD_STATE;

    public static int menuChoice = 0;
    private final VBox boxLabel;

    public EvtHandler(VBox boxLabel) {
        this.boxLabel = boxLabel;
    }

    /**
     * Handles all KeyEvents for each state.
     *
     * @param keyEvent
     */
    @Override
    public void handle(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();

        switch (STATE) {
            case "MENU" -> {
                switch (keyCode) {
                    case UP -> {
                        menuChoice--;
                        if (menuChoice < 0)
                            menuChoice = 4;
                        updateMenuDisplay();
                    }
                    case DOWN -> {
                        menuChoice++;
                        if (menuChoice > 4)
                            menuChoice = 0;
                        updateMenuDisplay();
                    }
                    case ENTER -> {
                        switch (menuChoice) {
                            case 0 -> {
                                new GameHandler().start(new Stage());
                                stage.close();
                            }
                            case 1 -> setState("EDITOR");
                            case 2 -> setState("SCORES");
                            case 3 -> setState("TUTORIAL");
                            case 4 -> System.exit(0);
                        }
                    }
                }
            }

            case "GAME" -> {} // TODO

            case "EDITOR", "SCORES", "TUTORIAL" -> {
                switch (keyCode) {
                    case ESCAPE, ENTER -> revertState();
                }
            }

            case "PAUSE" -> {} // TODO
        }
    }

    /**
     * Update the menu display based on the selected option
     */
    private void updateMenuDisplay() {
        ObservableList<Node> options = boxLabel.getChildren();
        for (int i = 0; i < options.size(); i++) {
            Text selection = (Text) options.get(i);
            if (i == menuChoice) {
                selection.setFill(Color.WHITE);
            } else {
                // Reset the style for non-selected options
                selection.setFill(Color.web("#2200ff"));
                selection.setStyle("");
            }
        }
    }

    /**
     * Set a new state.
     *
     * @param state
     */
    public static void setState(String state) {
        if (STATE.equals(state)) return;
        OLD_STATE = STATE;
        STATE = state;
        DisplayHandler.displayElements(stage);
        System.out.println(STATE);
    }

    /**
     * Revert to the previous state.
     */
    public static void revertState() {
        String tempState = STATE;
        STATE = OLD_STATE;
        OLD_STATE = tempState;
        menuChoice = 0;
        DisplayHandler.displayElements(stage);
    }
}