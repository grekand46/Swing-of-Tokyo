package engine;

import java.util.*;

public class Game {
    private int remaining;
    private final boolean pause;
    private final Monster[] monsters;
    public Game(int games, boolean pause, Monster[] monsters) {
        remaining = games;
        this.pause = pause;
        this.monsters = Arrays.copyOf(monsters, monsters.length);
    }

    private int current;
    private int tokyo;
    private void runOnce() {}

    public Map<Monster, Integer> res;
}
