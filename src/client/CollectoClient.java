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

public class CollectoClient extends CollectoNetworker {

	private static final String USAGE = "Start Client with arguments [host-adress][port]";
	private static final String CHOOSE_HUMAN = "HUMAN";
	private static final String CHOOSE_AI_EASY = "AI EASY";
	private static final String CHOOSE_AI_MEDIUM = "AI MEDIUM";
	private static final String CHOOSE_AI_HARD = "AI HARD";
	private static final String[] PLAYER_OPTIONS = new String[] { CHOOSE_HUMAN, CHOOSE_AI_EASY, CHOOSE_AI_MEDIUM,
			CHOOSE_AI_HARD };

	private Socket sock;
	
	public Collecto game;

	private CollectoClientPlayer localPlayer;
	private boolean localPlayerTurn = false;

	private String loginName;

	public void setupGame(int[] board, String secondPlayer) throws IOException {
		game = new Collecto();
		game.board = new Board(board);

		localPlayer.startGame();

		CollectoInterface.showMessage("Game started, press enter to join the game");

		if (!secondPlayer.equals(loginName)) {
			localPlayerTurn = true;
			localPlayer.sendServerMove(localPlayer.getMove());
		} else {
			CollectoInterface.showMessage("Waiting for other player");
		}
	}

	public void showWinner(String winnerName) {
		if (winnerName.equals(loginName)) {
			CollectoInterface.showMessage("Congrats you won");
		} else {
			CollectoInterface.showMessage("Good game, " + winnerName + " won");
		}
	}

	public void cleanUpGame() {
		game.board = null;
		localPlayer.endGame();
	}

	public void requestMove(int[] move) throws IOException {
		if (localPlayerTurn) {
			game.board.makeMove(move);
		} else {
			game.board.makeMove(move);
			if (game.board.noMovesLeft()) {
				return;
			}
			localPlayer.sendServerMove(localPlayer.getMove());
		}
		localPlayerTurn = !localPlayerTurn;
	}

	public void connectServer() throws IOException {
		handleInitialization();
		sock.close();
	}

	public void handleInitialization() {

		setConnection();

		setClientPlayer();

		try {

			setClientLogin();

			localPlayer.startLobby();
			while (true) {
				localPlayer.parseServerInput();
			}
		} catch (IOException e) {
			CollectoInterface.showMessage("Connection error: server disconnected");
		} catch (InvalidResponseException e) {
			CollectoInterface.showMessage("Error: unexpected response from server");
		}
	}

	private void setClientPlayer() {
		boolean modeChosen = false;

		while (!modeChosen) {
			CollectoInterface.showMessage("Enter client mode. Choices are: ");
			String playerType = CollectoInterface.requestOption(PLAYER_OPTIONS);

			if (playerType == null)
				continue;

			switch (playerType) {
			case CHOOSE_HUMAN:
				localPlayer = new HumanPlayer(this);
				modeChosen = true;
				break;
			case CHOOSE_AI_EASY:
				localPlayer = new ComputerPlayer(this, new EasyStrategy());
				modeChosen = true;
				break;
			case CHOOSE_AI_MEDIUM:
				localPlayer = new ComputerPlayer(this, new MediumStrategy());
				modeChosen = true;
				break;
			case CHOOSE_AI_HARD:
				localPlayer = new ComputerPlayer(this, new HardStrategy());
				modeChosen = true;
				break;
			}
		}
	}

	private void setConnection() {
		String[] args = null;
		while (true) {
			args = CollectoInterface.requestInput(USAGE).split(" ");
			if (args.length != 2) {
				continue;
			}
			try {
				InetAddress hostAdress = InetAddress.getByName(args[0]);
				int port = Integer.parseInt(args[1]);

				// create and assign socket and in and out
				sock = new Socket(hostAdress, port);
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				return;
			} catch (IOException e) {
				CollectoInterface.showMessage("Could not connect to server with adress '" + args[0] + "' and port '" + args[1]
						+ "', try again");
			}

		}
	}

	private void setClientLogin() throws IOException, InvalidResponseException {
		// hello server
		writeMessage(Communications.H + Communications.DELIM + CollectoInterface.requestInput("Enter client description"));
		CollectoInterface.showMessage("Sent Hello");

		String[] serverHello = awaitMessage().split(Communications.DELIM);

		// server responds hello
		if (!serverHello[0].equals(Communications.H)) {
			throw new InvalidResponseException();
		}
		CollectoInterface.showMessage("Hello aknowledged");

		// attempt login server
		boolean loggedIn = false;
		while (!loggedIn) {
			writeMessage(
					Communications.L + Communications.DELIM + (loginName = CollectoInterface.requestInput("Enter login name")));

			switch (awaitMessage()) {
			case Communications.L:
				loggedIn = true;
				break;
			case Communications.AL:
				CollectoInterface.showMessage("Login already taken, try another name");
				break;
			default:
				throw new InvalidResponseException();
			}
		}
		if (serverHello.length == 2) {
			CollectoInterface.showMessage("Logged onto server with description: " + serverHello[1]);
		} else {
			CollectoInterface.showMessage("Logged onto server without description");
		}
	}

	public void showBoard() {
		CollectoInterface.showMessage(System.lineSeparator() + game.board.toString());
	}

	

	public static void main(String[] args) {
		CollectoClient client = new CollectoClient();

		try {
			client.connectServer();
		} catch (IOException e) {
		}
	}
}
