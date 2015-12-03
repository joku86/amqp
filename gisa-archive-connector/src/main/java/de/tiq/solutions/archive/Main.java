package de.tiq.solutions.archive;

import de.tiq.solutions.archive.connection.ArchiveConnector;
import de.tiq.solutions.archive.connection.Connection;
import de.tiq.solutions.archive.connection.Connection.HbaseConnection;
import de.tiq.solutions.archive.connection.HBaseArchiveConnectorDecorator;
import de.tiq.solutions.archive.writer.HbaseArchiveWriter;
import de.tiq.solutions.archive.writer.MySqlWriter;

public class Main {

	public static void main(String[] args) {
		HbaseConnection hbaseConnection = new Connection.HbaseConnection();

		ArchiveConnector hbase = new HBaseArchiveConnectorDecorator(new HbaseArchiveWriter(), hbaseConnection);
		ArchiveConnector mysql = new HBaseArchiveConnectorDecorator(new MySqlWriter(), null);
		hbase.setup();
		try {
			hbase.shutDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
