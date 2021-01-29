/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package util;

import java.util.Scanner;

import game.Board;
import game.Board.Move;

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
	 * Enable console prints.
	 */
	public static void enableConsole() {
		consoleEnabled = true;
	}

	/**
	 * Disable console prints.
	 */
	public static void disableConsole() {
		consoleEnabled = false;
	}

	/**
	 * Show debug message. This method is used for debugging purposes. Since all of
	 * the normal console prints use showMessage, only messages using
	 * showDebugMessage will be printed after disableConsole().
	 *
	 * @param msg: the message to be printed to the console.
	 */
	public static void showDebugMessage(String msg) {
		System.out.println(msg);
	}

	/**
	 * Show message. In case consoleEnabled == false, no message will be printed.
	 * For printing debugger messages when the console is disabled, see
	 * showDebugMessage().
	 *
	 * @param msg: the message to be printed to the console.
	 */
	public static void showMessage(String msg) {
		if (consoleEnabled) {
			System.out.println(CONSOLE_INDICATOR + msg);
		}
	}

	/**
	 * Print indicator for indicating a user input request. Call this each time
	 * before the user is required to input something to the console.
	 */
	public static void showInput() {
		System.out.print(INPUT_INDICATOR);
	}

	/**
	 * Show all of the Strings inside of the passed array options as a list of
	 * options on the console.
	 * 
	 * @requires options != null.
	 * @param options: the options to choose from.
	 */
	public static void showOptions(String[] options) {
		int counter = 0;
		for (String option : options) {
			System.out.println(TAB + counter++ + ": " + option);
		}
	}

	/**
	 * Request multiple choice input from the user in the console. This method may
	 * block while waiting for input.
	 *
	 * @requires options != null.
	 * @param options: the options to choose from.
	 * @return a String from options or an empty String if an invalid input was
	 *         given not corresponding to any of the options.
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
	 * Request input from the user in the console. This method may block while
	 * waiting for input.
	 * 
	 * @requires msg != null.
	 * @param msg: the message to be printed before asking for input.
	 * @return a String containing the next line of input given by the user.
	 */
	public static String requestInput(String msg) {
		showMessage(msg);
		showInput();
		return scanner.nextLine();
	}

	/**
	 * Print possible moves of the passed board in human readable form.
	 * 
	 * @requires board != null.
	 * @param board: the board from which to get the possiblemoves.
	 */
	public static void showPossibleMoves(Board board) {
		String moves = System.lineSeparator();
		for (Move m : board.getPossibleMoves()) {
			moves += CollectoInterface.TAB + m.toString() + System.lineSeparator();
		}
		CollectoInterface.showMessage(SHOW_MOVES + moves);
	}

	/**
	 * Print board to the console in human readable form.
	 *
	 * @requires board != null.
	 * @param board: the board to print.
	 */
	public static void showBoard(Board board) {
		CollectoInterface.showMessage(System.lineSeparator() + board.toString());
	}
}
