package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import util.Communications;

public class CollectoServer {

	private static final String USAGE = "run this server with arguments: [portnumber][description]";
	private List<CollectoClientHandler> connectedClients = new ArrayList<CollectoClientHandler>();
	private List<CollectoClientHandler> queuedClients = new ArrayList<CollectoClientHandler>();
	
	private int newId = 0;
	
	// Constructors and Public methods ----------------------------------------------------------------------------

	public void setupServer(int port, String description) {
		try {
			ServerSocket ss = new ServerSocket(port, 0, InetAddress.getLocalHost());
			showMessage("Server created on port " + port 
					+ " with ip " + InetAddress.getLocalHost().getHostAddress());
			
			while (System.in.available() == 0) {
				Socket sock = ss.accept();
				showMessage("Client connected");
				(new Thread((new CollectoClientHandler(sock, this, description)))).start();
			}
			
			ss.close();
		} catch (IOException e) {
			System.out.println("Error creating server connections");
			System.exit(-1);
		}
	}
	
	// only called once name has been established
	// returns true if succesful
	synchronized public boolean addClient(CollectoClientHandler client) {
		if (hasExistingLogin(client.getName())) return false;
		showMessage("Client " + client.getName() + " initialized");
		connectedClients.add(client);
		return true;
	}
	
	synchronized public void removeClient(CollectoClientHandler client) {
		showMessage("Client " + client.getName() + " disconnected");
		connectedClients.remove(client);
		if (queuedClients.contains(client)) {
			queuedClients.remove(client);
		}
	}
	
	synchronized public void queue(CollectoClientHandler client) {
		
		if (queuedClients.contains(client)) {
			showMessage("Client " + client.getName() + " exited queue");
			queuedClients.remove(client);
		}
		else {
			showMessage("Client " + client.getName() + " joined queue");
			queuedClients.add(client);
			if (queuedClients.size()>1) {
				startNewGame();
			}
		}
	}
	
	public String getUsers()
	{
		String users = "";
		for (int i = 0; i < connectedClients.size(); i++)
		{
			users += connectedClients.get(i).getName();
			if (i==connectedClients.size()-1) {
				continue;
			}
			users += Communications.DELIM;
		}
		return users;
	}
	
	public static void showMessage(String msg) {
		System.out.println(msg);
	}
	
	// Private methods ---------------------------------------------------------------------------------------------
	
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
		
		game.startGame();
	}

	// MAIN METHOD ----------------------------------------------------------------

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println(USAGE);
			System.exit(-1);
		}
		int port = Integer.parseInt(args[0]);
		String description = "";
		for (int i = 1; i < args.length; i++) {
			description += args[i];
		}
		(new CollectoServer()).setupServer(port, description);
	}
}
