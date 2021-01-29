package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class CollectoNetworker {
	
	public BufferedWriter out;
	public BufferedReader in;

	public void writeMessage(String msg) throws IOException {
		out.write(msg);
		out.newLine();
		out.flush();
	}

	public String awaitMessage() throws IOException {
		return in.readLine();
	}
	
	public String moveIntToString(int[] move) {
		String moveMessage = Communications.M;
		for (int m : move) {
			moveMessage += Communications.DELIM + m;
		}
		return moveMessage;
	}

	// for convenience, this string array includes the MOVE at the start of the
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
}
