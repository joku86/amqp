package de.tiq.solutions.archive.connection;

/*
 * License
 * gisa-archive-connector
 * %%
 * Copyright (C) 2016 TIQ-Solutions
 * %%
 * Lizenzbestimmung bearbeite dafür die Datei header.txt
 * EndofLicense
 */


import java.io.IOException;

public interface ArchiveConnector {
	void setup(String... args) throws IOException, InterruptedException;

	boolean transferData(String jsonmessage) throws IOException;

	void shutDown() throws Exception;

}
