/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
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

/**
 * The Class CollectoClient.
 */
public class CollectoClient extends CollectoNetworker implements Runnable {

	/** Usage for inputting host. */
	private static final String USAGE = "Input server details with this format: <host-adress>_<port>";

	private static final String CHOOSE_HUMAN = "HUMAN";
	private static final String CHOOSE_AI_EASY = "AI EASY";
	private static final String CHOOSE_AI_MEDIUM = "AI MEDIUM";
	private static final String CHOOSE_AI_HARD = "AI HARD";

	/**
	 * PLAYER_OPTIONS holds the possible options for the user to choose as local
	 * player.
	 */
	public static final String[] PLAYER_OPTIONS = new String[] { CHOOSE_HUMAN, CHOOSE_AI_EASY, CHOOSE_AI_MEDIUM,
			CHOOSE_AI_HARD };

	/** Usage and notification for being in the lobby. */
	static private final String LOBBY_USAGE = "You are waiting in the lobby commands are:";

	static private final String HELP = "HELP";
	static private final String EX = "EXIT";

	/**
	 * LOBBY_COMMANDS holds the possible options/commands available to the user in
	 * the lobby.
	 */
	static private final String[] LOBBY_COMMANDS = new String[] { Communications.Q, Communications.LS, HELP, EX };

	/**
	 * sock is shared over the whole CollectoClient to enable closing of the sock in
	 * separate execution paths.
	 */
	private Socket sock;

	static private final String GAME_SEPERATOR = "------------NEW GAME------------";

	/**
	 * gameAvailable is used to track whether a NEWGAME protocol has been sent to
	 * the client. This way the lobby loop knows when to close.
	 */
	protected boolean gameAvailable = false;

	/**
	 * inQueue tracks whether the client is in a queue, since the server does not
	 * send a reply to QUEUE.
	 */
	protected boolean inQueue = false;

	/**
	 * The lobby thread. Used to join the lobbyThread with the main Thread handling
	 * the protocols while in a game.
	 */
	protected Thread lobbyThread;

	/** The game. */
	public Collecto game;

	/** The local player. */
	private CollectoClientPlayer localPlayer;

	/** The local player turn. */
	public boolean localPlayerTurn = false;

	/** The local is first player. */
	public boolean localIsFirstPlayer;

	/** The login name. */
	public String loginName;

	/**
	 * Displays the winner of the current local game.
	 *
	 * @requires game != null, game.board != null, game.board.noMovesLeft(),
	 *           loginName != null.
	 * @param winnerName, contains the name of the winner sent by the server.
	 */
	public void showWinner(String winnerName) {
		if (winnerName.equals(loginName)) {
			CollectoInterface
					.showMessage("Congrats you won with " + game.board.countPoints(localIsFirstPlayer) + " points");
		} else {
			CollectoInterface.showMessage("Good game, " + winnerName + " won with "
					+ game.board.countPoints(!localIsFirstPlayer) + " points");
		}
	}

	/**
	 * Starts the program and the initialization sequence.
	 * 
	 * @ensures sock.close() before returning.
	 */
	public void connectServer() {
		handleInitialization();
		try {
			sock.close();
		} catch (IOException e) {
			CollectoInterface.showMessage("Could not close socket");
		}
	}

	/**
	 * Handle initialization. Sequentially calls the setConnection(),
	 * setClietPlayer(), hello() and login(), using the input from the console as
	 * the parameters.
	 */
	private void handleInitialization() {

		// Setup host adress and port
		while (!setConnection(CollectoInterface.requestInput(USAGE).split(" ")))
			;

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
			while (!login(CollectoInterface.requestInput("Enter login name")))
				;

			startLobby();
			while (true) {
				parseServerInput(awaitMessage().split(Communications.DELIM));
			}
		} catch (IOException e) {
			CollectoInterface.showMessage("Connection error: server disconnected");
		} catch (InvalidResponseException e) {
			CollectoInterface.showMessage("Error: unexpected response from server");
		}
	}

	/**
	 * Connects the client to the server and initializes sock, in, and out.
	 *
	 * @requires host != null.
	 * @ensures sock != null, in != null, out != null if successful.
	 * @param host: 2 dimensional String array, with host[0] being the host address
	 *              of the host in numerical form and host[1] being the port of the
	 *              host through which to communicate.
	 * @return true, if successful, else false.
	 */
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

	/**
	 * Sets the type of CollectoClientPlayer to be used on this client, using
	 * playerType.
	 *
	 * @ensures localPlayer != null.
	 * @param playerType: should be a String inside of PLAYER_OPTIONS.
	 * @return true if successful and playerType != null, else false.
	 */
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

	/**
	 * Start initialization client-server connection with the HELLO protocol.
	 * 
	 * @requires out != nul, clientDescription != null.
	 * @param clientDescription: the client description.
	 * @throws IOException              Signals that an I/O exception has occurred.
	 *                                  Generally indicates that the connection has
	 *                                  closed.
	 * @throws InvalidResponseException Signals that a the server has sent this
	 *                                  client a message which is not part of the
	 *                                  known protocols.
	 */
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

	/**
	 * Complete initialization client-server connection with the LOGIN protocol.
	 * 
	 * @requires out != null, loginName != null.
	 * @param loginName the user name with which to log on to the server.
	 * @return true if successful and loginName is not an existing name on the
	 *         server.
	 * @throws IOException              Signals that an I/O exception has occurred.
	 *                                  Generally indicates that the connection has
	 *                                  closed.
	 * @throws InvalidResponseException Signals that a the server has sent this
	 *                                  client a message which is not part of the
	 *                                  known protocols.
	 */
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

	/**
	 * Runnable method, starts lobbyTUI().
	 */
	public void run() {
		lobbyTUI();
	}

	/**
	 * Activates the methods associated with the following server protocols: LIST,
	 * NEWGAME, MOVE, GAMEOVER, and ERROR.
	 *
	 * @requires args != null, args.length > 0.
	 * @param args: the full message from the server in array form.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that the connection has closed.
	 */
	public void parseServerInput(String[] args) throws IOException {

		switch (args[0]) {
		case Communications.LS:
			receiveList(args);
			break;
		case Communications.NG:
			newGame(args);
			break;
		case Communications.M:
			receiveMove(moveStringToInt(args));
			break;
		case Communications.GO:
			gameOver(args);
			break;
		case Communications.ERR:
			CollectoInterface.showMessage("You made an illegal move, try again");
			break;
		}
	}

	/**
	 * Start a new thread running the lobbyTUI() method.
	 */
	public void startLobby() {
		lobbyThread = new Thread(this);
		lobbyThread.start();
	}

	/**
	 * Read input from the console and call the method corresponding to any valid
	 * commands. This method will block until input has been provided.
	 */
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
					exit();
				default:
					break;
				}
			}
		} catch (IOException e) {
			CollectoInterface.showMessage("Connection to server lost");
		}
	}

	/**
	 * Implements the QUEUE protocol and sends a request to the server to queue.
	 *
	 * @requires out != null.
	 * @ensures inQueue = !inQueue.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that the connection has closed.
	 */
	public void queue() throws IOException {
		writeMessage(Communications.Q);
		inQueue = !inQueue;
		CollectoInterface.showMessage("Currently in queue: " + inQueue);
	}

	/**
	 * Implements the LIST protocol and should be called when LIST is received from
	 * the server. Reads the values of the list and prints them in human readable
	 * form
	 *
	 * @requires out != null, list != null, list.length > 1.
	 * @param list: the full message from the server containing the LIST message and
	 *              the names of all connected clients.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that the connection has closed.
	 */
	public void receiveList(String[] list) throws IOException {
		String connectedClients = "";
		for (int i = 1; i < list.length; i++) {
			connectedClients += list[i] + System.lineSeparator();
		}
		CollectoInterface.showMessage("Currently " + (list.length - 1) + " clients connected to server:"
				+ System.lineSeparator() + connectedClients);
	}

	/**
	 * Implements the LIST protocol and sends a request to the server to send back a
	 * list of connected clients.
	 *
	 * @requires out != null.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that the connection has closed.
	 */
	public void sendList() throws IOException {
		writeMessage(Communications.LS);
	}

	/**
	 * Implements the MOVE protocol and is called when the server has sent a MOVE
	 * message to make a move on the local game. Will call sendMove() if it's the
	 * local player's turn.
	 *
	 * @requires game != null, game.board != null.
	 * @ensures localPlayerTurn = !localPlayerTurn.
	 * @param move: the move to be made on the board as an integer array.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that the connection has closed.
	 */
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

	/**
	 * Implements the MOVE protocol and sends a message to the server containing the
	 * move to be played by the local player. This move is returned from calling
	 * getMove() from the localPlayer
	 *
	 * @requires lobbyThread != null, out != null, localPlayer != null, game !=
	 *           null, game.board != null.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that the connection has closed.
	 */
	public void sendMove() throws IOException {

		// By waiting for the lobby thread, we ensure that the user closes the lobby
		// by pressing enter, before the game starts, this way the lobby prints and game
		// prints do not interfere with each other.
		try {
			lobbyThread.join();
		} catch (InterruptedException e) {
			CollectoInterface.showMessage("Error joining main thread");
		}

		int[] move = localPlayer.getMove(game.board);
		CollectoInterface.showMessage(Board.moveToReadableString(move));
		writeMessage(moveIntToString(move));
	}

	/**
	 * Implements the NEWGAME protocol and is called when NEWGAME is received. The
	 * parameters of the NEWGAME protocol, which contains the layout of the board,
	 * are passed through the parameter and used to initialize the local game
	 *
	 * @requires gridPlayers != null, gridPlayer.length == 52
	 * @ensures game != null, game.board != null, gameAvailable = true, inQueue =
	 *          false, localIsFirstPlayer = firstPlater.equals(loginName).
	 * @param gridPlayers: full message from the server containing the MOVE
	 *                     protocol, the board state, and the players in their
	 *                     playing order
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates that the connection has closed.
	 */
	public void newGame(String[] gridPlayers) throws IOException {

		int[] grid = new int[Board.BOARD_LENGTH];
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

	/**
	 * Implements the GAMEOVER protocol and is called when GAMEOVER is received.
	 * Will cleanup the local game and start a new lobby TUI.
	 *
	 * @requires out != null, condition.length > 1.
	 * @ensures game = null, game.board = null, gameAvailable = false.
	 * @param condition: the full message from the server containing the GAMEOVER
	 *                   protocol, the win condition and possibly the winner's user
	 *                   name.
	 */
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
		game = null;
		CollectoInterface.showMessage("Game over, you will be sent back to the lobby");
		gameAvailable = false;

		startLobby();
	}

	/**
	 * Show the reminder for the lobby.
	 */
	protected void help() {
		CollectoInterface.showMessage(LOBBY_USAGE);
	}

	/**
	 * Closes the program.
	 */
	public void exit() {
		CollectoInterface.showMessage("Shutting down");
		System.exit(0);
	}

	/**
	 * The main method.
	 *
	 * @param args the parameters
	 */
	public static void main(String[] args) {
		(new CollectoClient()).connectServer();
	}
}
