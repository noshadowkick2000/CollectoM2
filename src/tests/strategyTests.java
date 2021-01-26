package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import game.Board;
import game.CollectoStrategy;
import game.EasyStrategy;
import game.HardStrategy;
import game.MediumStrategy;

class strategyTests {
	
	Board board;
	CollectoStrategy strategy;

	void setUpPlayableBoard() {
		board = new Board(boardTests.CORRECT_GRID_EXAMPLE);
	}
	
	void setUpFinishedBoard() {
		board = new Board(boardTests.ENDGAME_EXAMPLE);
	}

	@Test
	void easyTest() {
		setUpPlayableBoard();
		strategy = new EasyStrategy();
		assertTrue(board.isValidMove(strategy.getMove(board)));
	}
	
	@Test
	void easyNoMovesLeftTest() {
		setUpFinishedBoard();
		strategy = new EasyStrategy();
		assertNull(strategy.getMove(board));
	}

	@Test
	void mediumTest() {
		setUpPlayableBoard();
		strategy = new MediumStrategy();
		assertTrue(board.isValidMove(strategy.getMove(board)));
	}
	
	@Test
	void mediumNoMovesLeftTest() {
		setUpFinishedBoard();
		strategy = new MediumStrategy();
		assertNull(strategy.getMove(board));
	}
	
	@Test
	void hardTest() {
		setUpPlayableBoard();
		strategy = new HardStrategy();
		assertTrue(board.isValidMove(strategy.getMove(board)));
	}
	
	@Test
	void hardNoMovesLeftTest() {
		setUpFinishedBoard();
		strategy = new HardStrategy();
		assertNull(strategy.getMove(board));
	}
}
