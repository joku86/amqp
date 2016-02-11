package de.tiq.solutions.archive.connection;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.*;

import com.rabbitmq.client.Channel;

import de.tiq.solution.archive.HbaseArchiveWriterTest;
import de.tiq.solutions.archive.writer.HbaseArchiveWriter;
import de.tiq.solutions.gisaconnect.amqp.QueueType;

public class QueueConsumerTest {
	@Test
	public void queueConsumerTest() throws Exception {

		org.apache.hadoop.hbase.client.Connection hbaseTestConnection = Mockito.mock(org.apache.hadoop.hbase.client.Connection.class);
		com.rabbitmq.client.Envelope envelope = Mockito.mock(com.rabbitmq.client.Envelope.class);

		com.rabbitmq.client.Connection amqpConnection = Mockito.mock(com.rabbitmq.client.Connection.class);
		Channel amqpChannel = Mockito.mock(Channel.class);
		doNothing().when(amqpChannel).basicAck(any(Long.class), any(Boolean.class));
		when(amqpChannel.isOpen()).thenReturn(true);
		when(envelope.getDeliveryTag()).thenReturn(1L);
		QueueConsumer consumer = new QueueConsumer(new HbaseArchiveWriter(hbaseTestConnection, HbaseArchiveWriterTest.TEST_TABLE, QueueType.DATA) {

			public boolean transferData(String message) {
				if (message.equals("testNachricht"))
					return true;
				else
					return false;
			}

		}, amqpChannel);
		consumer.handleDelivery("consumerTag", envelope, null, "testNachricht".getBytes());
		consumer.handleDelivery("consumerTag", envelope, null, "notTransfered".getBytes());
		when(amqpChannel.isOpen()).thenReturn(false);
		consumer.handleDelivery("consumerTag", envelope, null, "testNachricht".getBytes());
		consumer.handleDelivery("consumerTag", envelope, null, "testNachricht".getBytes());

		consumer.handleCancel("test");
		consumer.handleCancelOk("test");
		consumer.handleConsumeOk("test");
		consumer.handleRecoverOk("test");

	}
}
