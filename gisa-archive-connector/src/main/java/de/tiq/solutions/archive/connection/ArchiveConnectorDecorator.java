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

public abstract class ArchiveConnectorDecorator implements ArchiveConnector {
	protected ArchiveConnector decoratedConnection;

	public ArchiveConnectorDecorator(ArchiveConnector decoratedConnection) {
		this.decoratedConnection = decoratedConnection;
	}

	public boolean transferData(String jsonmessage) throws IOException {
		return decoratedConnection.transferData(jsonmessage);

	}
}
