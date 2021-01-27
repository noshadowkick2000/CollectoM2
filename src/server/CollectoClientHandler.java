package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import game.Board;
import util.CollectoInterface;
import util.CollectoNetworker;
import util.Communications;

public class CollectoClientHandler extends CollectoNetworker implements Runnable {

	private enum State {
		UNINITIALIZED, NO_USERNAME, INITIALIZED, IN_GAME
	}

	// private Socket sock;
	private CollectoServer server;
	private CollectoServerGame game;
	private String description;

	private String name = "";
	private State state = State.UNINITIALIZED;

	// Public methods
	// ------------------------------------------------------------------------------------------

	public CollectoClientHandler(Socket sock, CollectoServer server, String description) throws IOException {
		// this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		this.server = server;
		this.description = description;
	}

	@Override
	public void run() {
		handleInput();
	}

	public String getName() {
		return name;
	}

	public void startGame(String board, String p1, String p2, CollectoServerGame game) {
		try {
			this.game = game;
			state = State.IN_GAME;
			writeMessage(Communications.NG + Communications.DELIM + board + Communications.DELIM + p1
					+ Communications.DELIM + p2);
		} catch (IOException e) {
			disconnect();
		}
	}

	public void showError(String description) throws IOException {
		writeMessage(Communications.ERR + Communications.DELIM + description);
	}

	// Private methods
	// -----------------------------------------------------------------------------------------

	private void disconnect() {
		showMessage("Client disconnected, client handler aborting");
		server.removeClient(this);
		if (state.equals(State.IN_GAME))
			game.disconnectClient(this);
	}

	private void handleInput() {
		String input;

		try {
			input = in.readLine();
			while (input != null) {
				parseCommand(input);
				input = in.readLine();
			}
		} catch (IOException e) {
			disconnect();
		}
	}

	private void parseCommand(String input) throws IOException {
		String[] commands = input.split(Communications.DELIM);

		if (state.equals(State.UNINITIALIZED) || state.equals(State.NO_USERNAME)) {
			// contains at least hello and description
			parseInitialization(commands);
		} else {
			parseInitializedCommands(commands);
		}
	}

	private void parseInitialization(String[] args) throws IOException {
		if (args.length > 1) {
			if (state.equals(State.UNINITIALIZED)) {
				// HELLO RESPONSE
				if (args[0].equals(Communications.H)) {
					hello(description);
				}
			} else if (state.equals(State.NO_USERNAME)) {
				// LOGIN RESPONSE
				if (args[0].equals(Communications.L)) {
					name = args[1];
					if (server.addClient(this)) {
						login(args[1]);
					} else {
						alreadyLoggedin(args[1]);
					}
				}
			}
		}
	}

	private void hello(String description) throws IOException {
		showMessage("Client with description: " + description + " connected to Server");
		state = State.NO_USERNAME;
		writeMessage(Communications.H + Communications.DELIM + description);
	}

	private void login(String newUser) throws IOException {
		showMessage("User " + newUser + " logged on");
		state = State.INITIALIZED;
		writeMessage(Communications.L);
	}

	private void alreadyLoggedin(String existingUser) throws IOException {
		showMessage("User " + existingUser + " tried logging on with existing name");
		writeMessage(Communications.AL);
	}

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
			receiveMove(Board.moveStringToInt(args));
		}
	}

	private void sendList() throws IOException {
		String list = Communications.LS + Communications.DELIM + server.getUsers();
		writeMessage(list);
	}

	private void queue() {
		server.queue(this);
	}
	
	private void receiveMove(int[] move) {
		game.receiveMove(move, this);
	}
	
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

	private void showMessage(String msg) {
		String represenation = this.toString();
		if (!name.equals("")) {
			represenation = name;
		}
		CollectoInterface.showMessage("Client " + represenation + ": " + msg);
	}
}
