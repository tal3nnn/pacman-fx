package pac;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: PAC-MAN
 * @author tsiga
 * Date: 05/23/2023
 * Description: Extract and work with sprites
 */
public class SpriteExtractor {
    private final int spriteWidth;
    private final int spriteHeight;
    private Image spriteSheet;
    private Map<String, int[]> spriteData;

    public SpriteExtractor(String spriteSheetPath, String jsonFilePath, int spriteWidth, int spriteHeight) {
        try {
            this.spriteSheet = new Image(new FileInputStream(spriteSheetPath));
        } catch (FileNotFoundException ex) {}
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.spriteData = readJsonFile(jsonFilePath);
    }

    /**
     * Get the tile value from the 
     * 
     * @param spriteName
     * @return value of tile
     */
    public static int getTileValue(String spriteName) {
        if (spriteName.contains("WALL") && !spriteName.equals("WALL_GHOST"))
            return 1;
        else if (spriteName.contains("PACGUM"))
            return 2;
        else
            return 0;
    }
    
    /**
     * Extract a sprite using its identification name.
     * @see resources/pacman_sheet.json
     * 
     * @param spriteName
     * @return the sub-image
     */
    public Image extractSprite(String spriteName) {
        int[] position = spriteData.get(spriteName);
        if (position == null) {
            System.out.println("Sprite " + spriteName + " not found in JSON data.");
            return null;
        }

        int x = position[0] * spriteWidth;
        int y = position[1] * spriteHeight;
        return createSubImage(x, y, spriteWidth, spriteHeight);
    }

    /**
     * Create the cropped sub-image sprite.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return the selected sprite
     */
    private Image createSubImage(int x, int y, int width, int height) {
        PixelReader reader = spriteSheet.getPixelReader();
        return new WritableImage(reader, x, y, width, height);
    }

    /**
     * Reads the JSON file.
     * 
     * @param jsonFilePath
     * @return 
     */
    private Map<String, int[]> readJsonFile(String jsonFilePath) {
        spriteData = new HashMap<>();
        try (FileReader reader = new FileReader(jsonFilePath)) {
            Gson gson = new Gson();

            Type mapType = new TypeToken<Map<String, int[]>>() {}.getType();
            spriteData = gson.fromJson(reader, mapType);
        } catch (IOException ex) {}
        return spriteData;
    }
}
