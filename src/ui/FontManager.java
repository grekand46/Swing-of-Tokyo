package ui;
import java.awt.*;
import java.util.*;

public class FontManager {
    private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static Map<String, Font> internalMap = new HashMap<>();
    private static String[] fonts = {
        "SFPRODISPLAYBLACKITALIC.OTF",
        "SFPRODISPLAYBOLD.OTF",
        "SFPRODISPLAYHEAVYITALIC.OTF",
        "SFPRODISPLAYLIGHTITALIC.OTF",
        "SFPRODISPLAYMEDIUM.OTF",
        "SFPRODISPLAYREGULAR.OTF",
        "SFPRODISPLAYSEMIBOLDITALIC.OTF",
        "SFPRODISPLAYTHINITALIC.OTF",
        "SFPRODISPLAYULTRALIGHTITALIC.OTF",
        "BakbakOne-Regular.ttf"
    };

    private static String[] names = {
        "SF Pro Display Black Italic",
        "SF Pro Display Bold",
        "SF Pro Display Heavy Italic",
        "SF Pro Display Light Italic",
        "SF Pro Display Medium",
        "SF Pro Display Regular",
        "SF Pro Display Semibold Italic",
        "SF Pro Display Thin Italic",
        "SF Pro Display Ultralight Italic",
        "Bakbak One"
    };

    public static void init() {
        try {
            int index = 0;
            for (String fontName : fonts) {
                Font font = Font.createFont(
                    Font.TRUETYPE_FONT, 
                    Main.class.getResourceAsStream("../resources/" + fontName)
                );
                internalMap.put(names[index], font);
                ge.registerFont(font);
                index++;
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    public static Font get(String name) {
        return internalMap.get(name);
    }
}
