package gameTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.Board;
import game.COLOUR;

class boardTests {
	
	Board board;
	
	private static final int[] CORRECT_GRID_EXAMPLE = new int[] {5, 3, 4, 2, 5, 3, 6, 4, 6, 3, 4, 3, 1, 2, 5, 3, 2, 1, 2, 6, 5, 4, 1, 4, 0, 4, 1, 4, 5, 6, 2, 1, 5, 6, 2, 3, 1, 5, 4, 6, 5, 3, 6, 3, 6, 2, 1, 2, 1};
	private static final int[] ENDGAME_EXAMPLE = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 0, 4, 3, 0, 0, 5, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 5, 6, 1, 0, 6};
	
	@BeforeEach
	void setUp() throws Exception {
		
	}
	
	@Test
	void passedArgumentGenerationTest()
	{
		board = new Board(CORRECT_GRID_EXAMPLE);
		int counter = 0;
		for (COLOUR c : board.grid)
		{
			assertEquals(c.getValue(), CORRECT_GRID_EXAMPLE[counter]);
			counter++;
		}
	}
	
	@Test
	void neighbourAlgorithmTest()
	{
		board = new Board(CORRECT_GRID_EXAMPLE);
		assertFalse(board.boardHasNeighbours());
	}

	@Test
	void selfGenerationTest() {
		board = new Board();
		assertFalse(board.boardHasNeighbours());
		assertEquals(board.grid[Board.CENTER], COLOUR.EMPTY);
	}
	
	@Test
	void endGameTest() {
		board = new Board(ENDGAME_EXAMPLE);
		assertFalse(board.boardHasNeighbours());
		assertEquals(board.noMovesLeft(), true);
	}
}
