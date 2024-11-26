package ui;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Random;

import javax.swing.JPanel;

public class Util {
    @FunctionalInterface
    public static interface TriConsumer<T, U, V> {
        public void accept(T t, U u, V v);
    }

    public static Color color(int r, int g, int b) {
        return new Color(Math.min(255, Math.max(0, r)), Math.min(255, Math.max(0, g)), Math.min(255, Math.max(0, b)));
    }

    public static Color darken(Color c, double value) {
        return color((int) (c.getRed() * value), (int) (c.getGreen() * value), (int) (c.getBlue() * value));
    }

    public static Color lighten(Color c, double value) {
        value = 1 - value;
        Color reverse = darken(new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()), value);
        return color(255 - reverse.getRed(), 255 - reverse.getGreen(), 255 - reverse.getBlue());
    }

    public static void fillRoundRect(Graphics2D g, int x, int y, int width, int height, int r) {
        final int d = r * 2;
        g.fillRect(x + r, y, width - d, height);
        g.fillRect(x, y + r, width, height - d);
        g.fillArc(x, y, d, d, 90, 90);
        g.fillArc(x + width - d, y, d, d, 0, 90);
        g.fillArc(x, y + height - d, d, d, 180, 90);
        g.fillArc(x + width - d, y + height - d, d, d, 270, 90);
    }

    public static void drawRoundRect(Graphics2D g, int x, int y, int width, int height, int r) {
        final int d = r * 2;
        g.drawLine(x + r, y, x + width - r, y);
        g.drawLine(x + r, y + width, x + width - r, y + width);
        g.drawLine(x, y + r, x, y + width - r);
        g.drawLine(x + width, y + r, x + width, y + width - r);
        g.drawArc(x, y, d, d, 90, 90);
        g.drawArc(x + width - d, y, d, d, 0, 90);
        g.drawArc(x, y + height - d, d, d, 180, 90);
        g.drawArc(x + width - d, y + height - d, d, d, 270, 90);
    }

    public static Dimension stretch(Dimension d, double kx, double ky, double dx, double dy) {
        double w = d.getWidth();
        double h = d.getHeight();
        return new Dimension((int) (w * kx + dx), (int) (h * ky + dy));
    }
    public static void attachMouseAdapter(Component component, MouseAdapter adapter) {
        component.addMouseListener(adapter);
        component.addMouseMotionListener(adapter);
        component.addMouseWheelListener(adapter);
    }

    public static void setRH(Graphics2D g) {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);
    }

    private static final String alphaNum = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private static final Random rand = new Random(System.currentTimeMillis());
    public static String randomAlphaNum(int len) {
        StringBuilder res = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int index = rand.nextInt(alphaNum.length());
            res.append(alphaNum.charAt(index));
        }
        return res.toString();
    }

    public static String fileNameNoExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return name;
        return name.substring(0, dotIndex);
    }

    public static void clearBG(javax.swing.JPanel... panels) {
        for (javax.swing.JPanel panel : panels) {
            panel.setOpaque(false);
            panel.setBackground(new Color(0, 0, 0, 0));
        }
    }

}
