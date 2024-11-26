package ui;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class CustomButton extends JPanel {
    private static Color fgDefault = new Color(0, 0, 0);
    private static Color bgDefault = new Color(230, 230, 230);
    private static final Cursor cursorForbidden = new Cursor(Cursor.DEFAULT_CURSOR);

    private JLabel label = new JLabel("", SwingConstants.CENTER);
    private Cursor cursorOnHover = new Cursor(Cursor.HAND_CURSOR);
    public void setHover(Cursor cursor) {
        cursorOnHover = cursor;
        setCursor(cursor);
    }


    private int borderRadius = 10;
    public void setRadius(int r) {
        borderRadius = r;
    }

    private Color background;
    private Color borderColor;
    private int borderWidth;
    private boolean disabled = false;
    private boolean hovered = false;
    private boolean clicked = false;
    public CustomButton() {
        this(fgDefault, bgDefault);
    }

    public CustomButton(Color fg, Color bg) {
        super();
        setLayout(new GridLayout(1, 1));
        setCursor(cursorOnHover);
        setSize(90, 35);
        label.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        label.setForeground(fg);
        add(label);
        Util.attachMouseAdapter(this, new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                clicked = false;
                repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (disabled) return;
                for (ActionListener listener : actionListeners) listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "clicked"));
                clicked = true;
                repaint();
            }

        });
        background = bg;
    }

    public void setText(String str) {
        label.setText(str);
    }

    @Override
    public void setEnabled(boolean enabled) {
        disabled = !enabled;
        if (disabled) setCursor(cursorForbidden);
        else setCursor(cursorOnHover);
        repaint();
    }

    @Override
    public void setBackground(Color bg) {
        background = bg;
    }

    public void setBorderColor(Color c) {
        borderColor = c;
    }

    public void setBorderWidth(int x) {
        borderWidth = x;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        Util.setRH(g);
        Color bg = disabled ? Definitions.DISABLED : 
            clicked ? Util.lighten(background, 0.15) : 
            hovered ? Util.lighten(background, 0.08) : background;
        if (borderWidth > 0) {
            g.setColor(borderColor);
            Util.fillRoundRect(g, 0, 0, getWidth(), getHeight(), borderRadius);
        }
        g.setColor(bg);
        Util.fillRoundRect(
            g, 
            borderWidth, 
            borderWidth, 
            getWidth() - 2 * borderWidth, 
            getWidth() - 2 * borderWidth, 
            borderRadius - borderWidth
        );
    }

    private java.util.List<ActionListener> actionListeners = new ArrayList<>();
    public void addActionListener(ActionListener h) {
        if (h != null) actionListeners.add(h);
    }

}