package de.tiq.solution.archive;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.hadoop.hbase.TableName;
import org.junit.Assert;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.sun.source.tree.AssertTree;

import de.tiq.solutions.archive.connection.ArchiveConnector;
import de.tiq.solutions.archive.connection.Connection;
import de.tiq.solutions.archive.connection.HBaseArchiveConnectorDecorator;
import de.tiq.solutions.archive.writer.HbaseArchiveWriter;
import de.tiq.solutions.gisaconnect.amqp.QueueType;

public class HbaseArchiveWriterTest {
	private static String TEST_TABLE = "testTable";
	private static String TEST_QUEUE = "tiqsolutions-q-Vertrag1AnlagendatenTest";

	@Test
	public void hbaseWriterTest() throws Exception {
		org.apache.hadoop.hbase.client.Connection hbaseConnection = new Connection.HbaseConnection().getConnection(this.getClass()
				.getResource("/hbase-site.xml").getFile());
		Assert.assertNotNull(hbaseConnection);
		Assert.assertFalse(hbaseConnection.isClosed());
		hbaseConnection.close();
		ArchiveConnector writer = new HbaseArchiveWriter(hbaseConnection, TEST_TABLE, QueueType.DATA);
		Assert.assertNotNull(writer);

	}

	@Test
	public void hbaseDecoratorTest() throws Exception {
		QueueType type = QueueType.valueOf("DATA");

		org.apache.hadoop.hbase.client.Connection hbaseConnection = new Connection.HbaseConnection()
				.getConnection(this.getClass()
						.getResource("/hbase-site.xml").getFile());
		assertFalse(hbaseConnection.getAdmin().tableExists(TableName.valueOf(TEST_TABLE)));

		com.rabbitmq.client.Connection amqpConnection = new Connection.AmqpConnection()
				.getConnection("test.connect.gisa.de,5671,true,gisa,tiqsolutions,sae1yedu3Aid3ie".split(","));
		assertNotNull(amqpConnection);

		ArchiveConnector hbaseAmqpDecorator = new HBaseArchiveConnectorDecorator(new HbaseArchiveWriter(hbaseConnection, TEST_TABLE,
				type),
				amqpConnection);
		hbaseAmqpDecorator.setup(TEST_QUEUE);
		assertTrue(hbaseConnection.getAdmin().tableExists(TableName.valueOf(TEST_TABLE)));
		Channel createChannel = amqpConnection.createChannel();
		assertTrue(createChannel.isOpen());
		hbaseConnection.getAdmin().disableTable(TableName.valueOf(TEST_TABLE));
		hbaseConnection.getAdmin().deleteTable(TableName.valueOf(TEST_TABLE));

	}

	@Test
	public void testLogfileArguments() throws Exception {
		// String[] args = new String[] { "-q", "Queuname?DATA,Quename2?LOG",
		// "-p", "e:/tmp/test.log",
		// "-c", "AMQP-Server details",
		// "-t", "Tabelle" };
		// Main.main(args);
		// System.setIn(new StringBufferInputStream("stop"));

	}
}
