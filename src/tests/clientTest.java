package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.CollectoClient;
import client.InvalidResponseException;

class clientTest {

	public final static String[] HOST = new String[] { "130.89.253.65", "4114" };
	public final static String[] WRONG_HOST = new String[] { "0000.69.420.112", "3435" };
	public final static String DESCRIPTION = "clientTest client instance";
	public final static String NAME = "clientTest_TimYeung";

	CollectoClient client;

	@BeforeEach
	void setUp() throws IOException {
		client = new CollectoClient();
	}

	@AfterEach
	void disconnect() throws IOException {
		if (client.in != null) {
			simulateConnectionLoss(client);
		}
	}

	@Test
	void fullConnectionTest() throws IOException, InvalidResponseException {
		// Connect to reference server;
		client.setConnection(HOST);

		assertNotNull(client.in);
		assertNotNull(client.out);

		client.setClientPlayer(CollectoClient.PLAYER_OPTIONS[0]);
		client.hello(DESCRIPTION);
		client.login(NAME);

		assertEquals(NAME, client.loginName);
	}

	@Test
	void invalidConnectionTest() {
		client.setConnection(WRONG_HOST);
	}

	void simulateConnectionLoss(CollectoClient client) throws IOException {
		client.in.close();
		client.out.close();
	}

	void setupCorrectConnection(CollectoClient client) {
		client.setConnection(HOST);
	}

	@Test
	void helloErrorTest() throws IOException, InvalidResponseException {
		setupCorrectConnection(client);
		simulateConnectionLoss(client);
		assertThrows(IOException.class, () -> client.hello(DESCRIPTION));
	}

	@Test
	void loginErrorTest() throws IOException, InvalidResponseException {
		setupCorrectConnection(client);
		client.hello(DESCRIPTION);
		simulateConnectionLoss(client);
		assertThrows(IOException.class, () -> client.login(NAME));
	}

	@Test
	void alreadyLoggedInTest() throws IOException, InvalidResponseException {
		setupCorrectConnection(client);
		client.hello(DESCRIPTION);
		client.login(NAME);

		CollectoClient duplicate = new CollectoClient();
		setupCorrectConnection(duplicate);
		duplicate.hello(DESCRIPTION);
		assertFalse(duplicate.login(NAME));
		simulateConnectionLoss(duplicate);
	}

	@Test
	void queueErrorTest() throws IOException, InvalidResponseException {
		setupCorrectConnection(client);
		client.hello(DESCRIPTION);
		client.login(NAME);
		simulateConnectionLoss(client);
		assertThrows(IOException.class, () -> client.queue());
	}
}
