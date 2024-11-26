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
        for (int i = 0; i < monsters.length; i++) {
            monsters[i].player.setId(i);
        }
    }

    private int[] dice = new int[6];
    private int turn;
    private int current;
    private int entering;
    private int alive;
    private int winner;
    private int tokyo;
    private boolean restart = true;
    private boolean skip;

    private Map<String, Integer> record = new HashMap<>();
    private void addWin() {
        String name = monsters[winner].getName();
        Integer val = record.get(name);
        if (val == null) record.put(name, 1);
        else record.put(name, val + 1);
    }

    public void nextTurn() {
        if (remaining == 0) return;
        if (restart) {
            restart = false;
            for (Monster m : monsters)
                m.reset();
            current = (int) (Math.random() * monsters.length);
            entering = current;
            alive = monsters.length;
            tokyo = -1;
            turn = 1;
            winner = -1;
            skip = false;
        }
        if (entering == current && current > 1)
            tokyo = entering;
        Monster currentMonster = monsters[current];
        int[] healths = new int[monsters.length];
        int[] fames = new int[monsters.length];
        for (int i = 0; i < monsters.length; i++) {
            healths[i] = monsters[i].getHealth();
            fames[i] = monsters[i].getFame();
        }
        rollDice();
        for (int i = 0; i < 2; i++) {
            boolean[] rerolls = currentMonster.player.rerollDice(
                    turn,
                    current,
                    tokyo,
                    dice.clone(),
                    healths,
                    fames);
            rollDice(rerolls);
        }
        applyFame(tally(1), tally(2), tally(3));
        if (restart) {
            addWin();
            return;
        }
        applyHeal(tally(5));
        applyDamage(tally(6));
        if (restart) {
            addWin();
            return;
        }
        if (!skip && tally(4) > 2) {
            skip = true;
        }
        else {
            nextPlayer();
            skip = false;
        }
    }

    private void nextPlayer() {
        do {
            current = (current + 1) % monsters.length;
        } while (monsters[current].getHealth() == 0);
        turn++;
    }

    private void applyFame(int ones, int twos, int threes) {
        int total = 0;
        Monster currentMonster = monsters[current];
        if (ones > 3)
            total += ones - 2;
        if (twos > 3)
            total += twos - 1;
        if (threes > 3)
            total += threes - 1;
        if (total > 0)
            currentMonster.addFame(total);
        if (currentMonster.getFame() >= Monster.TARGET_FAME) {
            restart = true;
            winner = current;
        }
    }

    private void applyHeal(int n) {
        if (current == tokyo)
            return;
        Monster currentMonster = monsters[current];
        currentMonster.changeHealth(n);
    }

    private void applyDamage(int n) {
        if (n == 0) return;
        if (current == tokyo) {
            for (int i = 0; i < monsters.length; i++) {
                if (i == current || monsters[i].getHealth() == 0) continue;
                monsters[i].changeHealth(-n);
                if (monsters[i].getHealth() == 0) alive--;
            }
        }
        else {
            if (tokyo == -1) return;
            monsters[tokyo].changeHealth(-n);
            if (monsters[tokyo].getHealth() == 0) {
                alive--;
                if (alive == 1) {
                    restart = true;
                    winner = current;
                    return;
                }
                tokyo = current;
            }
            else {
                int[] healths = new int[monsters.length];
                int[] fames = new int[monsters.length];
                for (int i = 0; i < monsters.length; i++) {
                    healths[i] = monsters[i].getHealth();
                    fames[i] = monsters[i].getFame();
                }
                boolean leave = monsters[tokyo].player.leaveTokyo(turn, current, tokyo, dice.clone(), healths, fames);
                if (leave) {
                    tokyo = current;
                }
            }
        }
    }

    private void rollDice() {
        for (int i = 0; i < 6; i++) {
            dice[i] = (int) (Math.random() * 6);
        }
    }

    private void rollDice(boolean[] reroll) {
        for (int i = 0; i < 6; i++) {
            if (reroll[i])
                dice[i] = (int) (Math.random() * 6);
        }
    }

    private int tally(int n) {
        int total = 0;
        for (int i = 0; i < 6; i++) {
            if (dice[i] == n)
                total++;
        }
        return total;
    }
}
