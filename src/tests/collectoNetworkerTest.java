package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import client.CollectoClient;
import util.Communications;

class collectoNetworkerTest {

	private static final String SINGLE_MOVE_EXAMPLE_PROTOCOL = "MOVE~3";
	private static final String DOUBLE_MOVE_EXAMPLE_PROTOCOL = "MOVE~17~6";

	CollectoClient cn = new CollectoClient();

	@Test
	void moveProtocolTest() {
		String protocolMove = cn.moveIntToString(boardTests.SINGLE_MOVE_EXAMPLE);
		assertEquals(SINGLE_MOVE_EXAMPLE_PROTOCOL, protocolMove);

		protocolMove = cn.moveIntToString(boardTests.DOUBLE_MOVE_EXAMPLE);
		assertEquals(DOUBLE_MOVE_EXAMPLE_PROTOCOL, protocolMove);
	}

	@Test
	void moveArrayTest() {
		int[] arrayMove = cn.moveStringToInt(SINGLE_MOVE_EXAMPLE_PROTOCOL.split(Communications.DELIM));
		assertArrayEquals(boardTests.SINGLE_MOVE_EXAMPLE, arrayMove);

		arrayMove = cn.moveStringToInt(DOUBLE_MOVE_EXAMPLE_PROTOCOL.split(Communications.DELIM));
		assertArrayEquals(boardTests.DOUBLE_MOVE_EXAMPLE, arrayMove);
	}

	@Test
	void writeReadTest() throws IOException {
		cn.setConnection(clientTest.HOST);
		cn.writeMessage(Communications.H + Communications.DELIM + clientTest.DESCRIPTION);
		assertTrue(cn.awaitMessage().contains(Communications.H));
	}
}
