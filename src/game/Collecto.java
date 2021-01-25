package game;

import java.util.List;

public class Collecto {
	
	public enum Condition {
		VICTORY_PLAYER_ONE,
		VICTORY_PLAYER_TWO,
		DISCONNECT,
		DRAW
	}
	
	public Board board;
	
	public List<COLOUR> makeMove(int[] move) {
		return board.makeMove(move);
	}
}
