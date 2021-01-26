package client;

import java.util.Scanner;

public class HumanPlayer extends CollectoClientPlayer {

	static private final String MOVE_FORMAT = "Select move to play, format is <firstMove>[second move]";
	static private final String YOUR_MOVE = "It's your move";

	public HumanPlayer(CollectoClient client, Scanner scanner) {
		super(client, scanner);
	}

	@Override
	public int[] getMove() {

		try {
			lobbyThread.join();
		} catch (InterruptedException e) {
			CollectoClient.showMessage("Error joining main thread");
		}

		// TODO empty scanner in case player was typing something outside their turn

		client.showBoard();

		CollectoClient.showMessage(YOUR_MOVE);

		String[] args;
		int[] move;

		while (true) {
			args = CollectoClient.requestInput(MOVE_FORMAT, scanner).split(" ");

			if (args[0].equals("hint")) {
				client.showPossibleMoves();
				continue;
			}

			move = new int[args.length];
			try {
				for (int i = 0; i < move.length; i++) {
					move[i] = Integer.parseInt(args[i]);
				}
			} catch (NumberFormatException e) {
				CollectoClient.showMessage("Move format was incorrect");
			}

			if (client.board.isValidMove(move)) {
				break;
			} else {
				CollectoClient.showMessage("Move was not valid, try again");
			}
		}
		return move;
	}
}
