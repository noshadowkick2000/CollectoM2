package game;

/**
 * The Interface CollectoStrategy.
 */
public interface CollectoStrategy {

	/**
	 * Returns a move to be played on the passed Board from the Strategy
	 *
	 * @requires board != null.
	 * @param board: the Board on which to play the move.
	 * @return the move to be played as an Integer array
	 */
	public int[] getMove(Board board);
}
