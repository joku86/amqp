package de.tiq.solutions.archive.connection;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

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
		System.out.println("und aus die maus");
		amqpChannel.close();
		amqpConnection.close();
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
