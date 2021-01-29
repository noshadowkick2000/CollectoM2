package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import game.Board;
import game.COLOUR;
import util.Communications;

class boardTests {

	Board board;

	public static final int[] CORRECT_GRID_EXAMPLE = new int[] { 5, 3, 4, 2, 5, 3, 6, 4, 6, 3, 4, 3, 1, 2, 5, 3, 2, 1,
			2, 6, 5, 4, 1, 4, 0, 4, 1, 4, 5, 6, 2, 1, 5, 6, 2, 3, 1, 5, 4, 6, 5, 3, 6, 3, 6, 2, 1, 2, 1 };
	public static final int[] ENDGAME_EXAMPLE = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 3, 0, 0, 5,
			2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0 };
	
	private static final String CORRECT_GRID_EXAMPLE_STRING = "5~3~4~2~5~3~6~4~6~3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1~4~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3~6~2~1~2~1";
	private static final int[] CORRECT_GRID_EXAMPLE_LEGAL_MOVES = new int[] { 3, 10, 17, 24 };
	private static final int[] CORRECT_GRID_EXAMPLE_MOVE_THREE = new int[] { 5, 3, 4, 2, 5, 3, 6, 4, 6, 3, 4, 3, 1, 2,
			5, 3, 2, 1, 2, 6, 5, 4, 1, 0, 0, 1, 4, 0, 5, 6, 2, 1, 5, 6, 2, 3, 1, 5, 4, 6, 5, 3, 6, 3, 6, 2, 1, 2, 1 };
	private static final int[] DOUBLE_MOVE_BOARD = new int[] { 6, 3, 5, 3, 4, 3, 1, 5, 2, 4, 5, 2, 1, 2, 2, 4, 6, 4, 6,
			3, 5, 1, 2, 5, 0, 3, 1, 2, 4, 6, 1, 6, 2, 5, 6, 6, 1, 5, 3, 1, 4, 1, 5, 3, 4, 6, 3, 2, 4 };
	private static final int[] SINGLE_MOVE_EXAMPLE = new int[] {3};
	private static final String SINGLE_MOVE_EXAMPLE_PROTOCOL = "MOVE~3";
	private static final int[] DOUBLE_MOVE_EXAMPLE = new int[] {3, 10};
	private static final String DOUBLE_MOVE_EXAMPLE_PROTOCOL = "MOVE~3~10";

	@Test
	void passedArgumentGenerationTest() {
		board = new Board(CORRECT_GRID_EXAMPLE);
		int counter = 0;
		for (COLOUR c : board.grid) {
			assertEquals(c.getValue(), CORRECT_GRID_EXAMPLE[counter]);
			counter++;
		}
	}

	@Test
	void neighbourAlgorithmTest() {
		board = new Board(CORRECT_GRID_EXAMPLE);
		assertFalse(board.boardHasNeighbours());
	}

	@Test
	void deepCopyTest() {
		board = new Board(CORRECT_GRID_EXAMPLE);
		Board copiedBoard = board.deepCopy();
		copiedBoard.makeMove(copiedBoard.getPossibleMoves().get(0).move);
		assertNotEquals(board.grid, copiedBoard.grid);
	}

	@Test
	void selfGenerationTest() {
		board = new Board();
		assertFalse(board.boardHasNeighbours());
		assertEquals(COLOUR.EMPTY, board.grid[Board.CENTER]);
	}

	@Test
	void endGameTest() {
		board = new Board(ENDGAME_EXAMPLE);
		assertFalse(board.boardHasNeighbours());
		assertTrue(board.noMovesLeft());
	}

	@Test
	void possibleMovesTest() {
		board = new Board(CORRECT_GRID_EXAMPLE);
		assertFalse(board.noMovesLeft());
		assertEquals(CORRECT_GRID_EXAMPLE_LEGAL_MOVES.length, board.getPossibleMoves().size());
		assertEquals(CORRECT_GRID_EXAMPLE_LEGAL_MOVES[0], board.getPossibleMoves().get(0).move[0]);
		assertEquals(CORRECT_GRID_EXAMPLE_LEGAL_MOVES[1], board.getPossibleMoves().get(1).move[0]);
		assertEquals(CORRECT_GRID_EXAMPLE_LEGAL_MOVES[2], board.getPossibleMoves().get(2).move[0]);
		assertEquals(CORRECT_GRID_EXAMPLE_LEGAL_MOVES[3], board.getPossibleMoves().get(3).move[0]);
	}

	@Test
	void moveTest() {
		board = new Board(CORRECT_GRID_EXAMPLE);
		assertTrue(board.firstPlayerTurn);
		board.makeMove(new int[] { 3 });
		assertFalse(board.firstPlayerTurn);

		int counter = 0;
		for (COLOUR c : board.grid) {
			assertEquals(c.getValue(), CORRECT_GRID_EXAMPLE_MOVE_THREE[counter]);
			counter++;
		}
		assertEquals(2, board.p1Balls.size());
		assertEquals(COLOUR.CYAN, board.p1Balls.get(0));
		assertEquals(COLOUR.CYAN, board.p1Balls.get(1));
		assertEquals(0, board.countPoints(true));
		assertEquals(0, board.p2Balls.size());

		board.makeMove(new int[] { 3 });

		assertEquals(2, board.p2Balls.size());
		assertEquals(COLOUR.RED, board.p2Balls.get(0));
		assertEquals(COLOUR.RED, board.p2Balls.get(1));
		assertEquals(0, board.countPoints(false));
	}

	@Test
	void doubleMoveTest() {
		board = new Board(DOUBLE_MOVE_BOARD);
		board.makeMove(new int[] { 17, 6 });
		assertNotEquals(0, board.p1Balls.size());
	}

	@Test
	void countPointsTest() {
		board = new Board();
		assertEquals(0, board.p1Balls.size());
		board.p1Balls.add(COLOUR.RED);
		board.p1Balls.add(COLOUR.RED);
		board.p1Balls.add(COLOUR.RED);
		board.p1Balls.add(COLOUR.CYAN);
		board.p1Balls.add(COLOUR.CYAN);
		board.p1Balls.add(COLOUR.BLUE);
		board.p1Balls.add(COLOUR.BLUE);
		board.p1Balls.add(COLOUR.BLUE);
		board.p1Balls.add(COLOUR.BLUE);
		assertEquals(2, board.countPoints(true));
	}

	@Test
	void illegalMoveTest() {
		board = new Board(ENDGAME_EXAMPLE);
		board.makeMove(new int[] { 0 });
		assertEquals(0, board.p1Balls.size());
	}

	@Test
	void communicationTest() {
		board = new Board(CORRECT_GRID_EXAMPLE);
		assertEquals(CORRECT_GRID_EXAMPLE_STRING, board.toCommunicationString());
	}
	
	@Test
	void moveProtocolTest() {
		String protocolMove = Board.moveIntToString(SINGLE_MOVE_EXAMPLE);
		assertEquals(SINGLE_MOVE_EXAMPLE_PROTOCOL, protocolMove);
		
		protocolMove = Board.moveIntToString(DOUBLE_MOVE_EXAMPLE);
		assertEquals(DOUBLE_MOVE_EXAMPLE_PROTOCOL, protocolMove);
	}
	
	@Test
	void moveArrayTest() {
		int[] arrayMove = Board.moveStringToInt(SINGLE_MOVE_EXAMPLE_PROTOCOL.split(Communications.DELIM));
		assertArrayEquals(SINGLE_MOVE_EXAMPLE, arrayMove);
		
		arrayMove = Board.moveStringToInt(DOUBLE_MOVE_EXAMPLE_PROTOCOL.split(Communications.DELIM));
		assertArrayEquals(DOUBLE_MOVE_EXAMPLE, arrayMove);
	}
}
