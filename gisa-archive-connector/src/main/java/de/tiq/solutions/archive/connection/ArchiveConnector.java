package de.tiq.solutions.archive.connection;

import java.util.Collection;

public interface ArchiveConnector {
	void setup();

	void transferData(Collection<String> test);

	void shutDown() throws Exception;

}
