package client;

import game.Board;
import game.CollectoStrategy;

/**
 * The Class ComputerPlayer.
 */
public class ComputerPlayer implements CollectoClientPlayer {

	/** The strategy to be used by the ComputerPlayer. */
	private CollectoStrategy strategy;

	/**
	 * Instantiates a new computer player with a given Strategy to use. The strategy
	 * determines what moves the ComputerPlayer will play
	 *
	 * @requires strategy != null.
	 * @param strategy: the strategy for the ComputerPlayer. Valid strategy classes
	 *                  include: EasyStrategy, MediumStrategy, and HardStrategy.
	 */
	public ComputerPlayer(CollectoStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Returns the move to be played by this ComputerPlayer.
	 * 
	 * @requires board != null.
	 * @param board: the Board on which the move will be played
	 * @return an Integer array of size 1 or 2, containing the move to be played.
	 */
	@Override
	public int[] getMove(Board board) {
		return strategy.getMove(board);
	}
}
