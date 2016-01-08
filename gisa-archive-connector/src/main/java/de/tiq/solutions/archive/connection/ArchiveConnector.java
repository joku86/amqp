package de.tiq.solutions.archive.connection;

import java.io.IOException;

public interface ArchiveConnector {
	void setup(String... args) throws IOException;

	boolean transferData(String jsonmessage);

	void shutDown() throws Exception;

}
