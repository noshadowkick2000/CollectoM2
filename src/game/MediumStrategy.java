package game;

import game.Board.Move;

public class MediumStrategy implements CollectoStrategy {

	@Override
	public int[] getMove(Board board) {
		
		int largestAmount = 0;
		int[] easyMove = null;
		for (Move m : board.getPossibleMoves()) {
			if (m.gainedBalls.size() > largestAmount) {
				largestAmount = m.gainedBalls.size();
				easyMove = m.move;
			}
		}
		
		return easyMove;
	}
}
