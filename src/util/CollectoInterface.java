package util;

import java.util.Scanner;

import game.Board;
import game.Board.Move;

// TODO: Auto-generated Javadoc
/**
 * The Class CollectoInterface.
 */
public class CollectoInterface {

	/** The Constant CONSOLE_INDICATOR. */
	public static final String CONSOLE_INDICATOR = "> ";
	
	/** The Constant INPUT_INDICATOR. */
	public static final String INPUT_INDICATOR = ": ";
	
	/** The Constant TAB. */
	public static final String TAB = "    ";
	
	/** The Constant SHOW_MOVES. */
	private static final String SHOW_MOVES = "Possible moves are: ";

	/** The scanner. */
	public static Scanner scanner = new Scanner(System.in);

	/** The console enabled. */
	private static boolean consoleEnabled = true;

	/**
	 * Enable console.
	 */
	public static void enableConsole() {
		consoleEnabled = true;
	}

	/**
	 * Disable console.
	 */
	public static void disableConsole() {
		consoleEnabled = false;
	}
	
	/**
	 * Show debug message.
	 *
	 * @param msg the msg
	 */
	public static void showDebugMessage(String msg) {
		System.out.println(msg);
	}

	/**
	 * Show message.
	 *
	 * @param msg the msg
	 */
	public static void showMessage(String msg) {
		if (consoleEnabled) {
			System.out.println(CONSOLE_INDICATOR + msg);
		}
	}

	/**
	 * Show input.
	 */
	public static void showInput() {
		System.out.print(INPUT_INDICATOR);
	}

	/**
	 * Show options.
	 *
	 * @param options the options
	 */
	public static void showOptions(String[] options) {
		int counter = 0;
		for (String option : options) {
			System.out.println(TAB + counter++ + ": " + option);
		}
	}

	/**
	 * Request option.
	 *
	 * @param options the options
	 * @return the string
	 */
	public static String requestOption(String[] options) {
		showOptions(options);
		int index;
		try {
			showInput();
			index = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			return "";
		}
		return options[index];
	}

	/**
	 * Request input.
	 *
	 * @param msg the msg
	 * @return the string
	 */
	public static String requestInput(String msg) {
		showMessage(msg);
		showInput();
		return scanner.nextLine();
	}

	/**
	 * Show possible moves.
	 *
	 * @param board the board
	 */
	public static void showPossibleMoves(Board board) {
		String moves = System.lineSeparator();
		for (Move m : board.getPossibleMoves()) {
			moves += CollectoInterface.TAB + m.toString() + System.lineSeparator();
		}
		CollectoInterface.showMessage(SHOW_MOVES + moves);
	}

	/**
	 * Show board.
	 *
	 * @param board the board
	 */
	public static void showBoard(Board board) {
		CollectoInterface.showMessage(System.lineSeparator() + board.toString());
	}
}
