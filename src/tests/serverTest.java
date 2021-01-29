package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.InvalidResponseException;
import server.CollectoServer;
import util.CollectoInterface;

class serverTest {

	public static int PORT = 8888;
	public static String DESCRIPTION = "test_description";
	public static String NAME = "REFERENCE_";
	public static String STEP = "and press enter to continue to the next test step";

	CollectoServer server;

	@BeforeAll
	static void disableClutter() {
		CollectoInterface.disableConsole();
	}

	@BeforeEach
	void setUp() throws Exception {
		server = new CollectoServer();
		server.createServerSocket(PORT);
	}

	@AfterEach
	void cleanUp() throws IOException {
		// cannot user server.exit() as it will shut down the test
		CollectoInterface.showDebugMessage("Close all active clients");
		CollectoInterface.requestInput("");
		server.ss.close();
	}

	// run tests with instance of reference-client-v1.jar

	@Test
	void connectClientTests() throws IOException, InvalidResponseException {

		// start first client with name 0

		CollectoInterface.showDebugMessage("start client with name 0");
		server.createNewHandler();
		CollectoInterface.requestInput("");

		assertEquals(1, server.connectedClients.size());
		assertEquals(NAME + "0", server.connectedClients.get(0).getName());
	}

	// The use of enableConsole and disableConsole in the following methods is to be
	// able to more easily perform this test at the finished stage, since without
	// these calls, the console will get cluttered, making it very difficult to know
	// where you are with the steps

	@Test
	void disconnectClientTests() throws IOException, InvalidResponseException {

		// start first client with name 0

		CollectoInterface.showDebugMessage("start first client with name 0 " + STEP);
		server.createNewHandler();
		CollectoInterface.requestInput("");

		assertEquals(1, server.connectedClients.size());
		assertEquals(NAME + "0", server.connectedClients.get(0).getName());

		// start second client with name 1

		CollectoInterface.showDebugMessage("start second client with name 1 " + STEP);
		server.createNewHandler();
		CollectoInterface.requestInput("");

		assertEquals(2, server.connectedClients.size());
		assertEquals(NAME + "1", server.connectedClients.get(1).getName());

		// remove first client

		CollectoInterface.showDebugMessage("remove client with name 0 " + STEP);
		CollectoInterface.requestInput("");

		assertEquals(1, server.connectedClients.size());
		assertEquals(NAME + "1", server.connectedClients.get(0).getName());

		// remove second client

		CollectoInterface.showDebugMessage("remove client with name 1 " + STEP);
		CollectoInterface.requestInput("");

		assertEquals(0, server.connectedClients.size());
	}

	@Test
	void existingClientTests() throws IOException, InvalidResponseException {

		// start first client with name 0

		CollectoInterface.showDebugMessage("start first client with name 0 " + STEP);
		server.createNewHandler();
		CollectoInterface.requestInput("");

		assertEquals(1, server.connectedClients.size());
		assertEquals(NAME + "0", server.connectedClients.get(0).getName());

		// start second client with name 0
		// client should show notification of failed login

		CollectoInterface.showDebugMessage("start second client with name 0 " + STEP);
		server.createNewHandler();
		CollectoInterface.requestInput("");

		assertEquals(1, server.connectedClients.size());
		assertEquals(NAME + "0", server.connectedClients.get(0).getName());

		// retry logging in on client with name 1
		// client should start playing game automatically

		CollectoInterface.showDebugMessage("retry to start second client with name 1 " + STEP);
		CollectoInterface.requestInput("");

		assertEquals(2, server.connectedClients.size());
		assertEquals(NAME + "1", server.connectedClients.get(1).getName());
	}

	@Test
	void getUsersTest() throws IOException {
		// start first client with name 0

		CollectoInterface.showDebugMessage("start first client with name 0 " + STEP);
		server.createNewHandler();
		CollectoInterface.requestInput("");

		// start second client with name 1
		// note reference clients will automatically start playing, ignore this

		CollectoInterface.showDebugMessage("start second client with name 1 " + STEP);
		server.createNewHandler();
		CollectoInterface.requestInput("");

		String usersString = server.getUsers();

		assertTrue(usersString.contains(NAME + "0") && usersString.contains(NAME + "1"));
	}
}
