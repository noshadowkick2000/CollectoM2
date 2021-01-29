/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package game;

// TODO: Auto-generated Javadoc
/**
 * The Enum COLOUR.
 */
public enum COLOUR {

	/** Empty space */
	EMPTY(0),
	/** Red ball */
	RED(1),
	/** Green ball */
	GREEN(2),
	/** Blue ball */
	BLUE(3),
	/** Cyan ball */
	CYAN(4),
	/** Yellow ball */
	YELLOW(5),
	/** Purple ball */
	PURPLE(6);

	private final int value;

	/**
	 * Instantiates a new COLOUR.
	 *
	 * @param newValue: the numerical representation of the COLOUR
	 */
	COLOUR(final int newValue) {
		value = newValue;
	}

	/**
	 * Gets the numerical value of this COLOUR. All numbers higher than 0 correspond
	 * with a coloured ball, 0 itself corresponds with an empty space.
	 *
	 * @return the value of this COLOUR
	 */
	public int getValue() {
		return value;
	}

	// implementation for colour in console does not work, reason unknown
	/*
	 * public static String getConsoleColour(COLOUR colour) { return
	 * COLOUR_STRINGS[colour.getValue()]; }
	 * 
	 * public static final char PRECHAR = (char)27; public static final String
	 * EMPTY_STRING = PRECHAR + "[30m"; public static final String RED_STRING =
	 * PRECHAR + "[31m"; public static final String GREEN_STRING = PRECHAR + "[32m";
	 * public static final String BLUE_STRING = PRECHAR + "[34m"; public static
	 * final String CYAN_STRING = PRECHAR + "[36m"; public static final String
	 * YELLOW_STRING = PRECHAR + "[33m"; public static final String PURPLE_STRING =
	 * PRECHAR + "[35m";
	 * 
	 * public static final String[] COLOUR_STRINGS = new String[] { EMPTY_STRING,
	 * RED_STRING, GREEN_STRING, BLUE_STRING, CYAN_STRING, YELLOW_STRING,
	 * PURPLE_STRING };
	 * 
	 * public static final String RESET = PRECHAR + "[0m";
	 */
}
