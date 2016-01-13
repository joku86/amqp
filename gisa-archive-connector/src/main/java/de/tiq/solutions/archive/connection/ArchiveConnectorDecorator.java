package de.tiq.solutions.archive.connection;

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
