package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;

import javax.swing.JPanel;

import java.util.List;
import java.util.ArrayList;

public class ToggleSwitch extends JPanel {
    private List<ActionListener> listeners = new ArrayList<>();

    public ToggleSwitch() {
        setPreferredSize(new Dimension(50, 25));
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                enabled = !enabled;
                listeners.forEach(a -> a.actionPerformed(null));
                repaint();
            }
        });
    }

    private boolean enabled = true;
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        repaint();
    }

    public void addActionListener(ActionListener a) {
        listeners.add(a);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Util.setRH(g2);
        int pad = 4;
        int size = getHeight() - 2 * pad;
        Color bg = enabled ? new Color(100, 128, 25) : Color.GRAY.darker();
        g2.setColor(bg);
        Util.fillRoundRect(g2, 0, 0, getWidth(), getHeight(), getHeight() / 2);
        g2.setColor(Color.WHITE);
        g2.fillOval(enabled ? getWidth() - size - pad : pad, pad, size, size);
    }
}
