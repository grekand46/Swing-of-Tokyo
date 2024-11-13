package ui;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;
import javax.swing.*;

public class App extends JFrame {
    private AppPanel panel;
    public AppPanel getBase() { return panel; }

    private ClientArea clientArea;

    private Font appFont;

    private WindowButton.Group buttonGroup;

    private Object mouseCapture = null;
    public Object getMouseCapture() { return mouseCapture; }
    public void setMouseCapture(Object obj) { mouseCapture = obj; } 

    private Rectangle savedBounds;
    private boolean maximized = false;
    public boolean isMaximized() { return maximized; }
    public void maximize() {
        savedBounds = this.getBounds();
        setExtendedState(MAXIMIZED_BOTH);
        maximized = true;
        onResize();
    }
    public void restore() {
        setExtendedState(NORMAL);
        maximized = false;
        setBounds(savedBounds);
        onResize();
    }
    public void minimize() {
        setExtendedState(ICONIFIED);
        maximized = false;
    }

    public App() {
        super("King of Tokyo");
        appFont = new Font("SF Pro Display", Font.PLAIN, 8);
        WindowButton.setApp(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 1));
        addWindowListener(new WindowListener() {
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {
                buttonGroup.hide();
                onResize();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowOpened(WindowEvent e) {}
        });
        {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = screenSize.width * 2 / 3;
            int height = screenSize.height * 2 / 3;
            setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        }
        panel = new AppPanel();
        MouseAdapter mouseAdapter = new DragListener(this);
        JLabel label = new JLabel("King of Tokyo", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        label.setFont(appFont.deriveFont(Font.BOLD, 13.0f));
        label.setPreferredSize(Util.stretch(label.getPreferredSize(), 1, 1, 0, 0));
        label.addMouseListener(mouseAdapter);
        label.addMouseMotionListener(mouseAdapter);
        label.setForeground(new Color(200, 200, 200));
        panel.getMain().setLayout(new BorderLayout());
        panel.getMain().add(label, BorderLayout.NORTH);
        clientArea = new ClientArea();
        panel.getMain().add(clientArea, BorderLayout.CENTER);

        setContentPane(panel);
        pack();
        setVisible(true);
    }

    private class AppPanel extends JPanel {
        private final int BORDER_R = 10;
        private final int BORDER_W = 2;
        public final int DECO_W = 6;

        private final JPanel[] decoration = new JPanel[8];
        private JPanel main = new JPanel();

        AppPanel() {
            super();
            this.setLayout(null);
            Dimension size = App.this.getSize();
            main.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            setSize(size);
            setPreferredSize(size);
            for(int i = 0; i < 8; i++) {
                decoration[i] = new JPanel();
                add(decoration[i]);
            }
            
            int[] cursors = {
                Cursor.NW_RESIZE_CURSOR,
                Cursor.N_RESIZE_CURSOR,
                Cursor.NE_RESIZE_CURSOR,
                Cursor.E_RESIZE_CURSOR,
                Cursor.W_RESIZE_CURSOR,
                Cursor.SW_RESIZE_CURSOR,
                Cursor.S_RESIZE_CURSOR,
                Cursor.SE_RESIZE_CURSOR
            };
            java.util.List<Function<App, Point>> anchorFuncs = new ArrayList<>();
            anchorFuncs.add((app) -> {
                Point p = app.getLocation();
                return new Point(p.x + app.getWidth(), p.y + app.getHeight());
            });
            anchorFuncs.add((app) -> {
                Point p = app.getLocation();
                return new Point(p.x, p.y + app.getHeight());
            });
            anchorFuncs.add((app) -> {
                Point p = app.getLocation();
                return new Point(p.x, p.y + app.getHeight());
            });
            anchorFuncs.add((app) -> {
                Point p = app.getLocation();
                return new Point(p.x + app.getWidth(), p.y);
            });
            anchorFuncs.add((app) -> app.getLocation());
            anchorFuncs.add((app) -> {
                Point p = app.getLocation();
                return new Point(p.x + app.getWidth(), p.y);
            });
            anchorFuncs.add((app) -> app.getLocation());
            anchorFuncs.add((app) -> app.getLocation());
            int[] xDirs = { -1, 0, 1, -1, 1, -1, 0, 1 };
            int[] yDirs = { -1, -1, -1, 0, 0, 1, 1, 1 };

            for(int i = 0; i < 8; i++)
                Util.attachMouseAdapter(
                    decoration[i], 
                    new WindowResizeHandler(
                        App.this, 
                        new Cursor(cursors[i]), 
                        anchorFuncs.get(i), 
                        xDirs[i],
                        yDirs[i]
                    )
                );
            
            int buttonOffset = 16;
            buttonGroup = new WindowButton.Group();
            WindowButton closeButton = new WindowButton(WindowButton.CLOSE, WindowButton.DRAW_X);
            WindowButton minimizeButton = new WindowButton(WindowButton.MINIMIZE, WindowButton.DRAW_DASH);
            WindowButton maximizeButton = new WindowButton(WindowButton.MAXIMIZE, WindowButton.DRAW_EXPAND);
            buttonGroup.add(closeButton, minimizeButton, maximizeButton);
            closeButton.setLocation(12, 12);
            minimizeButton.setLocation(12 + buttonOffset, 12);
            maximizeButton.setLocation(12 + 2 * buttonOffset, 12);
            Util.attachMouseAdapter(closeButton, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    App.this.dispose();
                }
            });
            Util.attachMouseAdapter(maximizeButton, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(!isMaximized()) {
                        maximize();
                        minimizeButton.disableButton();
                    }
                    else {
                        restore();
                        minimizeButton.enableButton();
                    }
                }
            });
            Util.attachMouseAdapter(minimizeButton, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(!isMaximized()) minimize();
                }
            });
            add(closeButton);
            add(minimizeButton);
            add(maximizeButton);

            main.setBackground(new Color(0, 0, 0, 0));
            add(main);
            onResize();
        }

        public void onResize() {
            final int width = App.this.getWidth();
            final int height = App.this.getHeight();
            int[] bx = { 0, DECO_W, width - DECO_W, 0, width - DECO_W, 0, DECO_W, width - DECO_W };
            int[] by = { 0, 0, 0, DECO_W, DECO_W, height - DECO_W, height - DECO_W, height - DECO_W };
            int[] bw = { DECO_W, width - 2 * DECO_W, DECO_W, DECO_W, DECO_W, DECO_W, width - 2 * DECO_W, DECO_W };
            int[] bh = { DECO_W, DECO_W, DECO_W, height - 2 * DECO_W, height - 2 * DECO_W, DECO_W, DECO_W, DECO_W };
            for(int i = 0; i < 8; i++) {
                decoration[i].setBounds(bx[i], by[i], bw[i], bh[i]);
                decoration[i].setBackground(new Color(0, 0, 0, 1));
            }
            main.setBounds(DECO_W, DECO_W, width - 2 * DECO_W, height - 2 * DECO_W);

        }

        @Override
        public void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics;
            Util.setRH(g);
            if(isMaximized()) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setColor(new Color(64, 64, 64)); // 48, 64, 89
            Util.fillRoundRect(g, 0, 0, getWidth(), getHeight(), BORDER_R);
            g.setColor(Definitions.BACKGROUND); // 29, 42, 64 // 17, 27, 43
            Util.fillRoundRect(g, BORDER_W, BORDER_W, getWidth() - 2 * BORDER_W, getHeight() - 2 * BORDER_W, BORDER_R - BORDER_W);
        }

        public JPanel getMain() {
            return main;
        }
    }

    public void onResize() {
        panel.onResize();
        clientArea.onResize();
    }

    private class DragListener extends MouseAdapter {
        private final JFrame frame;
        private Point lastMouse = null;

        public DragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            if(e.getButton() != MouseEvent.BUTTON1) return;
            lastMouse = null;
            mouseCapture = null;
        }

        public void mousePressed(MouseEvent e) {
            if(e.getButton() != MouseEvent.BUTTON1) return;
            lastMouse = e.getLocationOnScreen();
            mouseCapture = e.getSource();
        }

        public void mouseDragged(MouseEvent e) {
            if(lastMouse == null) return;
            Point currentMouse = e.getLocationOnScreen();
            frame.setLocation(frame.getX() + currentMouse.x - lastMouse.x, frame.getY() + currentMouse.y - lastMouse.y);
            lastMouse = currentMouse;
        }


    }
}
