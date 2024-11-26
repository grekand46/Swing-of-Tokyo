package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MonsterEntry extends JPanel {
    static final int BORDER_WIDTH = 3;

    public static class Group extends JPanel {
        private CustomButton addButton = new CustomButton() {
            public void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                Util.setRH(g);
                super.paintComponent(g);
                g.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                int x = getWidth() / 2;
                int y = getHeight() / 2;
                g.setColor(Definitions.BACKGROUND);
                g.drawLine(x - 20, y, x + 20, y);
                g.drawLine(x, y - 20, x, y + 20);
            }
        };
        {
            Color c = Color.GRAY.darker();
            addButton.setBackground(c);
            addButton.setPreferredSize(new Dimension(200, 200));
            addButton.setRadius(10);
        }
        public Group() {
            WrapLayout layout = new WrapLayout(FlowLayout.CENTER);
            layout.setHgap(20);
            layout.setVgap(20);
            setLayout(layout);
            add(addButton);
        }
        public void add(MonsterEntry entry) {
            remove(addButton);
            super.add(entry);
            add(addButton);
        }
    }

    private MonsterData data;
    private JPanel structuredContainer = new JPanel(new BorderLayout());
    private JLabel nameLabel = new JLabel();
    public MonsterEntry(MonsterData data) {
        super();
        this.data = data;
        setLayout(null);
        Font f = new Font("SF Pro Display", Font.BOLD, 22);
        
        Util.clearBG(structuredContainer);
        add(structuredContainer);
        structuredContainer.add(nameLabel, BorderLayout.NORTH);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(f);
        setPreferredSize(new Dimension(200, 200));
        update();

        onResize();
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize();
            }
        });
    }

    public void onResize() {
        int width = BORDER_WIDTH + 6;
        structuredContainer.setBounds(
            width, 
            width, 
            getPreferredSize().width - 2 * width, 
            getPreferredSize().height - 2 * width
        );
    }

    public void update() {
        nameLabel.setText(data.getName());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Util.setRH(g2);
        g2.setColor(Color.GRAY.darker());
        Util.fillRoundRect(g2, 0, 0, getWidth(), getHeight(), 10);
        g2.setColor(Definitions.BACKGROUND);
        Util.fillRoundRect(g2, BORDER_WIDTH, BORDER_WIDTH, getWidth() - 2 * BORDER_WIDTH, getHeight() - 2 * BORDER_WIDTH, 10 - BORDER_WIDTH);
    }

}
