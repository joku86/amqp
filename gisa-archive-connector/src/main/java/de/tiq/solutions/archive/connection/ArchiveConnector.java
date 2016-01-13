package de.tiq.solutions.archive.connection;

import java.io.IOException;

public interface ArchiveConnector {
	void setup(String... args) throws IOException, InterruptedException;

	boolean transferData(String jsonmessage) throws IOException;

	void shutDown() throws Exception;

}
