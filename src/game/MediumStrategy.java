package game;

import game.Board.Move;

/**
 * The Class MediumStrategy.
 */
public class MediumStrategy implements CollectoStrategy {
	
	/**
	 * Returns a move to be played on the passed Board from the Strategy
	 *
	 * @requires board != null.
	 * @param board: the Board on which to play the move.
	 * @return the move to be played as an Integer array
	 */
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
