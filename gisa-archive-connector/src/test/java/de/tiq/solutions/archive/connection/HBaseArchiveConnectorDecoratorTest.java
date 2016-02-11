package de.tiq.solutions.archive.connection;

import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.AMQP.Channel;

import de.tiq.solutions.archive.writer.HbaseArchiveWriter;

public class HBaseArchiveConnectorDecoratorTest {
	private Connection hbaseTestConnection;
	private com.rabbitmq.client.Connection amqpConnection;
	private HbaseArchiveWriter writer;

	@Test
	public void creationTest() throws Exception {
		hbaseTestConnection = Mockito.mock(org.apache.hadoop.hbase.client.Connection.class);
		writer = Mockito.mock(HbaseArchiveWriter.class);
		com.rabbitmq.client.Channel channel = Mockito.mock(com.rabbitmq.client.Channel.class);
		amqpConnection = Mockito.mock(com.rabbitmq.client.Connection.class);
		when(amqpConnection.createChannel()).thenReturn(channel);
		doNothing().when(channel).basicQos(1);
		doNothing().when(writer).setup(any(String.class));

		ArchiveConnector hbaseAmqpDecorator = new HBaseArchiveConnectorDecorator(writer,
				amqpConnection);
		hbaseAmqpDecorator.setup("QueueNme");
		hbaseAmqpDecorator.transferData("test");
		hbaseAmqpDecorator.shutDown();

	}
}
