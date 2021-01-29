/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import util.CollectoInterface;
import util.Communications;

/**
 * The Class CollectoServer.
 */
public class CollectoServer implements Runnable {

	/**
	 * The connected initialized clients represented by their respective client
	 * handlers.
	 */
	public List<CollectoClientHandler> connectedClients = new ArrayList<CollectoClientHandler>();

	/** The clients who are currently queueing for a game. */
	public List<CollectoClientHandler> queuedClients = new ArrayList<CollectoClientHandler>();

	/**
	 * The next id for each new game, is used to distinguish between messages from
	 * different games
	 */
	private int newId = 0;

	/** The server socket used to create the sockets for the client handlers. */
	public ServerSocket ss;

	/** The description of this server. */
	public String description = "";

	// Constructors and Public methods
	// ----------------------------------------------------------------------------

	/**
	 * Requests and sets the server port and description, and calls the methods to
	 * create the server socket and the method to accept new clients.
	 */
	public void setupServer() {
		int port = 0;
		while (true) {
			try {
				port = Integer.parseInt(CollectoInterface.requestInput("Enter the server port number"));
				break;
			} catch (NumberFormatException e) {
				CollectoInterface.showMessage("Invalid port, try again");
			}
		}

		description = CollectoInterface.requestInput("Enter the server description");

		try {
			createServerSocket(port);
			acceptNewClients();
		} catch (IOException e) {
			System.exit(-1);
		}
	}

	/**
	 * Creates the server socket on the local host using the passed port.
	 *
	 * @ensures ss = new ServerSocket(oirt, 0, InetAdress.getLocalHost().
	 * @param port: the port on which to create the ServerSocket
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 *                              Generally indicates client has disconnected.
	 */
	public void createServerSocket(int port) throws UnknownHostException, IOException {

		ss = new ServerSocket(port, 0, InetAddress.getLocalHost());
		CollectoInterface.showMessage(
				"Server created on port " + port + " with ip " + InetAddress.getLocalHost().getHostAddress());

	}

	/**
	 * Creates a new thread to listen for a close command in the server console and
	 * starts a loop to accept new clients.
	 *
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	public void acceptNewClients() throws IOException {
		(new Thread(this)).start();

		while (true) {
			createNewHandler();
		}
	}

	/**
	 * Creates a new client handler.
	 *
	 * @requires ss != null.
	 * @throws IOException Signals that an I/O exception has occurred. Generally
	 *                     indicates client has disconnected.
	 */
	public void createNewHandler() throws IOException {
		Socket sock = ss.accept();
		CollectoInterface.showMessage("Client connected");
		(new Thread((new CollectoClientHandler(sock, this)))).start();
	}

	/**
	 * Run implementation of the Runnable Interface. Listens for any input from the
	 * console. If there is any, call exit().
	 */
	public void run() {
		CollectoInterface.requestInput("Press enter to close the server");
		exit();
	}

	/**
	 * Adds the client handler to the List of connected clients, if the userName of
	 * the client is unique. Only call this method once the client has sent the
	 * HELLO protocol.
	 *
	 * @requires client != null, userName != null
	 * @ensures connectedClients.add(client);
	 * @param client:   the client handler assigned to the client.
	 * @param userName: user name sent for inspection by the client.
	 * @return true if user name is unique and client has been added to
	 *         connectedClients, else false
	 */
	synchronized public boolean addClient(CollectoClientHandler client, String userName) {
		if (hasExistingLogin(userName))
			return false;
		CollectoInterface.showMessage("Client " + userName + " initialized");
		connectedClients.add(client);
		return true;
	}

	/**
	 * Removes the client from the List of connectedClients and queuedClients in
	 * case the client handler was in the queue as well.
	 * 
	 * @requires client != null, connectedClients.contains(client).
	 * @ensures connectedClients.remove(client), queuedClients.remove(client) if
	 *          (queuedClients.contains(client)).
	 * @param client: the client handler assigned to the client.
	 */
	synchronized public void removeClient(CollectoClientHandler client) {
		CollectoInterface.showMessage("Client " + client.getName() + " disconnected");
		connectedClients.remove(client);
		if (queuedClients.contains(client)) {
			queuedClients.remove(client);
		}
	}

	/**
	 * Implements the QUEUE protocol. Add the passed CollectoClientHandler to the
	 * queuedClients List and checks whether there are enough people in the queue to
	 * start a newgame. If so, calls startNewGame().
	 *
	 * @requires client != null.
	 * @ensures queuedClients.remove(client) if queuedClients.contains(client),
	 *          queuedClients.add(client) if !queuedClients.contains(client).
	 * @param client: the client handler assigned to the client.
	 */
	synchronized public void queue(CollectoClientHandler client) {

		if (queuedClients.contains(client)) {
			CollectoInterface.showMessage("Client " + client.getName() + " exited queue");
			queuedClients.remove(client);
		} else {
			CollectoInterface.showMessage("Client " + client.getName() + " joined queue");
			queuedClients.add(client);
			if (queuedClients.size() > 1) {
				startNewGame();
			}
		}
	}

	/**
	 * Implements the LIST protocol. Returns a string containing all of the user
	 * names of the clients in connectedClients.
	 *
	 * @return a string containing all of the user names connected to this server.
	 */
	public String getUsers() {
		String users = "";
		for (int i = 0; i < connectedClients.size(); i++) {
			users += connectedClients.get(i).getName();
			if (i == connectedClients.size() - 1) {
				continue;
			}
			users += Communications.DELIM;
		}
		return users;
	}

	/**
	 * Closes the ServerSocket of this server and exits the program.
	 * 
	 * @ensures ss.close().
	 */
	public void exit() {
		try {
			CollectoInterface.showMessage("Shutting down");
			ss.close();
		} catch (IOException e) {
			CollectoInterface.showMessage("Error closing server socket");
		}
		System.exit(0);
	}

	// Private methods
	// ---------------------------------------------------------------------------------------------

	/**
	 * Checks for existing username in connectedClients equal to the passed
	 * parameter.
	 *
	 * @requires user != null.
	 * @param user: the user name to which to compare the existing users names.
	 * @return true if there is an existing client connected with the same user name
	 *         as user.
	 */
	private boolean hasExistingLogin(String user) {
		for (CollectoClientHandler c : connectedClients) {
			if (c.getName().equals(user)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Starts new game by creating a CollectoServerGame instance and assigning the 2
	 * lowest client handler in the queue to the game. Also removes these client
	 * handlers from the queue.
	 * 
	 * @requires queuedClients.size() > 1.
	 * @ensures first two clients from queuedClients are removed.
	 */
	synchronized private void startNewGame() {
		CollectoClientHandler playerOne = queuedClients.get(0);
		CollectoClientHandler playerTwo = queuedClients.get(1);
		CollectoServerGame game = new CollectoServerGame(newId++, playerOne, playerTwo);

		// remove them from the queue
		queuedClients.remove(playerOne);
		queuedClients.remove(playerTwo);

		game.newGame();
	}

	// MAIN METHOD ----------------------------------------------------------------

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		(new CollectoServer()).setupServer();
	}
}
