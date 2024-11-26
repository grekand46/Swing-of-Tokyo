package ui;

import java.awt.*;
import java.net.URLClassLoader;

import javax.swing.*;
import javax.swing.border.Border;

import java.util.List;
import java.net.URL;
import java.io.File;

import engine.Game;
import engine.Monster;
import engine.Player;
import engine.PlayerNaive;

public class GameWindow extends JFrame {
    private JLabel[] status;
    public GameWindow(Config cfg) {
        super("King of Tokyo: " + cfg.getName());
        setSize(800, 600);
        int games = cfg.getGames();
        boolean pause = cfg.getPause();
        Monster[] monsters = cfg.getMonsters().stream().map(md -> {
            try {
                URLClassLoader loader = new URLClassLoader(new URL[] {
                    new File("./app-data/classes/" + md.getCls() + ".class").toURI().toURL()
                });
                Class<?> cls = loader.loadClass(md.getCls());
                loader.close();
                return new Monster(md.getName(), (Player) cls.getConstructor().newInstance());
            } catch (Exception e) {
                return new Monster(md.getName(), new PlayerNaive());
            }
        }).toArray(Monster[]::new);

        status = new JLabel[monsters.length + 1];
        Font f = new Font("SF Pro Display", Font.BOLD, 20);
        for (int i = 0; i < status.length; i++) {
            status[i] = new JLabel();
            status[i].setForeground(Color.WHITE);
            status[i].setFont(f);
        }
        setLayout(new BorderLayout());
        getContentPane().setBackground(Definitions.BACKGROUND);
        System.out.println(monsters.length);
        Game game = new Game(games, pause, monsters);
        Box box = new Box(BoxLayout.PAGE_AXIS);
        for (JLabel label : status) box.add(label);
        CustomButton next = new CustomButton();
        next.setText("Next");
        next.setPreferredSize(new Dimension(10000, 30));
        next.setBackground(new Color(255, 170, 97));
        getContentPane().add(next, BorderLayout.SOUTH);
        getContentPane().add(box, BorderLayout.CENTER);
        String[] disp = game.display();
        for (int i = 0; i < disp.length; i++) {
            status[i].setText(disp[i]);
        }
        if (pause) next.addActionListener(e -> {
            if (game.finished()) return;
            game.nextTurn();
            String[] disp2 = game.display();
            for (int i = 0; i < disp2.length; i++) {
                status[i].setText(disp2[i]);
            }
        });
        else {
            next.addActionListener(e -> {
                if (game.finished()) return;
                while (!game.finished()) {
                    game.nextTurn();
                }
                String[] disp2 = game.display();
                for (int i = 0; i < disp2.length; i++) {
                    status[i].setText(disp2[i]);
                }
            });
        }
    }
}
