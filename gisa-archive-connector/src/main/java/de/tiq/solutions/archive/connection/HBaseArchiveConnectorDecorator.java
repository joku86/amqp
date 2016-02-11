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
		try {
			amqpChannel = amqpConnection.createChannel();
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
		amqpChannel.close();
		amqpConnection.close();
		decoratedConnection.shutDown();
		// connection.close();

	}

	public void setup(String... args) {
		try {
			decoratedConnection.setup(args);
			connect(decoratedConnection, args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
