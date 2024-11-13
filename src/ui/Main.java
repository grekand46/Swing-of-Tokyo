package ui;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        String osName = System.getProperty("os.name");
        FontManager.init();
        SwingUtilities.invokeLater(DefaultFrame::new);
        /*
        if (osName.contains("Linux")) SwingUtilities.invokeLater(DefaultFrame::new);
        else SwingUtilities.invokeLater(App::new);
        */
    }
}
