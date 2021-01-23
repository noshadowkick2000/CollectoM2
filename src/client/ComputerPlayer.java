package client;

import game.CollectoStrategy;

public class ComputerPlayer extends CollectoClientPlayer {
	
	private CollectoStrategy strategy;
	
	public ComputerPlayer(CollectoStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public int[] getMove() {
		// TODO Auto-generated method stub
		return null;
	}

}
