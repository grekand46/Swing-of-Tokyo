package engine;

// This is what Humans and Bots implement
public abstract class Player {
    private int id = -1;
    
    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    
    abstract public boolean[] rerollDice(
        int currentTurn,
        int currentPlayer,
        int inTokyo,
        int[] dice, 
        int[] playerHealths,
        int[] playerFames
    );
    
    abstract public boolean leaveTokyo(
        int currentTurn,
        int currentPlayer,
        int inTokyo,
        int[] dice, 
        int[] playerHealths,
        int[] playerFames
    );
    
}