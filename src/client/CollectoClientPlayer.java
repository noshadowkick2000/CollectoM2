package client;

import java.io.IOException;

import util.CollectoInterface;
import util.Communications;

public abstract class CollectoClientPlayer implements Runnable {

	private static final int boardLength = game.Board.BOARD_SIZE * game.Board.BOARD_SIZE;

	static private final String GAME_SEPERATOR = "------------NEW GAME------------";
	static private final String LOBBY_USAGE = "You are waiting in the lobby commands are:";
	static private final String GAME_OVER = "Game over, you will be sent back to the lobby";
	static private final String HELP = "HELP";
	static private final String EX = "EXIT";
	static private final String[] LOBBY_COMMANDS = new String[] { Communications.Q, Communications.LS, HELP, EX };

	protected CollectoClient client;
	protected Thread lobbyThread;

	protected boolean gameAvailable = false;
	protected boolean inQueue = false;

	public CollectoClientPlayer(CollectoClient client) {
		this.client = client;
	}

	public void run() {
		lobbyTUI();
	}

	public abstract int[] getMove();

	public void parseServerInput() throws IOException {
		String[] args = client.awaitMessage().split(Communications.DELIM);

		switch (args[0]) {
		case Communications.LS:
			String connectedClients = "";
			for (int i = 1; i < args.length; i++) {
				connectedClients += args[i] + System.lineSeparator();
			}
			CollectoInterface.showMessage("Currently " + (args.length - 1) + " clients connected to server:"
					+ System.lineSeparator() + connectedClients);
			break;
		case Communications.NG:
			int[] grid = new int[boardLength];
			for (int i = 0; i < grid.length; i++) {
				grid[i] = Integer.parseInt(args[i + 1]);
			}
			client.setupGame(grid, args[args.length - 1]);
			break;
		case Communications.M:
			int[] move = new int[args.length - 1];
			for (int i = 0; i < move.length; i++) {
				move[i] = Integer.parseInt(args[i + 1]);
			}
			client.requestMove(move);
		case Communications.GO:
			switch (args[1]) {
			case Communications.VICTORY:
				client.showWinner(args[2]);
				client.cleanUpGame();
				startLobby();
				break;
			case Communications.DRAW:
				CollectoInterface.showMessage("Game is a draw");
				client.cleanUpGame();
				startLobby();
				break;
			case Communications.DISCONNECT:
				CollectoInterface.showMessage("You won, other player disconnected");
				client.cleanUpGame();
				startLobby();
				break;
			}
			break;
		case Communications.ERR:
			CollectoInterface.showMessage("You made an illegal move, try again");
			break;
		}
	}

	public void startGame() {
		gameAvailable = true;
		inQueue = false;
	}

	public void endGame() {
		CollectoInterface.showMessage(GAME_OVER);
		gameAvailable = false;
	}

	public void startLobby() {
		lobbyThread = new Thread(this);
		lobbyThread.start();
	}

	protected void sendServerMove(int[] move) throws IOException {
		String moveMessage = Communications.M;
		for (int m : move) {
			moveMessage += Communications.DELIM + m;
		}
		client.writeMessage(moveMessage);
	}

	private void lobbyTUI() {
		try {
			CollectoInterface.showMessage(LOBBY_USAGE);
			String lastInput;

			// lobby loop
			while (true) {
				lastInput = CollectoInterface.requestOption(LOBBY_COMMANDS);
				if (gameAvailable) {
					CollectoInterface.showMessage(GAME_SEPERATOR);
					client.showBoard();
					return;
				}
				switch (lastInput) {
				case Communications.Q:
					switchQueue();
					break;
				case Communications.LS:
					requestPlayerList();
					break;
				case HELP:
					CollectoInterface.showMessage(LOBBY_USAGE);
					break;
				case EX:
					CollectoInterface.showMessage("Shutting down");
					System.exit(0);
				default:
					break;
				}
			}
		} catch (IOException e) {
			CollectoInterface.showMessage("Connection to server lost");
		}
	}

	protected void requestPlayerList() throws IOException {
		client.writeMessage(Communications.LS);
	}

	protected void switchQueue() throws IOException {
		client.writeMessage(Communications.Q);
		inQueue = !inQueue;
		CollectoInterface.showMessage("Currently in queue: " + inQueue);
	}
}
