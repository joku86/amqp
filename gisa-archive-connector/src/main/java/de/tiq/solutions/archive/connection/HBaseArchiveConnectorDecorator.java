package de.tiq.solutions.archive.connection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.QueueingConsumer;

import de.tiq.solutions.gisaconnect.amqp.ConnectionAmqp;

public class HBaseArchiveConnectorDecorator extends ArchiveConnectorDecorator {
	private Connection connection;
	private Channel createdChannel;
	private com.rabbitmq.client.Connection connection2;

	public HBaseArchiveConnectorDecorator(ArchiveConnector decoratedConnection, Connection connection) {
		super(decoratedConnection);
		this.connection = connection;

	}

	private void connect(final ArchiveConnector writer) {
		System.out.println("verbinde mit der Queue");
		try {
			ConnectionAmqp.DefaultConnectionFactory defaultConnectionFactory = new ConnectionAmqp.DefaultConnectionFactory();
			connection2 = defaultConnectionFactory.getConnection(null);
			createdChannel = connection2.createChannel();
			Consumer c = new DefaultConsumer(createdChannel) {
				public void handleDelivery(String consumerTag, com.rabbitmq.client.Envelope envelope,
						com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {
					System.out.println("nachricht ist angekommen");
					writer.transferData(Arrays.asList("empfangene nachricht"));

				}

			};
			defaultConnectionFactory.appendConsumerAndGetChannelWitchManualAck(connection2, c, "queue");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.apache.hadoop.hbase.client.Connection connection2 = (org.apache.hadoop.hbase.client.Connection) connection
				.getConnection("E:/logs/hbase-conf/hbase-site.xml");

		System.out.println("verbindung wurde aufgebaut");
		// TODO Auto-generated method stub

	}

	public void transferData(Collection<String> test) {

	}

	public void shutDown() throws Exception {
		System.out.println("und aus die maus");
		createdChannel.close();
		connection2.close();
		connection.close();

	}

	public void setup() {
		connect(decoratedConnection);
		System.out.println("decorierung abgeschlossen ");

	}

}
