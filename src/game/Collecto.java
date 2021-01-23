package game;

import java.util.List;

public abstract class Collecto {
	
	public enum Condition {
		VICTORY_PLAYER_ONE,
		VICTORY_PLAYER_TWO,
		DISCONNECT,
		DRAW
	}
	
	protected Board board;
	
	abstract public void startGame();
	
	abstract public List<COLOUR> makeMove(int[] move);
	
	abstract public void endGame(Condition endGameCondition);
}
