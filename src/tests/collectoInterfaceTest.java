/*
 * Collecto Network System
 * University of Twente
 * Tim Yeung s2085615
 * January 2021
 */
package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import util.CollectoInterface;

class collectoInterfaceTest {

	public static String OPTION_ONE = "One";
	public static String OPTION_TWO = "Two";
	public static String OPTION_THREE = "Three";
	public static String[] OPTIONS = new String[] { OPTION_ONE, OPTION_TWO, OPTION_THREE };

	@Test
	void testInput() {
		// type "One" and press enter
		assertEquals(OPTION_ONE, CollectoInterface.requestInput("Type " + OPTION_ONE + " and press enter"));
	}

	@Test
	void testOption() {
		CollectoInterface.showMessage("type first option and press enter");
		// type first option and press enter
		assertEquals(OPTION_ONE, CollectoInterface.requestOption(OPTIONS));
		CollectoInterface.showMessage("type second option and press enter");
		// type second option and press enter
		assertEquals(OPTION_TWO, CollectoInterface.requestOption(OPTIONS));
		CollectoInterface.showMessage("type third option and press enter");
		// type third option and press enter
		assertEquals(OPTION_THREE, CollectoInterface.requestOption(OPTIONS));

	}
}
