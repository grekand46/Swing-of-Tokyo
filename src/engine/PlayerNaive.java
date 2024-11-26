package engine;

public class PlayerNaive extends Player {
    @Override
    public boolean[] rerollDice(
        int currentTurn,
        int currentPlayer,
        int inTokyo,
        int[] dice, 
        int[] playerHealths,
        int[] playerFames
    ) {
        // Rerolls dice that are not 6s
        boolean[] rerolls = new boolean[6];
        for (int i = 0; i < dice.length; i++) {
            if (dice[i] != 6) rerolls[i] = true;
        }
        return rerolls;
    }
    
    @Override
    public boolean leaveTokyo(
        int currentTurn,
        int currentPlayer,
        int inTokyo,
        int[] dice, 
        int[] playerHealths,
        int[] playerFames
    ) {
        return (playerHealths[getId()] == 1);
    }
}