package client;

import game.Board;

/**
 * The Interface CollectoClientPlayer.
 */
public interface CollectoClientPlayer {

	/**
	 * Gets a move to be played on the Board passed in the parameter. Move should be
	 * a legal move;
	 *
	 * @param board: the Board on which the move is to be played.
	 * @return an Integer array of size 1 or 2 containing the moves to be played on the board.
	 */
	public int[] getMove(Board board);
}
