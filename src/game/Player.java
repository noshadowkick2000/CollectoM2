package game;

import java.util.ArrayList;
import java.util.List;

public class Player {
	
	public static int BALLS_PER_POINT = 3;
	
	public List<COLOUR> balls = new ArrayList<COLOUR>();
	
	public void giveBalls(List<COLOUR> wonBalls) {
		balls.addAll(wonBalls);
	}
	
	public int countPoints() {
		int[] ballCounter = new int[COLOUR.values().length];
		for (int i = 0; i < balls.size(); i++) {
			ballCounter[balls.get(i).getValue()]++;
		}
		
		int totalPoints = 0;
		
		for (int amount : ballCounter) {
			int excess = amount%BALLS_PER_POINT;
			totalPoints += (amount - excess)/BALLS_PER_POINT;
		}
		return totalPoints;
	}
}
