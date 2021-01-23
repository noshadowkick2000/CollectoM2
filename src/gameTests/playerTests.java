package gameTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import game.*;

class playerTests {

	private Player player;
	
	private List<COLOUR> ballHolder = new ArrayList<COLOUR>();
	
	@BeforeEach
	void setUp() throws Exception {
		player = new Player();
		ballHolder.clear();
	}

	@Test
	void testGiveBalls() {
		
		for (int i = 0; i < 10; i++) {
			ballHolder.add(getRandomColour());
		}
		
		player.giveBalls(ballHolder);
		
		assertEquals(ballHolder, player.balls);
	}
	
	@Test
	void testCountPoints() {
		ballHolder.add(COLOUR.RED);
		ballHolder.add(COLOUR.RED);
		ballHolder.add(COLOUR.RED);
		ballHolder.add(COLOUR.RED);
		ballHolder.add(COLOUR.RED);
		ballHolder.add(COLOUR.BLUE);
		ballHolder.add(COLOUR.BLUE);
		ballHolder.add(COLOUR.ORANGE);
		ballHolder.add(COLOUR.ORANGE);
		ballHolder.add(COLOUR.ORANGE);
		player.giveBalls(ballHolder);
		
		assertEquals(player.countPoints(), 2);
	}
	
    public static COLOUR getRandomColour() {
    	return COLOUR.values()[(new Random()).nextInt(COLOUR.values().length-1)+1];
    }

}
