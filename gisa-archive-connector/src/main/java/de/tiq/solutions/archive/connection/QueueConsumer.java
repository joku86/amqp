package de.tiq.solutions.archive.connection;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class QueueConsumer implements Consumer {

	private ArchiveConnector writer;
	private Channel channel;

	public QueueConsumer(ArchiveConnector connector, Channel amqpChannel) {
		writer = connector;
		channel = amqpChannel;
	}

	@Override
	public void handleConsumeOk(String consumerTag) {

	}

	@Override
	public void handleCancelOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDelivery(String consumerTag, com.rabbitmq.client.Envelope envelope,
			com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {
		String message = new String(body, "UTF-8");
		if (writer.transferData(message))
			confirm(envelope);
	}

	private void confirm(Envelope envelope) throws IOException {
		if (channel != null && channel.isOpen())
			channel.basicAck(envelope.getDeliveryTag(), false);

	}

	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

}
