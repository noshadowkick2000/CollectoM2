package client;

import game.Board;
import game.CollectoStrategy;

public class ComputerPlayer implements CollectoClientPlayer {
	
	private CollectoStrategy strategy;
	
	public ComputerPlayer(CollectoStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public int[] getMove(Board board) {	
		return strategy.getMove(board);
	}
}
