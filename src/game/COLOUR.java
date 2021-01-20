package game;

public enum COLOUR {
	EMPTY(0),
	RED(1),
	GREEN(2),
	BLUE(3),
	ORANGE(4),
	YELLOW(5),
	PURPLE(6);
	
	private final int value;

    COLOUR(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
