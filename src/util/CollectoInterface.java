package util;

import java.util.Scanner;

import game.Board;
import game.Board.Move;

public class CollectoInterface {

	public static final String CONSOLE_INDICATOR = "> ";
	public static final String INPUT_INDICATOR = ": ";
	public static final String TAB = "    ";
	private static final String SHOW_MOVES = "Possible moves are: ";
	
	public static Scanner scanner = new Scanner(System.in);
	
	public static void showMessage(String msg) {
		System.out.println(CONSOLE_INDICATOR + msg);
	}

	public static void showInput() {
		System.out.print(INPUT_INDICATOR);
	}

	public static void showOptions(String[] options) {
		int counter = 0;
		for (String option : options) {
			System.out.println(TAB + counter++ + ": " + option);
		}
	}

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

	public static String requestInput(String msg) {
		showMessage(msg);
		showInput();
		return scanner.nextLine();
	}
	
	public static void showPossibleMoves(Board board) {
		String moves = System.lineSeparator();
		for (Move m : board.getPossibleMoves()) {
			moves += CollectoInterface.TAB + m.toString() + System.lineSeparator();
		}
		CollectoInterface.showMessage(SHOW_MOVES + moves);
	}
}
