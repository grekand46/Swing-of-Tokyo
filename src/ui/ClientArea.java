package ui;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Flow;

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
        centralPanel.load(config);
        repaint();
    }

    public void unloadConfig() {
        leftPanel.setPlayEnabled(false);
        centralPanel.clear();
        repaint();
    }

    public void newGame() {
        centralPanel.newGame();
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
        playButton.addActionListener(e -> {
            root.newGame();
        });
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
    private JTextField gameField = new CustomTextField(true);
    private ToggleSwitch pauseToggle = new ToggleSwitch();
    private MonsterEntry.Group monsterLine = new MonsterEntry.Group(null);
    private BorderLayout containerLayout = new BorderLayout();
    private JPanel container = new JPanel(containerLayout);
    private JPanel box = new JPanel();

    CentralPanel() {
        super();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        Font f = new Font("SF Pro Display", Font.PLAIN, 20);
        setOpaque(false);
        JLabel gameLabel = new JLabel("Games: ");
        JLabel pauseLabel = new JLabel("Pause: ");
        JLabel monsterLabel = new JLabel("Monsters: ");
        JPanel gameLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pauseLine = new JPanel(new FlowLayout(FlowLayout.LEFT));

        gameField.setFont(f);
        gameField.setColumns(4);
        gameField.setHorizontalAlignment(SwingConstants.CENTER);
        gameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_ENTER) {
                    gameField.setFocusable(false);
                    gameField.setFocusable(true);
                    try {
                        int num = Integer.parseInt(gameField.getText().replace(",", ""));
                        internal.setGames(num);
                    }
                    catch (Exception ex) {
                        gameField.setText("1");
                    }
                }
            }
        });
        box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
        container.add(box, BorderLayout.NORTH);
        box.add(gameLine);
        box.add(pauseLine);
        {
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
            Util.clearBG(wrapper);
            wrapper.add(monsterLabel);
            box.add(wrapper);
        }
        container.add(monsterLine, BorderLayout.CENTER);
        gameLine.add(gameLabel);
        gameLine.add(gameField);
        gameLabel.setForeground(Color.WHITE);
        gameLabel.setFont(f.deriveFont(Font.BOLD));
        pauseLine.add(pauseLabel);
        pauseLine.add(pauseToggle);
        pauseLabel.setForeground(Color.WHITE);
        pauseLabel.setFont(f.deriveFont(Font.BOLD));
        monsterLabel.setForeground(Color.WHITE);
        monsterLabel.setFont(f.deriveFont(Font.BOLD));
        Util.clearBG(container, box, gameLine, pauseLine, monsterLine);
        pauseToggle.addActionListener(e -> {
            internal.setPause(pauseToggle.isEnabled());
        });
    }

    private boolean loaded = false;
    private Config internal;
    public void load(Config cfg) {
        loaded = true;
        internal = cfg;
        monsterLine.setRoot(cfg);
        gameField.setText("" + cfg.getGames());
        pauseToggle.setEnabled(cfg.getPause());
        monsterLine.removeAll();
        monsterLine.init();
        for (MonsterData data : cfg.getMonsters())
            monsterLine.add(new MonsterEntry(data));
        add(container);
        revalidate();
    }
    public void clear() {
        loaded = false;
        removeAll();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Util.setRH(g2d);
        g2d.setColor(Definitions.BACKGROUND);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if (!loaded) paintNone(g2d);
    }

    private void paintNone(Graphics2D g2d) {
        String message = "Create or select a battle";
        Font font = new Font("SF Pro Display", Font.BOLD, 25);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int h = metrics.getAscent() + metrics.getDescent();
        int y = (getHeight() + h) / 2;
        int w = metrics.stringWidth(message);
        int x = (getWidth() - w) / 2;
        g2d.setFont(font);
        g2d.setColor(Util.lighten(Definitions.DISABLED, 0.5));
        g2d.drawString(message, x, y - metrics.getDescent());
    }

    public void onResize() {
        Dimension size = new Dimension(getWidth(), getHeight());
        container.setPreferredSize(size);
        revalidate();
    }

    public void newGame() {
        GameWindow win = new GameWindow(internal);
        win.setVisible(true);
    }
    
}