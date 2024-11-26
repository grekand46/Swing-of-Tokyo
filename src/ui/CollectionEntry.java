package ui;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.*;
import javax.swing.*;

public class CollectionEntry extends JPanel {
    public static class Group extends JPanel {
        private static int entryHeight = 30;
        public static int getEntryHeight() { return entryHeight; }
        private int scrollBarPadding = 5;
        private int scrollBarWidth = 8;

        private final java.util.List<CollectionEntry> entries = new ArrayList<>();
        private final Map<String, CollectionEntry> entryMap = new HashMap<>();

        private ClientArea root;
        public Group(ClientArea root) {
            super();
            this.root = root;
            setOpaque(false);
            setLayout(null);
            onResize();
            init();
            for (Config cfg : Config.all()) {
                CollectionEntry entry = new CollectionEntry(cfg);
                addEntry(entry);
            }
        }

        private void init() {
            Util.attachMouseAdapter(this, new MouseAdapter() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (scrollDiff == 0) return;
                    scrollY += (int) (20 * e.getPreciseWheelRotation());
                    onResize();
                }
            });
        }

        public void addEntry() {
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                String name = "Unnamed " + i;
                if (entryMap.containsKey(name)) continue;
                CollectionEntry entry = new CollectionEntry(Config.create(name));
                entry.setAutoEdit(true);
                addEntry(entry);
                return;
            }
        }

        private void addEntry(CollectionEntry entry) {
            entry.group = this;
            add(entry);
            entries.add(entry);
            entryMap.put(entry.config.getName(), entry);
            onResize();
        }

        public void removeEntry(CollectionEntry entry) {
            remove(entry);
            entries.remove(entry);
            entryMap.remove(entry.config.getName());
            if (entry == selected) root.unloadConfig();
            selected = null;
            onResize();
        }

        private CollectionEntry selected = null;
        public void select(CollectionEntry entry) {
            if (selected != null) selected.selected = false;
            if (editing != entry && editing != null) editing.endEdit(true);
            entry.selected = true;
            selected = entry;

            root.loadConfig(entry.config);
        }

        private void renameEntry(CollectionEntry entry, String name) {
            name = name.stripTrailing();
            if (entryMap.get(name) != null) return;
            entryMap.remove(entry.config.getName());
            entry.config.setName(name);
            entryMap.put(name, entry);
        }

        private CollectionEntry editing;
        private void editNotify(CollectionEntry entry) {
            if (editing != null) editing.endEdit();
            select(entry);
            editing = entry;
        }

        public final void onResize() {
            int diff = entries.size() * entryHeight - getHeight();
            scrollDiff = Math.max(0, diff);
            scrollY = Math.min(scrollDiff, Math.max(0, scrollY));
            for (int i = 0; i < entries.size(); i++) {
                CollectionEntry entry = entries.get(i);
                entry.setBounds(0, i * entryHeight - scrollY, entryWidth(), entryHeight);
                entry.onResize();
            }
            repaint();
        }

        private int scrollY = 0;
        private int scrollDiff = 0;

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            Util.setRH(g2d);
            if (scrollDiff == 0) return;
            int h = getHeight();
            int scrollBarHeight = (int) ((double) h * h / (h + scrollDiff));
            int scrollBarOffset = (int) ((double) scrollY * h / (h + scrollDiff));
            g2d.setColor(Definitions.SCROLL_BAR);
            Util.fillRoundRect(
                g2d, 
                getWidth() - scrollBarWidth - scrollBarPadding, 
                scrollBarOffset, 
                scrollBarWidth, 
                scrollBarHeight, 
                scrollBarWidth / 2
            );
        }

        int entryWidth() {
            return scrollDiff > 0 ? getWidth() - scrollBarWidth - 2 * scrollBarPadding : getWidth();
        }
    }

    private boolean highlighted = false;
    private boolean selected = false;
    private Group group = null;
    private CustomButton deleteButton;
    private CustomButton renameButton;
    private Config config;
    public CollectionEntry(Config config) {
        super();
        setLayout(null);
        this.config = config;
        deleteButton = new CustomButton(Color.BLACK, Definitions.LEFT_PANEL) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Util.setRH(g2d);
                g2d.setColor(Definitions.DISABLED);
                g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                int mid = getWidth() / 2;
                g2d.drawLine(mid + 3, mid + 3, mid - 3, mid - 3);
                g2d.drawLine(mid - 3, mid + 3, mid + 3, mid - 3);
            }
        };
        deleteButton.addActionListener(e -> {
            group.removeEntry(this);
            config.delete();
        });
        deleteButton.setHover(new Cursor(Cursor.DEFAULT_CURSOR));
        deleteButton.setRadius(5);
        add(deleteButton);
        renameButton = new CustomButton(Color.BLACK, Definitions.LEFT_PANEL) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Util.setRH(g2d);
                g2d.setColor(Definitions.DISABLED);
                int mid = getWidth() / 2;
                Path2D path = new Path2D.Double();
                int ext = 6;
                int thk = 3;
                int crv = 1;
                int tip = 2;
                path.moveTo(mid - ext, mid + ext);
                path.lineTo(mid - ext, mid + ext - thk);
                path.lineTo(mid, mid - thk);
                path.lineTo(mid + thk, mid);
                path.lineTo(mid - ext + thk, mid + ext);
                g2d.fill(path);
                path.moveTo(mid + crv, mid - thk - crv);
                path.lineTo(mid + crv + tip, mid - crv - tip - thk);
                path.lineTo(mid + crv + tip + thk, mid - crv - tip);
                path.lineTo(mid + crv + thk, mid - crv);
                g2d.fill(path);
            }
        };
        renameButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        renameButton.setRadius(5);
        renameButton.addActionListener(e -> edit());
        add(renameButton);
        textbox.setForeground(Color.WHITE);
        textbox.setBackground(Definitions.LEFT_PANEL);
        textbox.setCaretColor(Color.WHITE);
        textbox.setSelectedTextColor(Definitions.LEFT_PANEL);
        textbox.setSelectionColor(Color.WHITE);
        textbox.setFont(titleFont);

        textbox.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        textbox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_ENTER:
                        endEdit();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        endEdit(true);
                        break;
                }
            }
        });
        init();
    }

    private void init() {
        Util.attachMouseAdapter(this, new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                highlighted = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                highlighted = false;
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (group == null) return;
                group.dispatchEvent(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                group.select(CollectionEntry.this);
                group.onResize();
            }
        });
    }

    private boolean autoEdit = false;
    public void setAutoEdit(boolean b) {
        autoEdit = b;
    } 

    private final Font titleFont = new Font("SF Pro Display", Font.PLAIN, 16);
    private final Font iconFont = new Font("Bakbak One", Font.PLAIN, 14);
    private int titleX = 0;
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Util.setRH(g2);
        Color bg = Definitions.LEFT_PANEL;
        bg = selected ? Util.lighten(bg, 0.1) : highlighted ? Util.lighten(bg, 0.05) : bg;
        g2.setColor(bg);
        deleteButton.setBackground(bg);
        renameButton.setBackground(bg);
        Util.fillRoundRect(g2, 0, 0, getWidth(), getHeight(), 5);
        String icon = "VS.";
        String title = config.getName();
        
        FontMetrics fm = g2.getFontMetrics(iconFont);
        g2.setColor(Definitions.ICON);
        g2.setFont(iconFont);
        int h = fm.getAscent() + fm.getDescent();
        int y = (getHeight() + h) / 2;
        int iconWidth = fm.stringWidth(icon);
        g2.drawString(icon, 10, y - fm.getDescent());
        fm = g2.getFontMetrics(titleFont);
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        if (!editing) {
            h = fm.getAscent() + fm.getDescent();
            y = (getHeight() + h) / 2;
            g2.drawString(title, 15 + iconWidth, y - fm.getDescent());
        }
        titleX = 15 + iconWidth;
        if (autoEdit) {
            autoEdit = false;
            group.editNotify(this);
            edit(false);
        }
    }

    public void onResize() {
        int size = getHeight() - 10;
        deleteButton.setBounds(getWidth() - size - 5, 5, size, size);
        renameButton.setBounds(getWidth() - 2 * size - 10, 5, size, size);
    }

    private final JTextField textbox = new CustomTextField();
    private boolean editing = false;
    private void edit() { edit(true); }
    private void edit(boolean repaint) {
        if (editing) return;
        group.editNotify(this);
        editing = true;
        textbox.setText(config.getName());
        textbox.setBounds(titleX, 3, 100, getHeight() - 6);
        add(textbox);
        textbox.grabFocus();
        textbox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                endEdit();
            }
        });
        if (repaint) repaint();
    }
    private void endEdit() { endEdit(false); }
    private void endEdit(boolean cancel) {
        if (!editing) return;
        editing = false;
        textbox.setFocusable(false);
        textbox.setFocusable(true);
        remove(textbox);
        if (!cancel) group.renameEntry(this, textbox.getText());
        repaint();
    }
}