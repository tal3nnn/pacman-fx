package pac;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Project: PAC-MAN
 * @author tsiga
 * Date: 05/23/2023
 * Description: Create the instruction panel
 */
public class Instruction {
    private final AnchorPane root;

    public Instruction() {
        var lines = new String[]{
            "USE THE ARROW KEYS TO MOVE.",
            "EAT ALL PAC-GUMS TO FINISH A LEVEL.",
            "GHOSTS WILL KILL YOU ASIDE IF YOU EAT",
            "A POWER PAC-GUM.",
            "IN ARCADE AND RANDOM MODE YOU HAVE TO",
            "ALL LEVELS ( LIMITED ).",
            "IN CUSTOM MODE YOU CAN PLAY LEVELS",
            "MADE IN THE LEVEL EDITOR.",

            "THE EDITOR CAN BE USED TO EDIT UP TO",
            "99 CUSTOM LEVELS, THAT WILL BE SAVED IN",
            "YOUR PC, UNDER A FOLDER CALLED /ADDGAMES.",
            "YOU CAN COPY-PASTE THE CONTENT OF THIS",
            "FOLDER TO SHARE/PUBLISH YOUR LEVELS."
        };

        Text titleGame = createText("#e1ff00", "HOW TO PLAY", 60, 18);
        Text titleEditor = createText("#e1ff00", "LEVEL EDITOR", 236, 18);
        root = new AnchorPane(titleGame, titleEditor);
        root.setPrefSize(600, 400);
        root.setStyle("-fx-background-color: #000000;");

        for (int i = 1; i < lines.length; i++) {
            Text line = createText("WHITE", lines[i], (i > 7 ? 123 : 67) + i * 17, 15);
            line.setTranslateX(10); // Indentation
            root.getChildren().add(line);
        }

        root.setPrefSize(600, 400);
        root.setStyle("-fx-background-color: #000000;");
    }

    /**
     * Get the AnchorPane.
     * @return the root
     */
    public AnchorPane getRoot() {
        return root;
    }

    /**
     * Create and format text for easy instruction creation.
     * 
     * @param color
     * @param text
     * @param alignY
     * @param fontSize
     * @return 
     */
    private Text createText(String color, String text, double alignY, int fontSize) {
        Text textNode = new Text(text);
        textNode.setFill(Color.web(color));
        textNode.setFont(Font.loadFont("file:resources/pixelNes.otf", fontSize));
        textNode.setLayoutX(30);
        textNode.setLayoutY(alignY);
        textNode.setTextAlignment(TextAlignment.CENTER);
        return textNode;
    }
}
