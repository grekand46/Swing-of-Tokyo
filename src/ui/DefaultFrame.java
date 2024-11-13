package ui;
import java.awt.*;
import javax.swing.*;

public class DefaultFrame extends JFrame {
    private final ClientArea panel;

    public DefaultFrame() {
        super("King of Tokyo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        panel = new ClientArea();
        setBackground(Definitions.BACKGROUND);
        setContentPane(panel);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screen.width * 2 / 3;
        int h = screen.height * 2 / 3;
        setBounds((screen.width - w) / 2, (screen.height - h) / 2, w, h);
        
        setVisible(true);
    }

    public void onResize() {
        panel.onResize();
    }
}