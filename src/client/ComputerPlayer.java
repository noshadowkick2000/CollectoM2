package client;

import java.util.Scanner;

import game.CollectoStrategy;

public class ComputerPlayer extends CollectoClientPlayer {
	
	private CollectoStrategy strategy;
	
	public ComputerPlayer(CollectoClient client, Scanner scanner, CollectoStrategy strategy) {
		super(client, scanner);
		this.strategy = strategy;
	}

	@Override
	public int[] getMove() {
		
		try {
			lobbyThread.join();
		} catch (InterruptedException e) {
			CollectoClient.showMessage("Error joining main thread");
		}
		
		return strategy.getMove(client.board);
	}
}
