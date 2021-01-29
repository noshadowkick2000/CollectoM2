package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import game.Board;

// TODO: Auto-generated Javadoc
/**
 * The Class CollectoNetworker.
 */
public abstract class CollectoNetworker {

	/** The out. */
	public BufferedWriter out;

	/** The in. */
	public BufferedReader in;

	/**
	 * Write message.
	 *
	 * @param msg the msg
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeMessage(String msg) throws IOException {
		out.write(msg);
		out.newLine();
		out.flush();
	}

	/**
	 * Await message.
	 *
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String awaitMessage() throws IOException {
		return in.readLine();
	}

	/**
	 * Move int to string.
	 *
	 * @param move the move
	 * @return the string
	 */
	public String moveIntToString(int[] move) {
		String moveMessage = Communications.M;
		for (int m : move) {
			moveMessage += Communications.DELIM + m;
		}
		return moveMessage;
	}

	// for convenience, this string array includes the MOVE at the start of the
	/**
	 * Move string to int.
	 *
	 * @param move the move
	 * @return the int[]
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
	 * Converts the grid of this Board to a form usable for the NEWGAME protocol
	 * sent by the server.
	 *
	 * @return 
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
