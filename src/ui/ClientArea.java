package ui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientArea extends JPanel {
    private int leftPanelWidth = 300;
    private LeftPanel leftPanel = new LeftPanel(this);
    private CentralPanel centralPanel = new CentralPanel();

    public ClientArea() {
        super();
        setLayout(null);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize();
            }
        });
        onResize();
        add(leftPanel);
        add(centralPanel);
    }

    @Override
    public void paintComponent(Graphics g) {}

    public final void onResize() {
        leftPanel.setSize(leftPanelWidth, getHeight());
        leftPanel.onResize();
        centralPanel.setBounds(leftPanelWidth, 0, getWidth() - leftPanelWidth, getHeight());
        centralPanel.onResize();
    }

    public void loadConfig(Config config) {
        leftPanel.setPlayEnabled(true);
        centralPanel.setLoaded(true);
    }

    public void unloadConfig() {
        leftPanel.setPlayEnabled(false);
        centralPanel.setLoaded(false);
    }
}

class LeftPanel extends JPanel {
    private final JLabel collectionLabel;
    private final CustomButton addButton;
    private final CustomButton playButton;
    private final CollectionEntry.Group collectionGroup;

    private final ClientArea root;

    public LeftPanel(ClientArea root) {
        super();
        this.root = root; 
        setLayout(null);
        collectionLabel = new JLabel("Collection", SwingConstants.CENTER);
        collectionLabel.setBounds(5, 5, 90, 20);
        collectionLabel.setForeground(Color.WHITE);
        collectionLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        add(collectionLabel);
        collectionGroup = new CollectionEntry.Group(root);
        add(collectionGroup);
        addButton = new CustomButton(Color.BLACK, Definitions.ADD_BUTTON);
        addButton.setText("+New");
        addButton.addActionListener(e -> collectionGroup.addEntry());
        add(addButton);
        playButton = new CustomButton(Color.BLACK, new Color(200, 255, 50));
        playButton.setText("Play");
        playButton.setEnabled(false);
        add(playButton);
        
    }
    @Override
    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        g.setColor(Definitions.LEFT_PANEL);
        g.fillRect(0, 0, getWidth(), getHeight());
        
    }

    public void onResize() {
        addButton.setLocation(getWidth() - 5 - addButton.getWidth(), 5);
        collectionLabel.setLocation(collectionLabel.getX(), (addButton.getHeight() - collectionLabel.getHeight()) / 2 + 5);
        playButton.setBounds(5, getHeight() - playButton.getHeight() - 5, getWidth() - 10, playButton.getHeight());
        collectionGroup.setBounds(5, addButton.getHeight() + 10, getWidth() - 10, playButton.getY() - addButton.getY() - addButton.getHeight() - 10);
        collectionGroup.onResize();
    }

    public void setPlayEnabled(boolean b) {
        playButton.setEnabled(b);
    }
}

class CentralPanel extends JPanel {
    CentralPanel() {
        super();
        setOpaque(false);
    }

    private boolean loaded = false;
    public void setLoaded(boolean b) {
        loaded = b;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Util.setRH(g2d);
        g2d.setColor(Definitions.BACKGROUND);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if (loaded) return;
        String message = "Create or select a battle";
        Font font = new Font("SF Pro Display", Font.BOLD, 25);
        FontMetrics metrics = g.getFontMetrics(font);
        int h = metrics.getAscent() + metrics.getDescent();
        int y = (getHeight() + h) / 2;
        int w = metrics.stringWidth(message);
        int x = (getWidth() - w) / 2;
        g2d.setFont(font);
        g2d.setColor(Util.lighten(Definitions.DISABLED, 0.5));
        g2d.drawString(message, x, y - metrics.getDescent());
    }

    public void onResize() {
        repaint();
    }
    
}