package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import util.CollectoInterface;
import util.CollectoNetworker;
import util.Communications;

/**
 * The Class CollectoClientHandler.
 */
public class CollectoClientHandler extends CollectoNetworker implements Runnable {

	/**
	 * Enum State is used to track the current state of the client handler. This is
	 * to prevent having to use multiple booleans.
	 */
	private enum State {

		/** The uninitialized. */
		UNINITIALIZED,
		/** The no username. */
		NO_USERNAME,
		/** The initialized. */
		INITIALIZED,
		/** The in game. */
		IN_GAME
	}

	/**
	 * The server associated with this client handler, used to communicate with the
	 * server.
	 */
	private CollectoServer server;

	/**
	 * The CollectoServerGame associated with this client handler, used to
	 * communicate with the game and make moves. This field is empty unless the
	 * client handler has been assigned to a game.
	 */
	private CollectoServerGame game;

	/**
	 * The user name of the client connected to this client handler. Is empty until
	 * the LOGIN protocol has been succesfully executed.
	 */
	private String name = "";

	/** The current state of this client handler. */
	private State state = State.UNINITIALIZED;

	// Public methods
	// ------------------------------------------------------------------------------------------

	/**
	 * Instantiates a new collecto client handler.
	 *
	 * @param sock:        socket connected to the client
	 * @param server:      the server associated with this client handler
	 * @param description: the description of the server associated with this client
	 *                     handler
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	public CollectoClientHandler(Socket sock, CollectoServer server) throws IOException {
		// this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		this.server = server;
	}

	/**
	 * Run implementation for Runnable Interface, will call handleInput() which
	 * handles the incoming messages to this client handler.
	 */
	@Override
	public void run() {
		handleInput();
	}

	/**
	 * Gets the user name of the connected client.
	 *
	 * @return the name of the connected client.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sends the NEWGAME protocol to the client containing the state of the board
	 * and the player order and names.
	 *
	 * @param board: the Board on which will be played.
	 * @param p1     the user name of player 1.
	 * @param p2     the user name of player 2.
	 * @param game:  the CollectoServerGame instance which handles the game logic.
	 */
	public void newGame(String board, String p1, String p2, CollectoServerGame game) {
		try {
			this.game = game;
			state = State.IN_GAME;
			writeMessage(Communications.NG + Communications.DELIM + board + Communications.DELIM + p1
					+ Communications.DELIM + p2);
		} catch (IOException e) {
			disconnect();
		}
	}

	/**
	 * Sends the ERROR protocol to the connected client. This is called when an
	 * invalid move has been sent to this client handler or when a move has been
	 * sent while it is not tis client handler's turn in the game.
	 *
	 * @param description: the description of the error
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	public void showError(String description) throws IOException {
		writeMessage(Communications.ERR + Communications.DELIM + description);
	}

	// Private methods
	// -----------------------------------------------------------------------------------------

	/**
	 * Called when the connection to the client is lost. Removes all references to
	 * this client from the server and game if currently in a game.
	 */
	private void disconnect() {
		showMessage("Client disconnected, client handler aborting");
		server.removeClient(this);
		if (state.equals(State.IN_GAME))
			game.disconnectClient(this);
	}

	/**
	 * Reads the input from the socket input untill the socket closes. This method
	 * will block untill a new message has been sent to this client handler.
	 */
	private void handleInput() {
		String input;
		try {
			while ((input = awaitMessage()) != null) {
				parseCommand(input);
			}
		} catch (IOException e) {
			disconnect();
		}
	}

	/**
	 * Switches the input passed through the parameter depending on the current
	 * state of the client handler. If it is still uninitialized, it will only read
	 * HELLO and LOGIN protocols, else it will only read the other protocols.
	 *
	 * @param input: the message received from the client.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	private void parseCommand(String input) throws IOException {
		String[] commands = input.split(Communications.DELIM);

		if (state.equals(State.UNINITIALIZED) || state.equals(State.NO_USERNAME)) {
			// contains at least hello and description
			parseInitializationCommands(commands);
		} else {
			parseInitializedCommands(commands);
		}
	}

	/**
	 * Reads the passed argument and replies to protocol messages of HELLO and
	 * LOGIN.
	 *
	 * @param args: the message received from the client split by the
	 *              Communication.DELIM
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	private void parseInitializationCommands(String[] args) throws IOException {
		if (args.length > 1) {
			if (state.equals(State.UNINITIALIZED)) {
				// HELLO RESPONSE
				if (args[0].equals(Communications.H)) {
					hello(server.description);
				}
			} else if (state.equals(State.NO_USERNAME)) {
				// LOGIN RESPONSE
				if (args[0].equals(Communications.L)) {
					if (server.addClient(this, args[1])) {
						login(args[1]);
					} else {
						alreadyLoggedin(args[1]);
					}
				}
			}
		}
	}

	/**
	 * Implements the HELLO protocol and sends a HELLO back to the connected client.
	 * This method should be called when a HELLO message has been received from the
	 * client. Changes the state of this client handler.
	 *
	 * @param description: the description of the server
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	private void hello(String description) throws IOException {
		showMessage("Client with description: " + description + " connected to Server");
		state = State.NO_USERNAME;
		writeMessage(Communications.H + Communications.DELIM + description);
	}

	/**
	 * Implements the LOGIN protocol and sends a LOGIN confirmation message to the
	 * connected client. This method should only be called after
	 * CollectoServer.addClient() has been called to confirm that no existing user
	 * has the same name. Changes the state of this client handler to allow for
	 * reading initialized protocols. Sets the name variable of this client.
	 *
	 * @param newUser: the user name of client
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	private void login(String newUser) throws IOException {
		name = newUser;
		showMessage("User " + name + " logged on");
		state = State.INITIALIZED;
		writeMessage(Communications.L);
	}

	/**
	 * Implements the ALREADYLOGGEDIN protocol and sends the ALREADYLOGGEDIN message
	 * to the connected client. This method should only be called after
	 * CollectoServer.addClient() to confirm that a user exists with the same name.
	 *
	 * @param existingUser: the existing user name submitted by the client.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	private void alreadyLoggedin(String existingUser) throws IOException {
		showMessage("User " + existingUser + " tried logging on with existing name");
		writeMessage(Communications.AL);
	}

	/**
	 * Reads the passed parameter and replies to the protocol messages of LIST,
	 * QUEUE, and MOVE (when state == IN_GAME).
	 *
	 * @param args: the message from the client split by the Communications.DELIM.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	private void parseInitializedCommands(String[] args) throws IOException {

		String command = args[0];

		// ALWAYS AVAILABLE
		if (command.equals(Communications.LS)) {
			sendList();
			return;
		}

		if (state.equals(State.INITIALIZED) && command.equals(Communications.Q)) {
			queue();
		} else if (state.equals(State.IN_GAME) && command.equals(Communications.M) && args.length > 1) {
			receiveMove(moveStringToInt(args));
		}
	}

	/**
	 * Implements the LIST protocol and sends a LIST message containing all of the
	 * clients connected and intialized to the server.
	 *
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	private void sendList() throws IOException {
		String list = Communications.LS + Communications.DELIM + server.getUsers();
		writeMessage(list);
	}

	/**
	 * Implements the QUEUE protocol adds the client to the server queue for
	 * matchmaking.
	 */
	private void queue() {
		server.queue(this);
	}

	/**
	 * Implements the MOVE protocol and calls the game to make the passed move.
	 *
	 * @param move: the move to be played as an Integer array.
	 */
	private void receiveMove(int[] move) {
		game.receiveMove(move, this);
	}

	/**
	 * Implements the MOVE protocol and sends the passed move to the client.
	 *
	 * @param move: the move to be sent to the client as an Integer array.
	 */
	public void sendMove(int[] move) {
		try {
			String moveMessage = Communications.M;
			for (int m : move) {
				moveMessage += Communications.DELIM + m;
			}
			writeMessage(moveMessage);
		} catch (IOException e) {
			disconnect();
		}
	}

	/**
	 * Implements the GAMEOVER protocol and dereferences the game of this client
	 * handler and returns the state to INITIALIZED. This method should only be
	 * called when this client handler is in a game.
	 *
	 * @param condition: the description of the condition for the game over.
	 */
	// is only called after initialization (is a precondition)
	public void gameOver(String condition) {
		try {
			game = null;
			state = State.INITIALIZED;
			writeMessage(condition);
		} catch (IOException e) {
			disconnect();
		}
	}

	/**
	 * Shows message with prepended user name. This method is used to distinguish
	 * between different client handlers on the server console.
	 *
	 * @param msg: the message to be written to the console.
	 */
	private void showMessage(String msg) {
		String represenation = this.toString();
		if (!name.equals("")) {
			represenation = name;
		}
		CollectoInterface.showMessage("Client " + represenation + ": " + msg);
	}
}
