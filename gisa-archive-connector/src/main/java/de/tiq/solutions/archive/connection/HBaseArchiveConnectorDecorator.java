package de.tiq.solutions.archive.connection;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import de.tiq.solutions.archive.writer.HbaseArchiveWriter;
import de.tiq.solutions.gisaconnect.amqp.FallbackOnError;

public class HBaseArchiveConnectorDecorator extends ArchiveConnectorDecorator {
	private static final Logger logger = Logger
			.getRootLogger();
	private Channel amqpChannel;
	private com.rabbitmq.client.Connection amqpConnection;
	private FallbackOnError fallback;

	public HBaseArchiveConnectorDecorator(ArchiveConnector decoratedConnection,
			com.rabbitmq.client.Connection amqpConnection, FallbackOnError fallback) {
		super(decoratedConnection);
		this.amqpConnection = amqpConnection;
		this.fallback = fallback;

	}

	private void connect(String queueName) throws RuntimeException {
		logger.info("build connection to AMQP");
		try {
			amqpChannel = amqpConnection.createChannel();
			logger.info(String.format("New channel open. Channel contains %d %s messages", amqpChannel.queueDeclarePassive(queueName)
					.getMessageCount(), ((HbaseArchiveWriter) decoratedConnection).getType().toString()));
			amqpChannel.basicQos(1);
			Consumer a = new QueueingConsumer(amqpChannel) {

				private Channel channel;

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
					try {
						String message = new String(body, "UTF-8");
						if (transferData(message)) {
							confirm(envelope);
							logger.info(".");
						} else {
							// TODO implemnt waiting mode
						}

					} catch (IOException e) {
						logger.error("Unable to write message to the Database " + e);
						try {
							shutDown();
						} catch (Exception e1) {
							logger.error("Clean shutdown fail " + e1);
						}
						fallback.notifyShutdown(null);
					}
				}

				private void confirm(Envelope envelope) throws IOException {
					channel = getChannel();
					if (channel != null && channel.isOpen())
						channel.basicAck(envelope.getDeliveryTag(), false);
				}

				@Override
				public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
					super.handleShutdownSignal(consumerTag, sig);
					try {
						logger.error("AMQP shutdown received. " + sig);
						decoratedConnection.shutDown();
						logger.info("AMQP shutdown received and interrupted the connections succesfully");
					} catch (Exception e) {
						logger.error("AMQP shutdown received but could not stop existing connections clean. Stop ");
					}
					fallback.notifyShutdown(null);
				}

			};
			amqpChannel.basicConsume(queueName, false, a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	private static Semaphore _sem = new Semaphore(1);

	public void shutDown() throws Exception {
		_sem.acquire();
		logger.info("begin to shutdown the connections");

		if (amqpChannel.isOpen())
			amqpChannel.close();
		if (amqpConnection.isOpen())
			amqpConnection.close();
		decoratedConnection.shutDown();
		logger.info("shutdown-method successful");
		_sem.release();

	}

	public void setup(String... args) throws InterruptedException, IOException {
		_sem.acquire();
		logger.info("Decorator setup the connections");
		decoratedConnection.setup(args);
		connect(args[0]);
		logger.info("Decorator finish the setup ");
		_sem.release();
	}

	public Channel getAmqpChannel() {
		return amqpChannel;
	}

}
