package game;

import java.util.List;

import client.CollectoClient;
import game.Board.Move;

public class HardStrategy implements CollectoStrategy {

	private static final int MAX_DEPTH = 4;

	@Override
	public int[] getMove(Board board) {

		boolean thisIsFirstPlayer = board.firstPlayerTurn;

		int depth = 0;

		List<Move> nextMoves = board.getPossibleMoves();
		int[] scores = new int[nextMoves.size()];
		crunchNodes(false, nextMoves, scores, board, depth, thisIsFirstPlayer);
		CollectoClient.showMessage(getHighestScore(scores, false) + "");
		return nextMoves.get(getHighestScore(scores, true)).move;
	}

	private int minMax(boolean maximizing, Board board, int depth, boolean thisIsFirstPlayer) {
		if (board.noMovesLeft() || depth > MAX_DEPTH) {
			return board.countPoints(thisIsFirstPlayer) - board.countPoints(!thisIsFirstPlayer);
		}

		List<Move> nextMoves = board.getPossibleMoves();
		int[] scores = new int[nextMoves.size()];
		crunchNodes(!maximizing, nextMoves, scores, board, depth, thisIsFirstPlayer);

		if (maximizing) {
			return getHighestScore(scores, false);
		} else {
			return getLowestScore(scores);
		}
	}

	private void crunchNodes(boolean maximizing, List<Move> nextMoves, int[] scores, Board board, int depth,
			boolean thisIsFirstPlayer) {
		for (int i = 0; i < nextMoves.size(); i++) {
			Board nextBoard = board.deepCopy();
			nextBoard.makeMove(nextBoard.getPossibleMoves().get(i).move);
			scores[i] = minMax(maximizing, nextBoard, depth + 1, thisIsFirstPlayer);
		}
	}

	private int getHighestScore(int[] scores, boolean returnIndex) {
		int index = 0;
		int highscore = scores[index];
		for (int i = 1; i < scores.length; i++) {
			if (scores[i] > highscore) {
				highscore = scores[i];
				index = i;
			}
		}
		return returnIndex ? index : highscore;
	}

	private int getLowestScore(int[] scores) {
		int lowestScore = scores[0];
		for (int i = 1; i < scores.length; i++) {
			if (scores[i] < lowestScore) {
				lowestScore = scores[i];
			}
		}
		return lowestScore;
	}
}
