package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Class Board.
 */
public class Board {

	/**
	 * The Class Move. Move is used to store the possible moves on the current
	 * Board. It also stores the resulting Board of the move and a List containing
	 * the balls which are won by playing that move.
	 */
	public class Move {

		/** The Board which results from making this move on the current Board */
		public Board board;

		/**
		 * The move as an Integer array. If the move is a single move, the length of the
		 * array is 1, else the length is 2. This format is used for all functions
		 * handling move logic.
		 */
		public int[] move;

		/** The balls which are removed from the board as a result of this move. */
		public List<COLOUR> gainedBalls;

		/**
		 * Instantiates a new Move for a single move
		 * 
		 * @requires board != null, move != null, gainedBalls != null.
		 * @ensures this.board = board, this.move = new int[] {move}, this.gainedBalls =
		 *          gainedBalls.
		 * @param board:       the resulting Board
		 * @param move:        the move
		 * @param gainedBalls: the gained balls
		 */
		// move is the number of the move that results in the given board
		public Move(Board board, int move, List<COLOUR> gainedBalls) {
			this.board = board;
			this.move = new int[] { move };
			this.gainedBalls = gainedBalls;
		}

		/**
		 * Instantiates a new Move for a double move.
		 *
		 * @requires board != null, move != null, gainedBalls != null.
		 * @ensures this.board = board, this.move = move, this.gainedBalls =
		 *          gainedBalls.
		 * @param board:       the resulting Board
		 * @param move:        the move
		 * @param gainedBalls: the gained balls
		 */
		// move is the number of the move that results in the given board
		public Move(Board board, int[] move, List<COLOUR> gainedBalls) {
			this.board = board;
			this.move = move;
			this.gainedBalls = gainedBalls;
		}

		/**
		 * To string. Prints the move with spaces in between the numbers if it is a
		 * double move.
		 *
		 * @return the String representation of the Move
		 */
		public String toString() {
			String moves = "";
			for (int m : move) {
				moves += m + " ";
			}
			return moves;
		}
	}

	/** Length of one side of the Board. */
	public static final int BOARD_SIZE = 7;

	/** Amount of total spaces on the Board. */
	public static final int BOARD_LENGTH = BOARD_SIZE * BOARD_SIZE;

	/** The index of the square at the centre of the Board */
	public static final int CENTER = (BOARD_SIZE * BOARD_SIZE - 1) / 2;

	/**
	 * All of the possible colours of balls on the grid. Also contains EMPTY, which
	 * represents an empty space on the grid.
	 */
	public static final COLOUR[] AVAILABLE_COLOURS = COLOUR.values();

	public static final String BOARD_PADDING_LEFT = "%-6s|";
	public static final String BOARD_PADDING_RIGHT = "%6s";
	public static final String BOARD_SEPERATOR = "------------------------------------------";
	public static final String BOARD_ROW = "      -----------------------------";
	public static final String BOARD_TOP = "        21  22  23  24  25  26  27" + System.lineSeparator()
			+ "        |   |   |   |   |   |   |" + System.lineSeparator() + "        v   v   v   v   v   v   v"
			+ System.lineSeparator();
	public static final String BOARD_BOTTOM = "        ^   ^   ^   ^   ^   ^   ^" + System.lineSeparator()
			+ "        |   |   |   |   |   |   |" + System.lineSeparator() + "        14  15  16  17  18  19  20"
			+ System.lineSeparator();

	/** The grid representing the squares of the Board. */
	public COLOUR[] grid = new COLOUR[BOARD_SIZE * BOARD_SIZE];

	/** A list of the next possible moves that can be played on the current Board */
	private List<Move> possibleNextMoves = new ArrayList<Move>();

	/** A list of COLOURs representing the balls won by player one. */
	public List<COLOUR> p1Balls = new ArrayList<COLOUR>();

	/** A list of COLOURs representing the balls won by player two. */
	public List<COLOUR> p2Balls = new ArrayList<COLOUR>();

	/** Indicates who's turn it is. When it's true, it's the first player's turn */
	public boolean firstPlayerTurn = true;

	/** The amount of balls of the same colour to equal a single point. */
	public static int BALLS_PER_POINT = 3;

	// Public methods and constructors
	// ----------------------------------------------------------------

	/**
	 * Instantiates a new board and generates a random valid starting grid.
	 * 
	 * @ensures for (COLOUR c : grid){c != null}, !boardHasNeighbours(),
	 *          grid[CENTER] = COLOUR.EMPTY.
	 */
	public Board() {
		while (true) {
			generateBoard();
			calculateNextPossibleMoves();
			if (possibleNextMoves.size() > 0)
				break;
		}
	}

	/**
	 * Instantiates a new board and assigns the values of boardValues to the grid.
	 *
	 * @requires boardValues != null, boardValues.length == 49, boardValues[CENTER]
	 *           == COLOUR.EMPTY.
	 * @ensures for (COLOUR c : grid){c != null}.
	 * @param boardValues: the values of the grid
	 */
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

	/**
	 * Instantiates a new board and clones the passed arguments to assign them to
	 * its own global variables. This constructor only used by deepCopy(). This
	 * constructor does not calculate the next moves since it is a copy of the
	 * current Board.
	 * 
	 * @requires grid != null, nextMoves != null, p1Balls != null, p2Balls != null.
	 * @ensures this.grid = grid.clone(), this.possibleNextMoves.addAll(nextMoves),
	 *          this.p1Balls.addAll(p1Balls) this.p2Balls.addAll(p2Balls).
	 *          this.firstPlayerTurn = firstPlayerTurn;
	 * @param grid:            the grid
	 * @param nextMoves:       the next possible moves
	 * @param p1Balls:         p1 balls
	 * @param p2Balls:         p2 balls
	 * @param firstPlayerTurn: whether it's currently the first player's turn
	 */
	// calculating the moves
	public Board(COLOUR[] grid, List<Move> nextMoves, List<COLOUR> p1Balls, List<COLOUR> p2Balls,
			boolean firstPlayerTurn) {
		this.grid = grid.clone();
		this.possibleNextMoves.addAll(nextMoves);
		this.p1Balls.addAll(p1Balls);
		this.p2Balls.addAll(p2Balls);
		this.firstPlayerTurn = firstPlayerTurn;
	}

	/**
	 * Play the passed move on the current Board. isValidMove() should be called
	 * before calling this method on the server.
	 * 
	 * @requires isValidMove(int[] move).
	 * @ensures grid = findMove(move) if findMove(move) != null,
	 *          p1Balls.addAll(m.gainedBalls) if firstPlayerTurn,
	 *          p2Balls.addAll(m.gainedBalld) if !firstPlayerTurn.
	 * @param move: the move to be played on the Board.
	 */
	public void makeMove(int[] move) {
		// search for calculated legal move and copy it's grid
		// recalculate next moves
		Move m = findMove(move);

		grid = m.board.grid;
		if (firstPlayerTurn) {
			p1Balls.addAll(m.gainedBalls);
		} else {
			p2Balls.addAll(m.gainedBalls);
		}

		firstPlayerTurn = !firstPlayerTurn;

		calculateNextPossibleMoves();
	}

	/**
	 * Checks if the passed move is a valid move.
	 *
	 * @param move: the move to be tested
	 * @return true if move is valid, else false
	 */
	public boolean isValidMove(int[] move) {
		if (findMove(move) == null) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a List containing the calculated next possible moves for this Board.
	 *
	 * @return List<Move> containing Moves that can be played on the current Board
	 */
	public List<Move> getPossibleMoves() {
		return possibleNextMoves;
	}

	/**
	 * Checks whether the current grid on the Board has at least two balls of the
	 * same COLOUR, which are not COLOUR.EMPTY, and are adjacent to each other
	 * horizontally or vertically.
	 * 
	 * @return true if the current grid has at least 2 spaces adjacent to each other
	 *         which have the same COLOUR, and that said COLOUR is not COLOUR.EMPTY
	 */
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

	/**
	 * Checks whether there are any moves left for the current Board by counting the
	 * amount of possible moves.
	 *
	 * @return true if the amount of possibleNextMoves is 0, else false
	 */
	public boolean noMovesLeft() {
		if (possibleNextMoves.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Makes a deep copy of this board and returns the instance.
	 *
	 * @return Board which is a deep copy of this Board.
	 */
	public Board deepCopy() {
		return new Board(grid, possibleNextMoves, p1Balls, p2Balls, firstPlayerTurn);
	}

	/**
	 * Converts the grids of the board to a readable form.
	 * 
	 * @return the String representation of this Board
	 */
	public String toString() {
		String board = BOARD_SEPERATOR + System.lineSeparator() + BOARD_TOP;
		for (int y = 0; y < BOARD_SIZE; y++) {
			String line = String.format(BOARD_PADDING_LEFT, (y + BOARD_SIZE) + "-->");
			for (int x = 0; x < BOARD_SIZE; x++) {
				line += " " + (grid[x + y * BOARD_SIZE].getValue()) + " |";
			}
			board += line + String.format(BOARD_PADDING_RIGHT, "<--" + (y)) + System.lineSeparator();
			board += BOARD_ROW + System.lineSeparator();
		}
		board += BOARD_BOTTOM + System.lineSeparator() + BOARD_SEPERATOR;
		return board;
	}

	/**
	 * Return the passed move as Integer array as a human readable string.
	 * 
	 * @requires move != null.
	 * @param move: the move as an Integer array
	 * @return String representation of the Integer array representation of a move
	 */
	public static String moveToReadableString(int[] move) {
		String returnString = "";
		for (int m : move) {
			returnString += m + " ";
		}
		return returnString;
	}

	/**
	 * Count the amount of points of a given player. The given player is indicated
	 * through the passed boolean.
	 *
	 * @param playerOne: true if the function should return the amount of points for
	 *                   the first player, else it will return the amount of points
	 *                   for the second player.
	 * @return int, the amount of points currently in possession for the given
	 *         player.
	 */
	public int countPoints(boolean playerOne) {
		List<COLOUR> balls = playerOne ? p1Balls : p2Balls;

		int[] ballCounter = new int[COLOUR.values().length];
		for (int i = 0; i < balls.size(); i++) {
			ballCounter[balls.get(i).getValue()]++;
		}

		int totalPoints = 0;

		for (int amount : ballCounter) {
			int excess = amount % BALLS_PER_POINT;
			totalPoints += (amount - excess) / BALLS_PER_POINT;
		}
		return totalPoints;
	}

	// Private methods
	// -------------------------------------------------------------------------------

	/**
	 * Finds the Move inside of the List possibleNextMoves containing the passed
	 * Integer array as its internal variable Move.move.
	 *
	 * @requires move != null.
	 * @param move: the move to be searched for as an Integer array
	 * @return Move corresponding to the move passed as a parameter. If no Move in
	 *         possibleNextMoves corresponds to the passed parameter, this will
	 *         return null.
	 */
	private Move findMove(int[] move) {
		for (Move m : possibleNextMoves) {
			if (Arrays.equals(m.move, move)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Generates board and initializes the grid of this Board.
	 * 
	 * @ensures for (COLOUR c : grid){c != null}, grid[CENTER] = COLOUR.EMPTY,
	 *          !boardHasNeighbours().
	 */
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

		if (boardHasNeighbours() || !grid[CENTER].equals(COLOUR.EMPTY)) {
			generateBoard();
		}
	}

	/**
	 * Finds the next index in grid, for which the COLOUR is not equal to the COLOUR
	 * of the passed index and the COLOUR is not COLOUR.EMPTY, or adjacent to the
	 * same COLOUR horizontally or vertically.
	 *
	 * @requires index > -1, index < grid.length.
	 * @param index: the index from which to start searching ahead.
	 * @return index of the next space in the grid which is not equal to the COLOUR
	 *         of the passed index, is not COLOUR.EMPTY, or adjacent to its own
	 *         COLOUR
	 */
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

	/**
	 * Returns a positive index of grid if there are any spaces adjacent to the
	 * passed index which are equal to the passed COLOUR. If there are no
	 * neighbours, return -1.
	 *
	 * @requires index > -1, index < grid.length.
	 * @param index:  the index of the space to check for adjacent spaces
	 * @param colour: the COLOUR for which to compare
	 * @return returns -1 if no same colour neighbours, else returns index of space
	 *         which has same COLOUR as passed index
	 */
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

	/**
	 * Returns a positive index of grid if there are any spaces adjacent to the
	 * passed index which are equal to the COLOUR of the passed index. If there are
	 * no neighbours, return -1.
	 *
	 * @requires index > -1, index < grid.length.
	 * @param index: the index of the space to check for adjacent spaces
	 * @return returns -1 if no same colour neighbours, else returns index of space
	 *         which has same COLOUR as passed index
	 */
	// else returns one of the indexes of the same colour neighbours
	private int getIdenticalNeighbour(int index) {
		return getIdenticalNeighbour(index, grid[index]);
	}

	/**
	 * Checks whether the passed index is equals to the passed COLOUR.
	 * 
	 * @requires index > -1, index < grid.length, colour != null.
	 * @param colour:         the COLOUR to compare with the COLOUR of
	 *                        grid[neighbourIndex]
	 * @param neighbourIndex: the index of the square to be compared against the
	 *                        passed colour.
	 * @return true the passed COLOUR and the COLOUR of grid[neighbourIndex] are the
	 *         same, else false.
	 */
	private boolean hasIdenticalNeighbour(COLOUR colour, int neighbourIndex) {
		if (grid[neighbourIndex].equals(colour)) {
			return true;
		}
		return false;
	}

	/**
	 * Physically move the COLOURs on the grid. This method should only be called by
	 * calculateMoves(). This method is only used to calculate the next moves, not
	 * to have a player actually make a move.
	 *
	 * @requires move > -1, move < BOARD_SIZE * 4.
	 * @param move: the move to try to make on the current Board
	 * @return a List containing all of the balls/COLOURs which are removed from the
	 *         Board after making the passed move. Will return null if the move was
	 *         invalid.
	 */
	private List<COLOUR> tryMove(int move) {
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

		if (!boardHasNeighbours()) {
			return null;
		}

		return removeBalls();
	}

	/**
	 * Shift each space with a non empty COLOUR on the given row or column into the
	 * given direction.
	 *
	 * @requires start > -1, start < grid.length, direction == 1 || direction == -1,
	 *           step == 1 || direction = BOARD.SIZE.
	 * @ensures all COLOURs in the designated line are moved in the given direction.
	 * @param start     the index from which to start shifting spaces
	 * @param direction the direction towards where the spaces are shifting,
	 *                  positive for moving spaces to the right and down and
	 *                  negative for left and up.
	 * @param step      the step between the indices which are shifted: should be 1
	 *                  for horizontal shifts and BOARD_SIZE for a vertical shift.
	 */
	private void moveLine(int start, int direction, int step) {
		int counter = 0;
		while (counter != BOARD_SIZE) {
			int index = start + (direction * counter * step);
			if (!grid[index].equals(COLOUR.EMPTY)) {
				int moveCounter = 1;
				while (true) {
					int previous = index - (direction * moveCounter * step);
					int current = index - (direction * (moveCounter - 1) * step);
					if (previous < 0 || previous > grid.length - 1 || previous == start - direction) {
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

	/**
	 * Move horizontal line of grid. Calls moveLine() to shift the COLOURs of a row
	 * to the left or right depending on the passed parameter.
	 *
	 * @requires n > -1, n < BOARD_SIZE * 2.
	 * @param moveLeft: true if the row should be shifted to the left.
	 * @param n:        the index of the move to be played, see also the move
	 *                  protocols of Collecto.
	 */
	private void moveHorizontal(boolean moveLeft, int n) {
		// int counter = 0;
		int start = moveLeft ? n * BOARD_SIZE : n * BOARD_SIZE + (BOARD_SIZE - 1);
		int direction = moveLeft ? 1 : -1;
		int step = 1;

		moveLine(start, direction, step);
	}

	/**
	 * Move vertical line of grid. Calls moveLine() to shift the COLOURs of a column
	 * to up or down depending on the passed parameter.
	 *
	 * @requires n > 13, n < BOARD_SIZE * 4.
	 * @param moveLeft: true if the column should be shifted upwards.
	 * @param n:        the index of the move to be played, see also the move
	 *                  protocols of Collecto.
	 */
	private void moveVertical(boolean moveUp, int n) {
		// int counter = 0;
		int start = moveUp ? n : n + (BOARD_SIZE * (BOARD_SIZE - 1));
		int direction = moveUp ? 1 : -1;
		int step = BOARD_SIZE;

		moveLine(start, direction, step);
	}

	/**
	 * Returns the balls currently adjacent to each other with the same COLOUR and
	 * replaces their space in grid with COLOUR.EMPTY.
	 *
	 * @return the balls which have been removed from the grid.
	 */
	private List<COLOUR> removeBalls() {
		List<COLOUR> connectedBalls = new ArrayList<COLOUR>();
		List<Integer> connectedBallsIndex = new ArrayList<Integer>();

		for (int i = 0; i < BOARD_SIZE; i++) {
			// check for each column for connected balls and mark them
			for (int x = 1; x < BOARD_SIZE; x++) {
				{
					int index = x + BOARD_SIZE * i;
					int neighbour = index - 1;
					if (!hasIdenticalNeighbour(grid[index], neighbour) || grid[index].equals(COLOUR.EMPTY)) {
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
				if (!hasIdenticalNeighbour(grid[index], neighbour) || grid[index].equals(COLOUR.EMPTY)) {
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

	/**
	 * Checks if the given row or column has an empty space.
	 *
	 * @requires move > -1, move < BOARD_SIZE * 4.
	 * @param move: the column or row to check: 0 to 13 checks columns, 14 to 27
	 *              checks rows
	 * @return true if there is an empty space in either the row or column checked.
	 */
	private boolean hasEmpty(int move) {

		// check if move is with row or column
		// check if empty spot in row or column
		// N between 0 and 6: push row N to left
		// N between 7 and 13: push row (N-7) to right
		// N between 14 and 20: push column (N-14) upwards
		// N between 21 and 27: push column (N-21) downwards

		if (move < BOARD_SIZE * 2) {
			// check horizontal
			if (horizontalHasEmpty(move)) {
				return true;
			}
		} else if (move < BOARD_SIZE * 4) {
			// check vertical
			if (verticalHasEmpty(move)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if horizontal row has empty space.
	 *
	 * @requires n > -1, n < BOARD_SIZE * 2.
	 * @param n: the index of the move corresponding to the row
	 * @return true if row in grid contains at least one space with COLOUR.EMPTY.
	 */
	private boolean horizontalHasEmpty(int n) {
		int modN = n;
		if (modN > BOARD_SIZE - 1) {
			modN -= BOARD_SIZE;
		}
		COLOUR[] column = Arrays.copyOfRange(grid, modN * BOARD_SIZE, modN * BOARD_SIZE + BOARD_SIZE);
		for (COLOUR c : column) {
			if (c == COLOUR.EMPTY) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if vertical column has empty space.
	 *
	 * @requires n > 13, n < BOARD_SIZE * 4.
	 * @param n: the index of the move corresponding to the column
	 * @return true if column in grid contains at least one space with COLOUR.EMPTY.
	 */
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

	/**
	 * Calculate next possible moves and add them to possibleNextMoves();
	 */
	private void calculateNextPossibleMoves() {
		List<Move> nextLegalMoves = new ArrayList<Move>();

		List<Board> nextSingleMoveBoards = new ArrayList<Board>();
		// generate all single moves
		for (int move = 0; move < BOARD_SIZE * 4; move++) {
			Board nextBoard = deepCopy();
			nextSingleMoveBoards.add(nextBoard);
			List<COLOUR> wonBalls = nextBoard.tryMove(move);
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
					Board nextNextBoard = nextSingleMoveBoards.get(firstMove).deepCopy();
					List<COLOUR> wonBalls = nextNextBoard.tryMove(secondMove);
					if (wonBalls == null) {
						continue;
					}
					nextLegalMoves.add(new Move(nextNextBoard, new int[] { firstMove, secondMove }, wonBalls));
				}
			}
		}

		possibleNextMoves = nextLegalMoves;
	}

	// DEBUG METHODS
	// -----------------------------------------------------------------------------------

	/*
	 * public static void main(String[] args) { Board b = new Board();
	 * System.out.println(b.toString()); System.out.println(b.boardHasNeighbours());
	 * System.out.println(b.countColours());
	 * System.out.println(b.getPossibleMoves().get(0).move[0]); if
	 * (b.isValidMove(new int[] { 3 })) { b.makeMove(new int[] { 3 });
	 * System.out.println("Cool"); } System.out.println(b.toString());
	 * System.out.println(b.getPossibleMoves().get(0).move[0]); if
	 * (b.isValidMove(new int[] { 14 })) { b.makeMove(new int[] { 14 });
	 * System.out.println("Cool"); } System.out.println(b.toString()); }
	 * 
	 * public String countColours() { int[] colourCounter = new
	 * int[AVAILABLE_COLOURS.length];
	 * 
	 * for (COLOUR c : grid) { colourCounter[c.getValue()]++; }
	 * 
	 * String block = ""; int counter = 0; for (int i : colourCounter) { block +=
	 * AVAILABLE_COLOURS[counter].toString() + ": " + i + System.lineSeparator();
	 * counter++; }
	 * 
	 * return block; }
	 */
}
