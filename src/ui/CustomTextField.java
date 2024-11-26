package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.BasicStroke;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.text.ParseException;

public class CustomTextField extends JFormattedTextField {
    public CustomTextField() {
        this(false);
    }

    public CustomTextField(boolean numberOnly) {
        super();
        setOpaque(false);
        setForeground(Color.WHITE);
        setBackground(Definitions.LEFT_PANEL);
        setCaretColor(Color.WHITE);
        setSelectedTextColor(Definitions.LEFT_PANEL);
        setSelectionColor(Color.WHITE);
        if (!numberOnly)
            return;
        AbstractFormatterFactory factory = new DefaultFormatterFactory() {
            @Override
            public AbstractFormatter getFormatter(JFormattedTextField source) {
                NumberFormat format = NumberFormat.getInstance();
                NumberFormatter formatter = new NumberFormatter(format) {
                    @Override
                    public Object stringToValue(String text) throws ParseException {
                        if (text.length() == 0)
                            return null;
                        return super.stringToValue(text);
                    }
                };
                formatter.setValueClass(Integer.class);
                formatter.setMinimum(1);
                formatter.setMaximum(10000);
                formatter.setAllowsInvalid(false);
                return formatter;
            }
        };
        setFormatterFactory(factory);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Util.setRH(g2);
        g2.setColor(getBackground());
        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
        super.paintComponent(g2);
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Util.setRH(g2);
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
    }

    private Shape shape;

    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        }
        return shape.contains(x, y);
    }
}
