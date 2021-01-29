package util;

/**
 * The Class Communications. Contains all of the protocols for the Collecto
 * assignment as per the given instructions. Full explanation of the usage can
 * be found on:
 * https://gitlab.utwente.nl/tvandijk/collecto-communications-protocol/-/blob/master/commands.md
 */
public class Communications {

	/** Protocol for HELLO. */
	public static final String H = "HELLO";

	/** Protocol for LOGIN. */
	public static final String L = "LOGIN";

	/** Protocol for HELLO.ALREADYLOGGEDIN. */
	public static final String AL = "ALREADYLOGGEDIN";

	/** Protocol for NEWGAME. */
	public static final String NG = "NEWGAME";

	/** Protocol for GAMEOVER. */
	public static final String GO = "GAMEOVER";

	/** Protocol for GAMEOVER~DRAW. */
	public static final String DRAW = "DRAW";

	/** Protocol for GAMEOVER~VICTORY. */
	public static final String VICTORY = "VICTORY";

	/** Protocol for GAMEOVER~DISCONNECT. */
	public static final String DISCONNECT = "DISCONNECT";

	/** Protocol for LIST. */
	public static final String LS = "LIST";

	/** Protocol for QUEUE. */
	public static final String Q = "QUEUE";

	/** Protocol for MOVE. */
	public static final String M = "MOVE";

	/** Message delimiter for all protocol messages. */
	public static final String DELIM = "~";

	/** Protocol for ERROR. */
	public static final String ERR = "ERROR";
}
