package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import game.Board;
import game.Collecto;
import game.EasyStrategy;
import game.HardStrategy;
import game.MediumStrategy;
import util.CollectoInterface;
import util.CollectoNetworker;
import util.Communications;

public class CollectoClient extends CollectoNetworker implements Runnable {

	private static final String USAGE = "Input server details with this format: <host-adress>_<port>";
	private static final String CHOOSE_HUMAN = "HUMAN";
	private static final String CHOOSE_AI_EASY = "AI EASY";
	private static final String CHOOSE_AI_MEDIUM = "AI MEDIUM";
	private static final String CHOOSE_AI_HARD = "AI HARD";
	public static final String[] PLAYER_OPTIONS = new String[] { CHOOSE_HUMAN, CHOOSE_AI_EASY, CHOOSE_AI_MEDIUM,
			CHOOSE_AI_HARD };

	private Socket sock;

	private static final int boardLength = Board.BOARD_SIZE * Board.BOARD_SIZE;

	static private final String GAME_SEPERATOR = "------------NEW GAME------------";
	static private final String LOBBY_USAGE = "You are waiting in the lobby commands are:";
	static private final String GAME_OVER = "Game over, you will be sent back to the lobby";
	static private final String HELP = "HELP";
	static private final String EX = "EXIT";
	static private final String[] LOBBY_COMMANDS = new String[] { Communications.Q, Communications.LS, HELP, EX };

	protected boolean gameAvailable = false;
	protected boolean inQueue = false;

	protected Thread lobbyThread;

	public Collecto game;

	private CollectoClientPlayer localPlayer;
	public boolean localPlayerTurn = false;
	public boolean localIsFirstPlayer;

	public String loginName;

	public void showWinner(String winnerName) {
		if (winnerName.equals(loginName)) {
			CollectoInterface
					.showMessage("Congrats you won with " + game.board.countPoints(localIsFirstPlayer) + " points");
		} else {
			CollectoInterface.showMessage("Good game, " + winnerName + " won with "
					+ game.board.countPoints(!localIsFirstPlayer) + " points");
		}
	}

	public void connectServer() throws IOException {
		handleInitialization();
		sock.close();
	}

	private void handleInitialization() {

		// Setup host adress and port
		while (true) {
			String[] args = CollectoInterface.requestInput(USAGE).split(" ");
			if (setConnection(args)) {
				break;
			}
		}

		// Setup player type and difficulty
		while (true) {
			CollectoInterface.showMessage("Enter client mode. Choices are: ");
			if (setClientPlayer(CollectoInterface.requestOption(PLAYER_OPTIONS))) {
				break;
			}
		}

		try {

			// Setup initial connection and execute hello protocol
			hello(CollectoInterface.requestInput("Enter client description"));

			// Setup login credentials and execute protocol
			while (true) {
				if (login(CollectoInterface.requestInput("Enter login name"))) {
					break;
				}
			}

			startLobby();
			while (true) {
				parseServerInput();
			}
		} catch (IOException e) {
			CollectoInterface.showMessage("Connection error: server disconnected");
		} catch (InvalidResponseException e) {
			CollectoInterface.showMessage("Error: unexpected response from server");
		}
	}

	public boolean setConnection(String[] host) {
		if (host.length != 2) {
			return false;
		}
		try {
			InetAddress hostAdress = InetAddress.getByName(host[0]);
			int port = Integer.parseInt(host[1]);

			// create and assign socket and in and out
			sock = new Socket(hostAdress, port);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			return true;
		} catch (IOException e) {
			CollectoInterface.showMessage(
					"Could not connect to server with adress '" + host[0] + "' and port '" + host[1] + "', try again");
			return false;
		}
	}

	public boolean setClientPlayer(String playerType) {
		if (playerType == null) {
			return false;
		}

		switch (playerType) {
		case CHOOSE_HUMAN:
			localPlayer = new HumanPlayer();
			return true;
		case CHOOSE_AI_EASY:
			localPlayer = new ComputerPlayer(new EasyStrategy());
			return true;
		case CHOOSE_AI_MEDIUM:
			localPlayer = new ComputerPlayer(new MediumStrategy());
			return true;
		case CHOOSE_AI_HARD:
			localPlayer = new ComputerPlayer(new HardStrategy());
			return true;
		default:
			return false;
		}
	}

	public void hello(String clientDescription) throws IOException, InvalidResponseException {
		writeMessage(Communications.H + Communications.DELIM + clientDescription);
		CollectoInterface.showMessage("Sent Hello");

		String[] serverHello = awaitMessage().split(Communications.DELIM);

		// server responds hello
		if (!serverHello[0].equals(Communications.H)) {
			throw new InvalidResponseException();
		}

		CollectoInterface.showMessage("Hello aknowledged");

		if (serverHello.length == 2) {
			CollectoInterface.showMessage("Connected to server with description: " + serverHello[1]);
		} else {
			CollectoInterface.showMessage("Connected to server without description");
		}
	}

	public boolean login(String loginName) throws IOException, InvalidResponseException {
		writeMessage(Communications.L + Communications.DELIM + loginName);

		switch (awaitMessage()) {
		case Communications.L:
			this.loginName = loginName;
			return true;
		case Communications.AL:
			CollectoInterface.showMessage("Login already taken, try another name");
			return false;
		default:
			throw new InvalidResponseException();
		}
	}

	public void run() {
		lobbyTUI();
	}

	public void parseServerInput() throws IOException {
		String[] args = awaitMessage().split(Communications.DELIM);

		switch (args[0]) {
		case Communications.LS:
			receiveList(args);
			break;
		case Communications.NG:
			newGame(args);
			break;
		case Communications.M:
			receiveMove(Board.moveStringToInt(args));
			break;
		case Communications.GO:
			gameOver(args);
			break;
		case Communications.ERR:
			CollectoInterface.showMessage("You made an illegal move, try again");
			break;
		}
	}

	public void startLobby() {
		lobbyThread = new Thread(this);
		lobbyThread.start();
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
					CollectoInterface.showBoard(game.board);
					return;
				}
				switch (lastInput) {
				case Communications.Q:
					queue();
					break;
				case Communications.LS:
					sendList();
					break;
				case HELP:
					help();
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

	public void queue() throws IOException {
		writeMessage(Communications.Q);
		inQueue = !inQueue;
		CollectoInterface.showMessage("Currently in queue: " + inQueue);
	}

	public void receiveList(String[] list) throws IOException {
		String connectedClients = "";
		for (int i = 1; i < list.length; i++) {
			connectedClients += list[i] + System.lineSeparator();
		}
		CollectoInterface.showMessage("Currently " + (list.length - 1) + " clients connected to server:"
				+ System.lineSeparator() + connectedClients);
	}

	public void sendList() throws IOException {
		writeMessage(Communications.LS);
	}

	public void receiveMove(int[] move) throws IOException {
		if (localPlayerTurn) {
			game.board.makeMove(move);
		} else {
			game.board.makeMove(move);
			if (game.board.noMovesLeft()) {
				return;
			}
			sendMove();
		}
		localPlayerTurn = !localPlayerTurn;
	}

	public void sendMove() throws IOException {
		try {
			lobbyThread.join();
		} catch (InterruptedException e) {
			CollectoInterface.showMessage("Error joining main thread");
		}

		int[] move = localPlayer.getMove(game.board);
		writeMessage(Board.moveIntToString(move));
	}

	public void newGame(String[] gridPlayers) throws IOException {

		int[] grid = new int[boardLength];
		for (int i = 0; i < grid.length; i++) {
			grid[i] = Integer.parseInt(gridPlayers[i + 1]);
		}

		String firstPlayer = gridPlayers[gridPlayers.length - 2];

		game = new Collecto();
		game.board = new Board(grid);

		gameAvailable = true;
		inQueue = false;

		CollectoInterface.showMessage("Game started, press enter to join the game");

		localIsFirstPlayer = firstPlayer.equals(loginName);

		if (localIsFirstPlayer) {
			localPlayerTurn = true;
			sendMove();
		} else {
			CollectoInterface.showMessage("Waiting for other player");
		}
	}

	public void gameOver(String[] condition) {

		switch (condition[1]) {
		case Communications.VICTORY:
			showWinner(condition[2]);
			break;
		case Communications.DRAW:
			CollectoInterface.showMessage("Game is a draw");
			break;
		case Communications.DISCONNECT:
			CollectoInterface.showMessage("You won, other player disconnected");
			break;
		}

		game.board = null;
		CollectoInterface.showMessage(GAME_OVER);
		gameAvailable = false;

		startLobby();
	}

	protected void help() {
		CollectoInterface.showMessage(LOBBY_USAGE);
	}

	public static void main(String[] args) {
		CollectoClient client = new CollectoClient();

		try {
			client.connectServer();
		} catch (IOException e) {
		}
	}
}
