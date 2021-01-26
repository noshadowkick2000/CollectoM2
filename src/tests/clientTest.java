package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.CollectoClient;
import client.InvalidResponseException;

class clientTest {
	
	private final static String[] HOST = new String[] {"130.89.253.65", "4114"};
	private final static String DESCRIPTION = "clientTest client instance";
	private final static String NAME = "clientTest_TimYeung";
	
	CollectoClient client;

	@BeforeEach
	void setUp() throws Exception {
		client = new CollectoClient();
	}

	@Test
	void connectionTest() {
		client = new CollectoClient();
		
		// Connect to reference server;
		client.setConnection(HOST);
		
		assertNotNull(client.in);
		assertNotNull(client.out);
		
		client.setClientPlayer(CollectoClient.PLAYER_OPTIONS[0]);
		try {
			client.setHello(DESCRIPTION);
			client.setLogin(NAME);
			
			assertEquals(NAME, client.loginName);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidResponseException e) {
			e.printStackTrace();
		}
	}
}
