package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import game.Board;
import game.Board.Move;
import game.Collecto;
import game.EasyStrategy;
import game.HardStrategy;
import game.MediumStrategy;
import util.Communications;

public class CollectoClient extends Collecto {

	private static final String USAGE = "Start Client with arguments [host-adress][port]";
	private static final String SHOW_MOVES = "Possible moves are: ";
	private static final String CHOOSE_HUMAN = "HUMAN";
	private static final String CHOOSE_AI_EASY = "AI EASY";
	private static final String CHOOSE_AI_MEDIUM = "AI MEDIUM";
	private static final String CHOOSE_AI_HARD = "AI HARD";
	private static final String[] PLAYER_OPTIONS = new String[] { CHOOSE_HUMAN, CHOOSE_AI_EASY, CHOOSE_AI_MEDIUM,
			CHOOSE_AI_HARD };

	private static final String CONSOLE_INDICATOR = "> ";
	private static final String INPUT_INDICATOR = ": ";
	private static final String TAB = "    ";

	private BufferedReader in;
	private BufferedWriter out;

	private CollectoClientPlayer localPlayer;
	private boolean localPlayerTurn = false;

	private String loginName;

	public void connectServer(String[] args) throws IOException {
		// get arguments
		InetAddress hostAdress = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);

		// create and assign socket and in and out
		Socket sock = new Socket(hostAdress, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

		handleInitialization();

		sock.close();
	}

	public void setupGame(int[] board, String secondPlayer) throws IOException {
		this.board = new Board(board);

		localPlayer.startGame();

		showMessage("Game started, press enter to join the game");

		if (!secondPlayer.equals(loginName)) {
			localPlayerTurn = true;
			localPlayer.sendServerMove(localPlayer.getMove());
		} else {
			showMessage("Waiting for other player");
		}
	}

	public void showWinner(String winnerName) {
		if (winnerName.equals(loginName)) {
			showMessage("Congrats you won");
		} else {
			showMessage("Good game, " + winnerName + " won");
		}
	}

	public void cleanUpGame() {
		board = null;
		localPlayer.endGame();
	}

	public void requestMove(int[] move) throws IOException {
		if (localPlayerTurn) {
			board.makeMove(move);
		} else {
			board.makeMove(move);
			if (board.noMovesLeft()) {
				return;
			}
			localPlayer.sendServerMove(localPlayer.getMove());
		}
		localPlayerTurn = !localPlayerTurn;
	}

	public void handleInitialization() {
		Scanner scanner = new Scanner(System.in);

		setClientPlayer(scanner);

		try {

			setClientLogin(scanner);

			localPlayer.startLobby();
			while (true) {
				localPlayer.parseServerInput();
			}

		} catch (IOException e) {
			showMessage("Connection error: server disconnected");
		} catch (InvalidResponseException e) {
			showMessage("Error: unexpected response from server");
		}
	}

	private void setClientPlayer(Scanner scanner) {
		boolean modeChosen = false;

		while (!modeChosen) {
			showMessage("Enter client mode. Choices are: ");
			String playerType = requestOption(PLAYER_OPTIONS, scanner);

			if (playerType == null)
				continue;

			switch (playerType) {
			case CHOOSE_HUMAN:
				localPlayer = new HumanPlayer(this, scanner);
				modeChosen = true;
				break;
			case CHOOSE_AI_EASY:
				localPlayer = new ComputerPlayer(this, scanner, new EasyStrategy());
				modeChosen = true;
				break;
			case CHOOSE_AI_MEDIUM:
				localPlayer = new ComputerPlayer(this, scanner, new MediumStrategy());
				modeChosen = true;
				break;
			case CHOOSE_AI_HARD:
				localPlayer = new ComputerPlayer(this, scanner, new HardStrategy());
				modeChosen = true;
				break;
			}
		}
	}

	private void setClientLogin(Scanner scanner) throws IOException, InvalidResponseException {
		// hello server
		writeMessage(Communications.H + Communications.DELIM + requestInput("Enter client description", scanner));
		showMessage("Sent Hello");

		String[] serverHello = awaitMessage().split(Communications.DELIM);

		// server responds hello
		if (!serverHello[0].equals(Communications.H)) {
			throw new InvalidResponseException();
		}
		showMessage("Hello aknowledged");

		// attempt login server
		boolean loggedIn = false;
		while (!loggedIn) {
			writeMessage(
					Communications.L + Communications.DELIM + (loginName = requestInput("Enter login name", scanner)));

			switch (awaitMessage()) {
			case Communications.L:
				loggedIn = true;
				break;
			case Communications.AL:
				showMessage("Login already taken, try another name");
				break;
			default:
				throw new InvalidResponseException();
			}
		}
		if (serverHello.length == 2) {
			showMessage("Logged onto server with description: " + serverHello[1]);
		} else {
			showMessage("Logged onto server without description");
		}
	}

	public void showBoard() {
		showMessage(System.lineSeparator() + board.toString());
	}

	public void showPossibleMoves() {
		String moves = System.lineSeparator();
		for (Move m : board.getPossibleMoves()) {
			moves += TAB + m.toString() + System.lineSeparator();
		}
		showMessage(SHOW_MOVES + moves);
	}

	public static void showMessage(String msg) {
		System.out.println(CONSOLE_INDICATOR + msg);
	}

	public static void showInput() {
		System.out.print(INPUT_INDICATOR);
	}

	public static void showOptions(String[] options) {
		int counter = 0;
		for (String option : options) {
			System.out.println(TAB + counter++ + ": " + option);
		}
	}

	public static String requestOption(String[] options, Scanner scanner) {
		showOptions(options);
		int index;
		try {
			showInput();
			index = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			return "";
		}
		return options[index];
	}

	public static String requestInput(String msg, Scanner scanner) {
		showMessage(msg);
		showInput();
		return scanner.nextLine();
	}

	public void writeMessage(String msg) throws IOException {
		out.write(msg);
		out.newLine();
		out.flush();
	}

	public String awaitMessage() throws IOException {
		return in.readLine();
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			showMessage(USAGE);
			System.exit(-1);
		}

		CollectoClient client = new CollectoClient();

		try {
			client.connectServer(args);
		} catch (IOException e) {
			showMessage("Could not connect to the server with given arguments");
			showMessage(USAGE);
		}
	}
}
