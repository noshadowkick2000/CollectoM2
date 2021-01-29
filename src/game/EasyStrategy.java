/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package game;

import java.util.Random;

/**
 * The Class EasyStrategy.
 */
public class EasyStrategy implements CollectoStrategy {

	/**
	 * Returns a move to be played on the passed Board from the Strategy
	 *
	 * @requires board != null.
	 * @param board: the Board on which to play the move.
	 * @return the move to be played as an Integer array
	 */
	@Override
	public int[] getMove(Board board) {
		Random r = new Random();
		if (board.getPossibleMoves().size() == 0) {
			return null;
		}
		return board.getPossibleMoves().get(r.nextInt(board.getPossibleMoves().size())).move;
	}
}
