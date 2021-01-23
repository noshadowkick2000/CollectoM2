package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import util.Communications;

public class Board {

	public class Move {
		public Board board;
		public int[] move;
		public List<COLOUR> gainedBalls;

		// move is the number of the move that results in the given board
		public Move(Board board, int move, List<COLOUR> gainedBalls) {
			this.board = board;
			this.move = new int[] {move};
			this.gainedBalls = gainedBalls;
		}

		// move is the number of the move that results in the given board
		public Move(Board board, int[] move, List<COLOUR> gainedBalls) {
			this.board = board;
			this.move = move;
			this.gainedBalls = gainedBalls;
		}
	}

	public static final int BOARD_SIZE = 7;
	public static final int CENTER = (BOARD_SIZE * BOARD_SIZE - 1) / 2;
	public static final COLOUR[] AVAILABLE_COLOURS = COLOUR.values();

	public COLOUR[] grid = new COLOUR[BOARD_SIZE * BOARD_SIZE];
	private List<Move> possibleNextMoves = new ArrayList<Move>();

	// Public methods and constructors
	// ----------------------------------------------------------------

	public Board() {
		generateBoard();

		calculateNextPossibleMoves();
	}

	public Board(int[] boardValues) {
		assert boardValues.length == grid.length;
		assert boardValues[CENTER] == 0;

		int counter = 0;
		for (int i = 0; i < grid.length; i++) {
			grid[i] = AVAILABLE_COLOURS[boardValues[counter]];
			counter++;
		}

		calculateNextPossibleMoves();
	}

	// constructor so deepCopy() is facilitated
	// does not calculate the next moves since it is a copy of the board currently
	// calculating the moves
	public Board(COLOUR[] grid) {
		this.grid = grid.clone();
	}

	public List<COLOUR> makeMove(int[] move) {
		// search for calculated legal move and copy it's grid
		// recalculate next moves
		for (Move m : possibleNextMoves) {
			if (Arrays.equals(m.move, move)) {
				grid = m.board.grid;
				List<COLOUR> wonBalls = m.gainedBalls;
				calculateNextPossibleMoves();
				return wonBalls;
			}
		}

		// if move was not in calculated moves, it could not have been legal
		// return null
		return null;
	}

	public List<Move> getPossibleMoves() {
		return possibleNextMoves;
	}

	public boolean boardHasNeighbours() {
		for (int i = 0; i < grid.length; i++) {
			if (i == CENTER) {
				continue;
			}
			if (getIdenticalNeighbour(i) != -1) {
				return true;
			}
		}
		return false;
	}

	public boolean noMovesLeft() {
		if (possibleNextMoves.size() == 0) {
			return true;
		}
		return false;
	}

	public Board deepCopy() {
		return new Board(grid);
	}

	public String toString() {
		String board = "";
		for (int y = 0; y < BOARD_SIZE; y++) {
			String line = "|";
			for (int x = 0; x < BOARD_SIZE; x++) {
				line += (grid[x + y * BOARD_SIZE].getValue()) + "|";
			}
			board += line + System.lineSeparator();
		}
		return board;
	}
	
	public String toCommunicationString() {
		String board = "";
		for (int i = 0; i < grid.length; i++) {
			board += grid[i].getValue();
			if (i==grid.length-1) {
				continue;
			}
			board += Communications.DELIM;
		}
		return board;
	}
	
	// used for min max and deeper calculations
	public void manuallyCalculateMoves()
	{
		calculateNextPossibleMoves();
	}

	// Private methods
	// -------------------------------------------------------------------------------

	private void generateBoard() {
		// create list with 8 items per colour
		List<COLOUR> randomList = new ArrayList<COLOUR>();
		for (COLOUR c : AVAILABLE_COLOURS) {
			if (c == COLOUR.EMPTY) {
				randomList.add(c);
				continue;
			}
			for (int i = 0; i < 8; i++) {
				randomList.add(c);
			}
		}

		// randomize list
		Collections.shuffle(randomList);

		// find empty spot
		int emptyPosition = 0;
		for (int i = 0; i < randomList.size(); i++) {
			if (randomList.get(i).equals(COLOUR.EMPTY)) {
				emptyPosition = i;
				break;
			}
		}

		// swap positions to have center empty
		if (emptyPosition != CENTER) {
			Collections.swap(randomList, CENTER, emptyPosition);
		} else {
			Collections.swap(randomList, CENTER + 1, emptyPosition);
		}

		// assign temporary list to grid
		for (int i = 0; i < grid.length; i++) {
			grid[i] = randomList.get(i);
		}

		// reorganize list to make sure it is valid

		for (int i = 0; i < grid.length; i++) {
			if (i == CENTER) {
				continue;
			}
			int problem = -1;
			while ((problem = getIdenticalNeighbour(i)) != -1) {
				int swappedIndex = findNextValidIndex(problem);
				COLOUR swappedColour = grid[swappedIndex];
				grid[swappedIndex] = grid[problem];
				grid[problem] = swappedColour;
			}
		}

		if (boardHasNeighbours()) {
			generateBoard();
		}
	}

	// return next index after given index that is not equal to the given COLOUR
	// and
	// does not have neighbours if given COLOUR would be on index
	private int findNextValidIndex(int index) {
		int modIndex = index;
		COLOUR originalColour = grid[modIndex];
		while (grid[modIndex].equals(originalColour) || grid[modIndex].equals(COLOUR.EMPTY)
				|| getIdenticalNeighbour(modIndex, originalColour) != -1) {
			modIndex++;
			if (modIndex == grid.length) {
				modIndex = 0;
			}
		}
		return modIndex;
	}

	// returns -1 if no same colour neighbours
	// else returns one of the indexes of the same colour neighbours
	private int getIdenticalNeighbour(int index, COLOUR colour) {
		if (grid[index].equals(COLOUR.EMPTY)) {
			return -1;
		}

		int left = index - 1;
		int right = index + 1;
		int up = index - BOARD_SIZE;
		int down = index + BOARD_SIZE;

		if (index % BOARD_SIZE != 0) {
			if (hasIdenticalNeighbour(colour, left)) {
				return left;
			}
		}
		if ((index + 1) % BOARD_SIZE != 0) {
			if (hasIdenticalNeighbour(colour, right)) {
				return right;
			}
		}
		if (index < grid.length - BOARD_SIZE) {
			if (hasIdenticalNeighbour(colour, down)) {
				return down;
			}
		}
		if (index > BOARD_SIZE - 1) {
			if (hasIdenticalNeighbour(colour, up)) {
				return up;
			}
		}
		return -1;
	}

	// returns -1 if no same colour neighbours
	// else returns one of the indexes of the same colour neighbours
	private int getIdenticalNeighbour(int index) {
		return getIdenticalNeighbour(index, grid[index]);
	}

	private boolean hasIdenticalNeighbour(COLOUR colour, int neighbourIndex) {
		if (grid[neighbourIndex].equals(colour)) {
			return true;
		}
		return false;
	}

	// before this method is called, all of the possible moves should have been
	// calculated
	// checkValidMove = true if player makes single move
	// and
	// checkValidMove = true if player makes double move and this method is called
	// for the second time
	// returns
	// see protocol on gitlab to see move mapping
	private List<COLOUR> tryMove(int move, boolean checkValidMove) {
		if (!hasEmpty(move)) {
			return null;
		}

		if (move < 7) {
			moveHorizontal(true, move);
		} else if (move < 14) {
			moveHorizontal(false, move - BOARD_SIZE);
		} else if (move < 21) {
			moveVertical(true, move - BOARD_SIZE * 2);
		} else {
			moveVertical(false, move - BOARD_SIZE * 3);
		}

		if (!boardHasNeighbours() && checkValidMove) {
			return null;
		}

		return removeBalls();
	}

	private boolean hasEmpty(int move) {

		// check if move is with row or column
		// check if empty spot in row or column
		// N between 0 and 6: push row N to left
		// N between 7 and 13: push row (N-7) to right
		// N between 14 and 20: push column (N-14) upwards
		// N between 21 and 27: push column (N-21) downwards

		if (move < 14) {
			// check horizontal
			if (horizontalHasEmpty(move)) {
				return true;
			}
		} else if (move < 28) {
			// check vertical
			if (verticalHasEmpty(move)) {
				return true;
			}
		}

		return false;
	}

	private void moveLine(int start, int direction, int step) {
		int counter = 0;
		while (counter != BOARD_SIZE) {
			int index = start + (direction * counter * step);
			if (!grid[index].equals(COLOUR.EMPTY)) {
				int moveCounter = 1;
				while (true) {
					int previous = index - (direction * moveCounter * step);
					int current = index - (direction * (moveCounter - 1) * step);
					if (previous < 0 || previous > grid.length || previous == start - direction) {
						break;
					}
					if (!grid[previous].equals(COLOUR.EMPTY)) {
						break;
					}
					grid[previous] = grid[current];
					grid[current] = COLOUR.EMPTY;
					moveCounter++;
				}
			}
			counter++;
		}
	}

	private void moveHorizontal(boolean moveLeft, int n) {
		// int counter = 0;
		int start = moveLeft ? n * BOARD_SIZE : n * BOARD_SIZE + (BOARD_SIZE - 1);
		int direction = moveLeft ? 1 : -1;
		int step = 1;

		moveLine(start, direction, step);
	}

	private void moveVertical(boolean moveUp, int n) {
		// int counter = 0;
		int start = moveUp ? n : n + (BOARD_SIZE * (BOARD_SIZE - 1));
		int direction = moveUp ? 1 : -1;
		int step = BOARD_SIZE;

		moveLine(start, direction, step);
	}

	private boolean horizontalHasEmpty(int n) {
		int modN = n;
		if (modN > BOARD_SIZE - 1) {
			modN -= BOARD_SIZE;
		}
		COLOUR[] column = Arrays.copyOfRange(grid, modN * BOARD_SIZE, 
				modN * BOARD_SIZE + BOARD_SIZE);
		for (COLOUR c : column) {
			if (c == COLOUR.EMPTY) {
				return true;
			}
		}
		return false;
	}

	private boolean verticalHasEmpty(int n) {
		int modN = n;
		if (modN > 20) {
			modN -= BOARD_SIZE;
		}
		for (int i = 0; i < BOARD_SIZE; i++) {
			if (grid[(modN - 2 * BOARD_SIZE) + i * BOARD_SIZE].equals(COLOUR.EMPTY)) {
				return true;
			}
		}
		return false;
	}

	private void calculateNextPossibleMoves() {
		List<Move> nextLegalMoves = new ArrayList<Move>();

		List<Board> nextSingleMoveBoards = new ArrayList<Board>();
		// generate all single moves
		for (int move = 0; move < BOARD_SIZE * 4; move++) {
			Board nextBoard = deepCopy();
			nextSingleMoveBoards.add(nextBoard);
			List<COLOUR> wonBalls = nextBoard.tryMove(move, true);
			if (wonBalls == null) {
				continue;
			}
			nextLegalMoves.add(new Move(nextBoard, move, wonBalls));
		}

		// if single move possible only return single moves
		if (nextLegalMoves.size() > 0) {
			possibleNextMoves = nextLegalMoves;
			return;
		}

		// check for legal double moves
		for (int firstMove = 0; firstMove < nextSingleMoveBoards.size(); firstMove++) {
			{
				for (int secondMove = 0; secondMove < BOARD_SIZE * 4; secondMove++) {
					Board nextNextBoard = deepCopy();
					List<COLOUR> wonBalls = nextNextBoard.tryMove(secondMove, true);
					if (wonBalls == null) {
						continue;
					}
					nextLegalMoves.add(new Move(nextNextBoard, 
							new int[] {firstMove, secondMove}, wonBalls));
				}
			}
		}

		possibleNextMoves = nextLegalMoves;
	}

	// before this is called, a move should have been made on the board
	private List<COLOUR> removeBalls() {
		List<COLOUR> connectedBalls = new ArrayList<COLOUR>();
		List<Integer> connectedBallsIndex = new ArrayList<Integer>();

		for (int i = 0; i < BOARD_SIZE; i++) {
			// check for each column for connected balls and mark them
			for (int x = 1; x < BOARD_SIZE; x++) {
				{
					int index = x + BOARD_SIZE * i;
					int neighbour = index - 1;
					if (!hasIdenticalNeighbour(grid[index], neighbour)) {
						continue;
					}
					connectedBallsIndex.add(index);
					if (connectedBallsIndex.contains(neighbour)) {
						continue;
					}
					connectedBallsIndex.add(neighbour);
				}
			}

			// check for each row for connected balls and mark them
			for (int y = 1; y < BOARD_SIZE; y++) {
				int index = i + BOARD_SIZE * y;
				int neighbour = index - BOARD_SIZE;
				if (!hasIdenticalNeighbour(grid[index], neighbour)) {
					continue;
				}
				connectedBallsIndex.add(index);
				if (connectedBallsIndex.contains(neighbour)) {
					continue;
				}
				connectedBallsIndex.add(neighbour);
			}
		}

		for (int index : connectedBallsIndex) {
			connectedBalls.add(grid[index]);
			grid[index] = COLOUR.EMPTY;
		}

		return connectedBalls;
	}

	// DEBUG METHODS
	// -----------------------------------------------------------------------------------

	public static void main(String[] args) {
		Board b = new Board();
		System.out.println(b.toString());
		System.out.println(b.boardHasNeighbours());
		System.out.println(b.countColours());
		System.out.println(b.getPossibleMoves().get(0).move[0]);
		if (b.makeMove(new int[] {3}) != null) {
			System.out.println("Cool");
		}
		System.out.println(b.toString());
		System.out.println(b.getPossibleMoves().get(0).move[0]);
		if (b.makeMove(new int[] {14}) != null) {
			System.out.println("Cool");
		}
		System.out.println(b.toString());
	}

	public String countColours() {
		int[] colourCounter = new int[AVAILABLE_COLOURS.length];

		for (COLOUR c : grid) {
			colourCounter[c.getValue()]++;
		}

		String block = "";
		int counter = 0;
		for (int i : colourCounter) {
			block += AVAILABLE_COLOURS[counter].toString() + ": " + i + System.lineSeparator();
			counter++;
		}

		return block;
	}
}
