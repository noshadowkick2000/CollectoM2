/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package game;

import java.util.List;

import game.Board.Move;
import util.CollectoInterface;

/**
 * The Class HardStrategy.
 */
public class HardStrategy implements CollectoStrategy {

	/** Maximum depth for the minmax algorithm */
	private static final int MAX_DEPTH = 4;

	/**
	 * Returns a move to be played on the passed Board from the Strategy
	 *
	 * @requires board != null.
	 * @param board: the Board on which to play the move.
	 * @return the move to be played as an Integer array
	 */
	@Override
	public int[] getMove(Board board) {

		if (board.getPossibleMoves().size() == 0) {
			return null;
		}

		boolean thisIsFirstPlayer = board.firstPlayerTurn;

		int depth = 0;

		List<Move> nextMoves = board.getPossibleMoves();
		int[] scores = new int[nextMoves.size()];
		crunchNodes(false, nextMoves, scores, board, depth, thisIsFirstPlayer);
		CollectoInterface.showMessage(getHighestScore(scores, false) + "");
		return nextMoves.get(getHighestScore(scores, true)).move;
	}

	/**
	 * Implements minimax algorithm. Looks ahead at for all moves up to a certain
	 * depth and returns the moves associated with the best score. The score is
	 * determined by the amount of points this player has - the amount of points the
	 * opponent has.
	 *
	 * @param maximizing:        true if next move is to be made by opponent player.
	 * @param board:             the Board on which this move has to be played.
	 * @param depth:             the current depth inside of the minimax algrithm.
	 * @param thisIsFirstPlayer: true if this player is the first player in the
	 *                           game.
	 * @return the score of this node.
	 */
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

	/**
	 * Subimplementation for minimax algorithm.
	 *
	 * @param nextMoves:         List containing all of the Moves that can currently
	 *                           be played on the passed board.
	 * @param scores:            Array containing the scores corresponding to each
	 *                           Move passed in nextMoves.
	 * @param maximizing:        true if next move is to be made by opponent player.
	 * @param board:             the Board on which this move has to be played.
	 * @param depth:             the current depth inside of the minimax algrithm.
	 * @param thisIsFirstPlayer: true if this player is the first player in the
	 *                           game.
	 * @return the score of this node.
	 */
	private void crunchNodes(boolean maximizing, List<Move> nextMoves, int[] scores, Board board, int depth,
			boolean thisIsFirstPlayer) {
		for (int i = 0; i < nextMoves.size(); i++) {
			Board nextBoard = board.deepCopy();
			nextBoard.makeMove(nextBoard.getPossibleMoves().get(i).move);
			scores[i] = minMax(maximizing, nextBoard, depth + 1, thisIsFirstPlayer);
		}
	}

	/**
	 * Gets the highest score from the given array scores. Will return the index of
	 * the highest score in the array if returnIndex == true.
	 *
	 * @param scores:      array containing the integer scores to be compared.
	 * @param returnIndex: true if this function should return an index rather than
	 *                     a score.
	 * @return the highest score from scores
	 */
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

	/**
	 * Gets the lowest score from the given array scores. Will return the index of
	 * the lowest score in the array if returnIndex == true.
	 *
	 * @param scores: array containing the integer scores to be compared.
	 * @return the lowest score from scores
	 */
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
