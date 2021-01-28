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

public class CollectoServer implements Runnable {

	public List<CollectoClientHandler> connectedClients = new ArrayList<CollectoClientHandler>();
	public List<CollectoClientHandler> queuedClients = new ArrayList<CollectoClientHandler>();

	private int newId = 0;

	public ServerSocket ss;

	// Constructors and Public methods
	// ----------------------------------------------------------------------------

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

		String description = CollectoInterface.requestInput("Enter the server description");

		try {
			createServerSocket(port);
			acceptNewClients(description);
		} catch (IOException e) {
			System.exit(-1);
		}
	}

	public void createServerSocket(int port) throws UnknownHostException, IOException {

		ss = new ServerSocket(port, 0, InetAddress.getLocalHost());
		CollectoInterface.showMessage(
				"Server created on port " + port + " with ip " + InetAddress.getLocalHost().getHostAddress());

	}

	public void acceptNewClients(String description) throws IOException {
		(new Thread(this)).start();

		while (true) {
			createNewHandler(description);
		}
	}

	synchronized public void createNewHandler(String description) throws IOException {
		Socket sock = ss.accept();
		CollectoInterface.showMessage("Client connected");
		(new Thread((new CollectoClientHandler(sock, this, description)))).start();
	}

	public void run() {
		CollectoInterface.requestInput("Press enter to close the server");
		exit();
	}

	// only called once name has been established
	// returns true if succesful
	synchronized public boolean addClient(CollectoClientHandler client) {
		if (hasExistingLogin(client.getName()))
			return false;
		CollectoInterface.showMessage("Client " + client.getName() + " initialized");
		connectedClients.add(client);
		return true;
	}

	synchronized public void removeClient(CollectoClientHandler client) {
		CollectoInterface.showMessage("Client " + client.getName() + " disconnected");
		connectedClients.remove(client);
		if (queuedClients.contains(client)) {
			queuedClients.remove(client);
		}
	}

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

	private boolean hasExistingLogin(String user) {
		for (CollectoClientHandler c : connectedClients) {
			if (c.getName().equals(user)) {
				return true;
			}
		}
		return false;
	}

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

	public static void main(String[] args) {
		(new CollectoServer()).setupServer();
	}
}
