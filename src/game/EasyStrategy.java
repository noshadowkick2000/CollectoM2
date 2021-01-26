package game;

import java.util.Random;

public class EasyStrategy implements CollectoStrategy {

	@Override
	public int[] getMove(Board board) {
		Random r = new Random();
		return board.getPossibleMoves().get(r.nextInt(board.getPossibleMoves().size())).move;
	}
}
