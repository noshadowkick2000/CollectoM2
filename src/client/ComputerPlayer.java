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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endGame() {
		// TODO Auto-generated method stub
		
	}
}
