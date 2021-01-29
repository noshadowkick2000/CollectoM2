/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import game.Board;

/**
 * The Class CollectoNetworker.
 */
public abstract class CollectoNetworker {

	/** The out. */
	public BufferedWriter out;

	/** The in. */
	public BufferedReader in;

	/**
	 * Writes the passed message to the socket output.
	 *
	 * @requires msg != null, out != null.
	 * @param msg: the message to be sent.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that socket has disconnected.
	 */
	public void writeMessage(String msg) throws IOException {
		out.write(msg);
		out.newLine();
		out.flush();
	}

	/**
	 * Awaits single line from the socket input and returns it. This method may
	 * block while waiting for input from the socket.
	 *
	 * @requires in != null.
	 * @return a String containing one line of a message from the socket input.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that socket has disconnected.
	 */
	public String awaitMessage() throws IOException {
		return in.readLine();
	}

	/**
	 * Converts the passed Integer array to a single String to use in the MOVE
	 * protocol.
	 *
	 * @requires move != null.
	 * @param move: an Integer array adhering to the move format defined in
	 *              Board.Move.move
	 * @return a String containing the move with each move being separated by a
	 *         Communications.DELIM, as per the MOVE protocol.
	 */
	public String moveIntToString(int[] move) {
		String moveMessage = Communications.M;
		for (int m : move) {
			moveMessage += Communications.DELIM + m;
		}
		return moveMessage;
	}

	/**
	 * Converts the passed move String array containing the full MOVE protocol
	 * message into an Integer array containing said move.
	 *
	 * @requires move != null.
	 * @param move: a String containing the move with each move being separated by a
	 *              Communications.DELIM, as per the MOVE protocol.
	 * @return an Integer array adhering to the move format defined in
	 *         Board.Move.move
	 */
	// protocol
	public int[] moveStringToInt(String[] move) {

		if (move.length == 3) {
			// make double move
			return new int[] { Integer.parseInt(move[1]), Integer.parseInt(move[2]) };
		} else if (move.length == 2) {
			// make single move
			return new int[] { Integer.parseInt(move[1]) };
		}
		return null;
	}

	/**
	 * Converts the grid of the passed Board to a form usable for the NEWGAME
	 * protocol sent by the server.
	 *
	 * @param board: the board from which to read the grid.
	 * @return a String containing all of the grids of the passed board, per the
	 *         NEWGAME protocol.
	 */
	static public String toCommunicationString(Board board) {
		String boardString = "";
		for (int i = 0; i < board.grid.length; i++) {
			boardString += board.grid[i].getValue();
			if (i == board.grid.length - 1) {
				continue;
			}
			boardString += Communications.DELIM;
		}
		return boardString;
	}
}
