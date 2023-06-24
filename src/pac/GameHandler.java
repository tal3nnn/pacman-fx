package pac;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Project: PAC-MAN
 * @author tsiga
 * Date: 05/23/2023
 * Description: Render the map and handle the game
 */
public class GameHandler extends Application {
    private static final int TILE_SIZE = 16;
    private final List<ImageView> ghostImageViews = new ArrayList<>();
    private final SpriteExtractor extractor = new SpriteExtractor("resources/pacman_sheet.png", "resources/pacman_sheet.json", 16, 16);
    private final ArrayList<Double> ghostDirectionsX = new ArrayList<>();
    private final ArrayList<Double> ghostDirectionsY = new ArrayList<>();
    private final ArrayList<Long> lastDirectionChangeTimes = new ArrayList<>();
    private final Random random = new Random();
    private final Image[] deathSprites = new Image[12];
    ArrayList<Integer> ghostDirections = new ArrayList<>();
    private int currentLevel = 1;
    private Stage stage;
    private ImageView playerImageView;
    private double playerX;
    private double playerY;
    private int[][] levelData;
    private Group root;
    private Text scoreText;
    private Text scoreTxt;
    private Text levelText;
    private Text levelTxt;
    private int currentScore = 0;
    private JSONObject mapData;
    private int lives = 3;
    private int currentDirectionX = 0;
    private int currentDirectionY = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        root = new Group();
        Scene scene = new Scene(root, TILE_SIZE * 40.0, TILE_SIZE * 31.0, Color.BLACK);
        primaryStage.setTitle("PACMAN");
        primaryStage.getIcons().add(new Image("file:resources/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        try {
            // Read the map JSON file
            String json = new String(Files.readAllBytes(Paths.get("resources/level.json")));
            mapData = new JSONObject(json);
        } catch (IOException e) {}

        // Get the map array
        JSONArray mapArray = mapData.getJSONArray("MAP");
        levelData = new int[mapArray.length()][mapArray.getJSONArray(0).length()];

        // Iterate over each row of the map
        for (int row = 0; row < mapArray.length(); row++) {
            JSONArray rowArray = mapArray.getJSONArray(row);

            // Iterate over each column of the map
            for (int col = 0; col < rowArray.length(); col++) {
                String spriteName = rowArray.getString(col);

                // Get the image for the sprite
                Image image = extractor.extractSprite(spriteName);

                // Create an ImageView for the sprite
                ImageView imageView = new ImageView(image);
                imageView.setX(col * (double) TILE_SIZE);
                imageView.setY(row * (double) TILE_SIZE);

                // Add the ImageView to the root
                root.getChildren().add(imageView);

                // Store the tile value in levelData
                if (spriteName.contains("WALL") && !spriteName.equals("WALL_GHOST"))
                    levelData[row][col] = 1;
                else if (spriteName.contains("PACGUM"))
                    levelData[row][col] = 2;
                else
                    levelData[row][col] = 0;
            }
        }

        // Death animation
        for (int i = 0; i < deathSprites.length; i++)
            deathSprites[i] = extractor.extractSprite("PACMAN_DEAD_" + i);

        // Get player position
        JSONArray playerPosArray = mapData.getJSONArray("PLAYER_POS");
        playerX = playerPosArray.getDouble(0);
        playerY = playerPosArray.getDouble(1);

        // Create an ImageView for the player sprite
        playerImageView = new ImageView(extractor.extractSprite("PACMAN_UP_0"));
        playerImageView.setX(playerX * TILE_SIZE);
        playerImageView.setY(playerY * TILE_SIZE);
        Rectangle rectangle = new Rectangle(450, 5, 188, 490);
        levelText = new Text("Level");
        levelText.setX(480);
        levelText.setY(50);
        levelText.setFill(Color.YELLOW);
        levelText.setFont(Font.loadFont("file:resources/pixelNes.otf", 25));
        levelTxt = new Text(String.valueOf(currentLevel));
        levelTxt.setX(480);
        levelTxt.setY(75);
        levelTxt.setFill(Color.WHITE);
        levelTxt.setFont(Font.loadFont("file:resources/pixelNes.otf", 19));
        scoreText = new Text("Score");
        scoreText.setX(480);
        scoreText.setY(125);
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.loadFont("file:resources/pixelNes.otf", 25));
        scoreTxt = new Text(String.valueOf(currentScore));
        scoreTxt.setX(480);
        scoreTxt.setY(150);
        scoreTxt.setFill(Color.WHITE);
        scoreTxt.setFont(Font.loadFont("file:resources/pixelNes.otf", 19));

        // Set the border color
        rectangle.setStroke(Color.web("#0000FF"));

        // Set the border width
        rectangle.setStrokeWidth(3);
        root.getChildren().addAll(levelText, levelTxt, scoreText, scoreTxt);
        root.getChildren().add(rectangle);
        root.getChildren().add(playerImageView);
        levelText.toFront();
        levelTxt.toFront();
        scoreText.toFront();
        scoreTxt.toFront();

        // Get ghost positions
        JSONArray ghostPosArray = mapData.getJSONArray("GHOST_POS");
        for (int i = 0; i < ghostPosArray.length(); i++) {
            JSONArray ghostPos = ghostPosArray.getJSONArray(i);
            double ghostX = ghostPos.getDouble(0);
            double ghostY = ghostPos.getDouble(1);

            // Create an ImageView for each ghost sprite
            ImageView ghostImageView = new ImageView(extractor.extractSprite("GHOST_" + i + "_DOWN_0"));
            ghostImageView.setX(ghostX * TILE_SIZE);
            ghostImageView.setY(ghostY * TILE_SIZE);
            root.getChildren().add(ghostImageView);
            ghostImageViews.add(ghostImageView);
            ghostDirectionsX.add(0.0);
            ghostDirectionsY.add(-1.0);
        }

        for (int i = 0; i < ghostImageViews.size(); i++) {
            ghostDirections.add(-1);
            lastDirectionChangeTimes.add(System.currentTimeMillis());
        }

        // Set up key event handling
        scene.setOnKeyReleased(event -> {
            KeyCode keyCode = event.getCode();
            switch (keyCode) {
                case UP -> {
                    if (currentDirectionY != -1) keepMovingPlayer(0, -1);
                }
                case DOWN -> {
                    if (currentDirectionY != 1) keepMovingPlayer(0, 1);
                }
                case LEFT -> {
                    if (currentDirectionX != -1) keepMovingPlayer(-1, 0);
                }
                case RIGHT -> {
                    if (currentDirectionX != 1) keepMovingPlayer(1, 0);
                }
                case ENTER -> {
                    
                }
                default -> System.out.println("Invalid key");
            }
        });
        updateGhostMovement();
    }

    private void updateGhostMovement() {
        // Update ghost movements every 1 second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.25), event -> {
            keepMovingPlayer(currentDirectionX, currentDirectionY);
            moveGhosts();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Move the player according to the keys pressed.
     * 
     * @param dx
     * @param dy 
     */
    private void keepMovingPlayer(int dx, int dy) {
        if (dx != currentDirectionX || dy != currentDirectionY) {
            // Update the movement direction if it's different from the current direction
            currentDirectionX = dx;
            currentDirectionY = dy;
        }

        double newX = playerX + dx;
        double newY = playerY + dy;

        // Check if the new position is off the map
        if (newX < 0) newX = levelData[0].length - 1.0;
        else if (newX >= levelData[0].length) newX = 0;

        if (newY < 0) newY = levelData.length - 1.0;
        else if (newY >= levelData.length) newY = 0;

        if (isValidMove(newX, newY)) {
            currentDirectionX = dx;
            currentDirectionY = dy;
            double roundedNewX = Math.round(newX * 10) / 10.0;
            double roundedNewY = Math.round(newY * 10) / 10.0;

            if (isOnFood(roundedNewX, roundedNewY)) {
                currentScore += 10;
                if (currentScore == 2440) {
                    currentScore = 0;
                    levelTxt.setText(String.valueOf(currentLevel++));
                }
                scoreTxt.setText(String.valueOf(currentScore));
                removeSprite(roundedNewX, roundedNewY);
            }

            playerX = roundedNewX;
            playerY = roundedNewY;

            // Determine the appropriate sprite skin based on direction
            String spriteName = "PACMAN_LEFT_0";
            if (currentDirectionX == -1) spriteName = "PACMAN_LEFT_0";
            else if (currentDirectionX == 1) spriteName = "PACMAN_RIGHT_0";
            else if (currentDirectionY == -1) spriteName = "PACMAN_UP_0";
            else if (currentDirectionY == 1) spriteName = "PACMAN_DOWN_0";

            // Update the player's sprite skin
            Image image = extractor.extractSprite(spriteName);
            playerImageView.setImage(image);

            playerImageView.setX(playerX * TILE_SIZE - 6);
            playerImageView.setY(playerY * TILE_SIZE);
        }
        checkCollision();
    }

    /**
     * Check if the move of direction is valid.
     * 
     * @param x
     * @param y
     * @return 
     */
    private boolean isValidMove(double x, double y) {
        if (x < 0 || x >= levelData[0].length || y < 0 || y >= levelData.length) return false; // Out of bounds
        int tileValue = levelData[(int) y][(int) x];
        return tileValue != 1; // Not a wall
    }

    /**
     * 
     * 
     * @param x
     * @param y
     * @return 
     */
    private boolean isOnFood(double x, double y) {
        return levelData[(int) y][(int) x] == 2; // Not a wall
    }

    /**
     * Remove the sprite from the screen.
     * 
     * @param x
     * @param y 
     */
    private void removeSprite(double x, double y) {
        // Iterate over the root group's children
        for (Iterator<Node> iterator = root.getChildren().iterator(); iterator.hasNext(); ) {
            Node node = iterator.next();
            if (node instanceof ImageView imageView) {
                double imageViewX = imageView.getX() / TILE_SIZE;
                double imageViewY = imageView.getY() / TILE_SIZE;
                // Check if the ImageView's position matches the given coordinates
                if (imageViewX == (int) x && imageViewY == (int) y) {
                    iterator.remove();
                    break;
                }
            }
        }

        // Update the levelData array to reflect the removed sprite
        levelData[(int) y][(int) x] = 0;
    }

    /**
     * Create movement for the ghosts.
     */
    private void moveGhosts() {
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < ghostImageViews.size(); i++) {
            ImageView ghostImageView = ghostImageViews.get(i);
            double ghostDx = ghostDirectionsX.get(i);
            double ghostDy = ghostDirectionsY.get(i);

            double currentX = ghostImageView.getX() / TILE_SIZE;
            double currentY = ghostImageView.getY() / TILE_SIZE;

            // Calculate the target position based on the current direction
            double targetX = currentX + ghostDx;
            double targetY = currentY + ghostDy;

            // Check if the target position is a valid move
            if (isValidMove(targetX, targetY)) {
                ghostImageView.setX(targetX * TILE_SIZE);
                ghostImageView.setY(targetY * TILE_SIZE);
            } else {
                // Ghost hit a wall, or it's time to change direction
                if (currentTime - lastDirectionChangeTimes.get(i) >= 5000) {
                    changeGhostDirection(); // Pass the index of the current ghost
                    lastDirectionChangeTimes.set(i, currentTime);
                } else changeGhostDirection(); // Pass the index of the current ghost
            }
            if ((ghostImageView.getX() == 0 && ghostImageView.getY() == 224)) {
                ghostImageView.setX(432);
                ghostImageView.setY(224);
            } else if ((ghostImageView.getX() == 432 && ghostImageView.getY() == 224)) {
                ghostImageView.setX(0);
                ghostImageView.setY(224);
            }

            // Determine the appropriate sprite skin based on direction
            String spriteName = "GHOST_" + i;
            if (ghostDx == -1) spriteName += "_LEFT_0";
            else if (ghostDx == 1) spriteName += "_RIGHT_0";
            else if (ghostDy == -1) spriteName += "_UP_0";
            else if (ghostDy == 1) spriteName += "_DOWN_0";

            // Update the ghost's sprite skin
            Image image = extractor.extractSprite(spriteName);
            ghostImageView.setImage(image);
        }
        checkCollision();
    }

    /**
     * Randomly change the ghosts direction.
     */
    private void changeGhostDirection() {
        int maxAttempts = 10;

        int[][] direction = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Left, Right, Up, Down

        for (int i = 0; i < ghostImageViews.size(); i++) {
            ImageView ghostImageView = ghostImageViews.get(i);
            double ghostX = ghostImageView.getX() / TILE_SIZE;
            double ghostY = ghostImageView.getY() / TILE_SIZE;

            int[] newDirection = null;
            boolean foundValidDirection = false;
            int attempt = 0;

            // Randomly choose a direction
            while (!foundValidDirection && attempt < maxAttempts) {
                int directionIndex = random.nextInt(direction.length);
                newDirection = direction[directionIndex];

                // Check if the new direction is a valid move
                double targetX = ghostX + newDirection[0];
                double targetY = ghostY + newDirection[1];
                if (isValidMove(targetX, targetY) && (ghostDirectionsX.get(i) + newDirection[0] != 0 || ghostDirectionsY.get(i) + newDirection[1] != 0))
                    foundValidDirection = true;

                attempt++;
            }

            // Update the ghost's direction variables
            if (foundValidDirection) {
                ghostDirectionsX.set(i, (double) newDirection[0]);
                ghostDirectionsY.set(i, (double) newDirection[1]);
            }
        }
    }

    /**
     * Check if the player is colliding with a ghost.
     */
    private void checkCollision() {
        Bounds playerBounds = playerImageView.getBoundsInParent();

        // Check collision with each ghost
        for (ImageView ghostImageView : ghostImageViews) {
            if (playerBounds.intersects(ghostImageView.getBoundsInParent())) {
                // Collision detected between player and ghost
                handleCollision();
                break;
            }
        }
    }

    /**
     * Action performed when a collision is confirmed.
     */
    private void handleCollision() {
        // Create a Timeline for the death sprites animation
        Timeline timeline = new Timeline();
        for (Image deathSprite : deathSprites) {
            KeyFrame keyFrame = new KeyFrame(Duration.millis(250), event -> playerImageView.setImage(deathSprite));
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(deathSprites.length);
        timeline.play();

        lives--;
        if (lives == 0) {
            gameOver();
            // TODO: Remove a life icon
        }
        initializeSprites();
    }

    /**
     * Initialize the sprite elements.
     */
    private void initializeSprites() {
        // Get player position
        JSONArray playerPosArray = mapData.getJSONArray("PLAYER_POS");
        playerX = playerPosArray.getDouble(0);
        playerY = playerPosArray.getDouble(1);

        // Create an ImageView for the player sprite
        playerImageView.setX(playerX * TILE_SIZE);
        playerImageView.setY(playerY * TILE_SIZE);

        // Get ghost positions
        JSONArray ghostPosArray = mapData.getJSONArray("GHOST_POS");
        for (int i = 0; i < ghostPosArray.length(); i++) {
            JSONArray ghostPos = ghostPosArray.getJSONArray(i);
            double ghostX = ghostPos.getDouble(0);
            double ghostY = ghostPos.getDouble(1);
            ghostImageViews.forEach(ghostImageView -> {
                // Create an ImageView for each ghost sprite
                ghostImageView.setX(ghostX * TILE_SIZE);
                ghostImageView.setY(ghostY * TILE_SIZE);
                ghostDirectionsX.add(0.0);
                ghostDirectionsY.add(-1.0);
            });
        }

        for (int i = 0; i < ghostImageViews.size(); i++) {
            ghostDirections.add(-1);
            lastDirectionChangeTimes.add(System.currentTimeMillis());
        }
    }

    /**
     * Method called when the game is over.
     */
    private void gameOver() {
        // Create a label with the provided text
        Label textLabel;
        textLabel = new Label("GAME OVER");
        Font customFont = Font.loadFont("file:resources/pixelNes.otf", 29);
        textLabel.setFont(customFont);
        textLabel.setStyle("-fx-text-fill: white;");

        // Create a stack pane to hold the label and set its background to black
        StackPane stackPane = new StackPane(textLabel);
        stackPane.setStyle("-fx-background-color: black;");

        // Create a scene with the stack pane and set its size
        Scene scene = new Scene(stackPane, 800, 600, Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }
}