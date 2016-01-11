package de.tiq.solutions.archive.connection;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

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
			Consumer a = new QueueConsumer(writer, amqpChannel);
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
