package de.tiq.solutions.archive.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ShutdownSignalException;

public class QueueConsumer implements Consumer {
	private ArchiveConnector writer;
	private List<String> toTransfer = new ArrayList<>();

	public QueueConsumer(ArchiveConnector connector) {
		writer = connector;
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
		System.out.println("nachricht ist angekommen");
		writer.transferData(Arrays.asList("empfangene nachricht"));

		System.out.println("bestaetige nachricht");

	}

	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

}
