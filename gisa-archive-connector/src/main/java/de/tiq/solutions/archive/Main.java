package de.tiq.solutions.archive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeoutException;

import de.tiq.solutions.archive.connection.ArchiveConnector;
import de.tiq.solutions.archive.connection.Connection;
import de.tiq.solutions.archive.connection.Connection.HbaseConnection;
import de.tiq.solutions.archive.connection.HBaseArchiveConnectorDecorator;
import de.tiq.solutions.archive.writer.HbaseArchiveWriter;
import de.tiq.solutions.archive.writer.MySqlWriter;

public class Main {

	public static void main(String[] args) {
		org.apache.hadoop.hbase.client.Connection hbaseConnection = new Connection.HbaseConnection()
				.getConnection("E:/logs/hbase-conf/hbase-site.xml");
		com.rabbitmq.client.Connection amqpConnection = null;
		try {
			amqpConnection = new Connection.AmqpConnection()
					.getConnection();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArchiveConnector hbaseAmqpDecorator = new HBaseArchiveConnectorDecorator(new HbaseArchiveWriter(hbaseConnection), null, amqpConnection);
		ArchiveConnector mysql = new HBaseArchiveConnectorDecorator(new MySqlWriter(), null, null);
		hbaseAmqpDecorator.setup("tiqsolutions-q-Vertrag1Anlagendaten");
		try {
			hbaseAmqpDecorator.shutDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
