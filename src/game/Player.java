package game;

public interface Player {
	
	public void giveBalls(COLOUR[] wonBalls);
	
	public int makeMove(Board[] nextBoards);

}
