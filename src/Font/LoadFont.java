package Font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

public class LoadFont {

    public static Font customFont, customFont1;

    public static void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/Font/Outfit-Light.ttf"));
            customFont1 = Font.createFont(Font.TRUETYPE_FONT, new File("src/Font/LondrinaSketch-Regular.ttf"));

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            GraphicsEnvironment ge1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge1.registerFont(customFont1);
        } catch (IOException | FontFormatException e) {
            customFont = new Font("Arial", Font.PLAIN, 15);
            System.out.println("Arial Fold Applied");
        }
    }
}
