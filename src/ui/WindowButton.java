package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.*;

public class WindowButton extends JPanel {
    public static class Group {
        private java.util.List<WindowButton> buttons = new ArrayList<>();
        public void add(WindowButton b) { buttons.add(b); b.buttonGroup = this; }
        public void add(WindowButton... buttons) {
            for(WindowButton b : buttons) add(b);
        }
        public void show() {
            for(WindowButton b : buttons) {
                b.showIcon = true;
                b.repaint();
            }
        }
        public void hide() {
            for(WindowButton b : buttons) {
                b.showIcon = false;
                b.repaint();
            }
        }
    }

    private static App app;
    public static void setApp(App app) { WindowButton.app = app; }

    public static final Color MAXIMIZE = new Color(39, 201, 63);
    public static final Color MINIMIZE = new Color(255, 189, 46);
    public static final Color CLOSE = new Color(255, 95, 86);
    public static final Util.TriConsumer<WindowButton, App, Graphics2D> DRAW_X = (button, app, g) -> {
        double centerX = button.getWidth() / 2 + 0.1;
        double centerY = button.getHeight() / 2 + 0.1;
        double xSize = 1.9;
        Stroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g.setStroke(stroke);
        Path2D.Double path = new Path2D.Double();
        path.moveTo(centerX - xSize, centerY - xSize);
        path.lineTo(centerX + xSize, centerY + xSize);
        path.moveTo(centerX + xSize, centerY - xSize);
        path.lineTo(centerX - xSize, centerY + xSize);
        g.draw(path);
    };
    public static final Util.TriConsumer<WindowButton, App, Graphics2D> DRAW_DASH = (button, app, g) -> {
        int centerX = button.getWidth() / 2;
        int centerY = button.getHeight() / 2;
        int dashSize = 2;
        Stroke stroke = new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g.setStroke(stroke);
        g.drawLine(centerX - dashSize, centerY, centerX + dashSize, centerY);
    };
    public static final Util.TriConsumer<WindowButton, App, Graphics2D> DRAW_EXPAND = (button, app, g) -> {
        double centerX = button.getWidth() / 2 + 0.1;
        double centerY = button.getHeight() / 2 + 0.1;
        double shift1 = 2.6;
        double shift2 = 1.3;
        
        if(!app.isMaximized()) {
            Path2D.Double tri1 = new Path2D.Double();
            Path2D.Double tri2 = new Path2D.Double();
            tri1.moveTo(centerX - shift1, centerY - shift1);
            tri1.lineTo(centerX - shift1, centerY + shift2);
            tri1.lineTo(centerX + shift2, centerY - shift1);
            tri2.moveTo(centerX + shift1, centerY + shift1);
            tri2.lineTo(centerX + shift1, centerY - shift2);
            tri2.lineTo(centerX - shift2, centerY + shift1);
            g.fill(tri1);
            g.fill(tri2);
        }
        else {
            Path2D.Double quad = new Path2D.Double();
            quad.moveTo(centerX, centerY - shift1 - 1.7);
            quad.lineTo(centerX - shift1 - 1.7, centerY);
            quad.lineTo(centerX + shift1 + 0.5, centerY);
            quad.lineTo(centerX, centerY + shift1 + 0.5);
            g.fill(quad);
        }
        
    };

    private Color primary;
    private Color secondary;
    private Color tertiary;
    private boolean showIcon = false;
    private boolean pressed = false;
    private boolean disabled = false;
    private Group buttonGroup;
    private Util.TriConsumer<WindowButton, App, Graphics2D> paint;
    public WindowButton(Color primaryColor, Util.TriConsumer<WindowButton, App, Graphics2D> paintFunc) {
        super();
        setOpaque(false);
        setSize(10, 10);
        primary = primaryColor;
        secondary = Util.darken(primaryColor, 0.6);
        tertiary = Util.lighten(primaryColor, 0.4);
        paint = paintFunc;

        Util.attachMouseAdapter(this, new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(buttonGroup == null) showIcon = true;
                else buttonGroup.show();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(buttonGroup == null) showIcon = false;
                else buttonGroup.hide();
            }
        });
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(disabled ? Color.GRAY : pressed ? tertiary : primary);
        g.fillArc(0, 0, getWidth(), getHeight(), 0, 360);
        if(disabled) return;
        g.setColor(secondary);
        if(showIcon) paint.accept(this, app, g);
    }

    public void disableButton() { disabled = true; }
    public void enableButton() { disabled = false; }
}
