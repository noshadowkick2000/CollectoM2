package game;

public enum COLOUR {
	EMPTY(0), RED(1), GREEN(2), BLUE(3), CYAN(4), YELLOW(5), PURPLE(6);

	private final int value;

	COLOUR(final int newValue) {
		value = newValue;
	}

	public int getValue() {
		return value;
	}

	//implementation for colour in console does not work, reason unknown
	/*
	public static String getConsoleColour(COLOUR colour) {
		return COLOUR_STRINGS[colour.getValue()];
	}

	public static final char PRECHAR = (char)27;
	public static final String EMPTY_STRING = PRECHAR + "[30m";
	public static final String RED_STRING = PRECHAR + "[31m";
	public static final String GREEN_STRING = PRECHAR + "[32m";
	public static final String BLUE_STRING = PRECHAR + "[34m";
	public static final String CYAN_STRING = PRECHAR + "[36m";
	public static final String YELLOW_STRING = PRECHAR + "[33m";
	public static final String PURPLE_STRING = PRECHAR + "[35m";

	public static final String[] COLOUR_STRINGS = new String[] { EMPTY_STRING, RED_STRING, GREEN_STRING, BLUE_STRING,
			CYAN_STRING, YELLOW_STRING, PURPLE_STRING };

	public static final String RESET = PRECHAR + "[0m";*/
}
