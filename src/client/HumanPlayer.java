package client;

import util.CollectoInterface;

public class HumanPlayer extends CollectoClientPlayer {

	static private final String MOVE_FORMAT = "Select move to play, format is <firstMove>[second move]";
	static private final String YOUR_MOVE = "It's your move";

	public HumanPlayer(CollectoClient client) {
		super(client);
	}

	@Override
	public int[] getMove() {

		try {
			lobbyThread.join();
		} catch (InterruptedException e) {
			CollectoInterface.showMessage("Error joining main thread");
		}

		// TODO empty scanner in case player was typing something outside their turn

		client.showBoard();

		CollectoInterface.showMessage(YOUR_MOVE);

		String[] args;
		int[] move;

		while (true) {
			args = CollectoInterface.requestInput(MOVE_FORMAT).split(" ");

			if (args[0].equals("hint")) {
				CollectoInterface.showPossibleMoves(client.game.board);
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

			if (client.game.board.isValidMove(move)) {
				break;
			} else {
				CollectoInterface.showMessage("Move was not valid, try again");
			}
		}
		return move;
	}
}
