package de.tiq.solutions.archive.connection;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class HBaseArchiveConnectorDecorator extends ArchiveConnectorDecorator {

	private Channel amqpChannel;
	private com.rabbitmq.client.Connection amqpConnection;

	public HBaseArchiveConnectorDecorator(ArchiveConnector decoratedConnection,
			com.rabbitmq.client.Connection amqpConnection) {
		super(decoratedConnection);
		this.amqpConnection = amqpConnection;

	}

	private void connect(final ArchiveConnector writer, String queueName) {
		System.out.println("verbinde mit der Queue");
		try {
			amqpChannel = amqpConnection.createChannel();

			System.out.println("New channel open. Channel contains "
					+ amqpChannel.queueDeclarePassive(queueName).getMessageCount()
					+ " mesasges");
			amqpChannel.basicQos(1);
			Consumer a = new QueueingConsumer(amqpChannel) {

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					if (writer.transferData(message))
						confirm(envelope);
				}

				private void confirm(Envelope envelope) throws IOException {
					Channel channel = getChannel();
					if (channel != null && channel.isOpen())
						channel.basicAck(envelope.getDeliveryTag(), false);

				}

				@Override
				public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
					// TODO Auto-generated method stub
					super.handleShutdownSignal(consumerTag, sig);
				}

			};
			amqpChannel.basicConsume(queueName, false, a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("verbindung wurde aufgebaut");
		// TODO Auto-generated method stub

	}

	public void shutDown() throws Exception {
		System.out.println("Dekorator beendet die Queue");
		amqpChannel.close();
		amqpConnection.close();
		System.out.println("Queue wurde beendet");
		System.out.println("hbase tabelle wird beendet");
		decoratedConnection.shutDown();
		System.out.println("hbase tabelle geschlossen");
		// connection.close();

	}

	public void setup(String... args) {
		try {
			decoratedConnection.setup(args);
			connect(decoratedConnection, args[0]);
			System.out.println("decorierung abgeschlossen ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
