package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class CollectoNetworker {
	
	protected BufferedWriter out;
	protected BufferedReader in;

	public void writeMessage(String msg) throws IOException {
		out.write(msg);
		out.newLine();
		out.flush();
	}

	public String awaitMessage() throws IOException {
		return in.readLine();
	}
}
