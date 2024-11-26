package ui;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Config.saveAll();
            }

        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize();
            }
        });
        
        setVisible(true);
    }

    public void onResize() {
        panel.onResize();
    }
}