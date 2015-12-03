package de.tiq.solutions.archive.writer;

import java.util.Collection;
import java.util.Iterator;

import de.tiq.solutions.archive.connection.ArchiveConnector;

public class HbaseArchiveWriter implements ArchiveConnector {

	public void transferData(Collection<String> test) {
		System.out.println("daten werden geschrieben");

	}

	public void shutDown() {

	}

	public void setup() {
		// TODO Auto-generated method stub

	}
}
