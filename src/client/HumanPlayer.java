/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package client;

import game.Board;
import util.CollectoInterface;

/**
 * The Class HumanPlayer.
 */
public class HumanPlayer implements CollectoClientPlayer {

	/** Usage format for inputting moves to the console. */
	static private final String MOVE_FORMAT = "Select move to play, format is <firstMove>[second move]";

	/**
	 * Return the move to be played by the HumanPlayer on the passed Board. This
	 * method will block while waiting for the user to input the move. Method will
	 * not return unless valid move has been inputed.
	 * 
	 * @requires board != null.
	 * @param board: the Board on which the move is to be played
	 * @return an Integer array of size 1 or 2 containing the move to be played
	 */
	@Override
	public int[] getMove(Board board) {

		// TODO empty scanner in case player was typing something outside their turn

		CollectoInterface.showBoard(board);

		CollectoInterface.showMessage("It's your move");

		String[] args;
		int[] move;

		while (true) {
			args = CollectoInterface.requestInput(MOVE_FORMAT).split(" ");

			if (args[0].equals("hint")) {
				CollectoInterface.showPossibleMoves(board);
				continue;
			}

			move = new int[args.length];
			try {
				for (int i = 0; i < move.length; i++) {
					move[i] = Integer.parseInt(args[i]);
				}
			} catch (NumberFormatException e) {
				CollectoInterface.showMessage("Move format was incorrect");
			}

			if (board.isValidMove(move)) {
				break;
			} else {
				CollectoInterface.showMessage("Move was not valid, try again");
			}
		}
		return move;
	}
}
