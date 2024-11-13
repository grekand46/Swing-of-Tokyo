package engine;

public class Monster {
    public static final int MAX_HEALTH = 12;
    public static final int TARGET_FAME = 20;

    private int health = 10;
    private int fame = 0;
    private String name;
    Player player;

    public Monster(String name, Player player) {
        this.name = name;
        this.player = player;
    }

    void changeHealth(int amount) {
        health = Math.max(0, Math.min(MAX_HEALTH, health + amount));
    }

    void addFame(int amount) {
        fame += amount;
        if (fame > TARGET_FAME) fame = TARGET_FAME;
    }

    void reset() {
        fame = 0;
        health = 10;
    }

    public String getName() {
        return name;
    }
    void setName(String name) {
        this.name = name;
    }
}
