package de.tiq.solutions.archive.connection;

public abstract class ArchiveConnectorDecorator implements ArchiveConnector {
	protected ArchiveConnector decoratedConnection;

	public ArchiveConnectorDecorator(ArchiveConnector decoratedConnection) {
		this.decoratedConnection = decoratedConnection;
	}

	public boolean transferData(String jsonmessage) {
		return decoratedConnection.transferData(jsonmessage);

	}
}
