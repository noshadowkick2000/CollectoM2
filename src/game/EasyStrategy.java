package game;

import java.util.Random;

public class EasyStrategy implements CollectoStrategy {

	@Override
	public int[] getMove(Board board) {
		Random r = new Random();
		if (board.getPossibleMoves().size() == 0) {
			return null;
		}
		return board.getPossibleMoves().get(r.nextInt(board.getPossibleMoves().size())).move;
	}
}
