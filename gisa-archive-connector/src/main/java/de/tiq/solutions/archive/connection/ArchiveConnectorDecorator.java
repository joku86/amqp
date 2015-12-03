package de.tiq.solutions.archive.connection;

import java.util.Collection;

public abstract class ArchiveConnectorDecorator implements ArchiveConnector {
	protected ArchiveConnector decoratedConnection;

	public ArchiveConnectorDecorator(ArchiveConnector decoratedConnection) {
		this.decoratedConnection = decoratedConnection;
	}

}
