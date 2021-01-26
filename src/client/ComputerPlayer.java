package client;

import game.CollectoStrategy;
import util.CollectoInterface;

public class ComputerPlayer extends CollectoClientPlayer {
	
	private CollectoStrategy strategy;
	
	public ComputerPlayer(CollectoClient client, CollectoStrategy strategy) {
		super(client);
		this.strategy = strategy;
	}

	@Override
	public int[] getMove() {
		
		try {
			lobbyThread.join();
		} catch (InterruptedException e) {
			CollectoInterface.showMessage("Error joining main thread");
		}
		
		return strategy.getMove(client.game.board);
	}
}
