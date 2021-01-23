package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import game.COLOUR;
import game.Collecto;
import game.EasyStrategy;
import game.HardStrategy;
import util.Communications;

public class CollectoClient extends Collecto {
	
	private static final String USAGE = "Start Client with arguments [host-adress][port]";
	private static final String CHOOSE_HUMAN = "HUMAN";
	private static final String CHOOSE_AI_EASY = "AI EASY";
	private static final String CHOOSE_AI_HARD = "AI HARD";
	
	private BufferedReader in;
	private BufferedWriter out;
	
	private CollectoClientPlayer localPlayer;

	@Override
	public void startGame() {
		
	}

	@Override
	public List<COLOUR> makeMove(int[] move) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endGame(Condition endGameCondition) {
		
	}
	
	public void connectServer(String[] args) throws IOException {
		// get arguments
		InetAddress hostAdress = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[2]);
		
		// create and assign socket and in and out
		Socket sock = new Socket(hostAdress, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		
		// TODO
		// create new thread for input handling and output handling and pass this Collecto ref so they van make moves
		handleInitialization();
	}
	
	public void handleInitialization() {
		Scanner scanner = new Scanner(System.in);
		
		boolean modeChosen = false;
		
		while (!modeChosen) {
			String input = scanner.nextLine();
			
			switch (input) {
			case CHOOSE_HUMAN:
				localPlayer = new HumanPlayer();
				modeChosen = true;
				break;
			case CHOOSE_AI_EASY:
				localPlayer = new ComputerPlayer(new EasyStrategy());
				modeChosen = true;
				break;
			case CHOOSE_AI_HARD:
				localPlayer = new ComputerPlayer(new HardStrategy());
				modeChosen = true;
				break;
			}
		}
		
		try {
		
		showMessage("Enter client description");
		String description = scanner.nextLine();
		
		writeMessage(Communications.H + Communications.DELIM + description);
		
		scanner.close();
		
		} catch (IOException e) {
			showMessage("Connection error: server disconnected");
		}
	}
	
	public void initializeProtocol(String description, Scanner scanner) {
		
	}
	
	public static void showMessage(String msg) {
		System.out.println(msg);
	}
	
	private void writeMessage(String msg) throws IOException {
		out.write(msg);
		out.newLine();
		out.flush();
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
