package de.tiq.solutions;

import java.io.IOException;
import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AmqpInOutTest {
	@Test
	public void testName() throws Exception {
		{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			factory.setPort(5671);
			factory.useSslProtocol();
			factory.setVirtualHost("secureVH");
			factory.setUsername("admin");
			factory.setPassword("admin");

			String exchangeName = "amq.direct";
			String routingKey = "forTestQueue";

			Connection connection = null;
			Channel channel = null;

			try {
				connection = factory.newConnection();
				channel = connection.createChannel();

				String payload = "Nachricht" + new Date();
				channel.basicPublish(exchangeName, routingKey, null, payload.getBytes());

			} finally {
				if (channel != null)
					try {
						channel.close();
					} catch (Exception ex) {
						// ignored
					}
				if (connection != null)
					try {
						connection.close();
					} catch (Exception ex) {
						// ignored
					}
			}

		}
	}

	@Test
	public void testName2() throws Exception {
		{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			factory.setPort(5671);
			factory.useSslProtocol();
			factory.setVirtualHost("secureVH");
			factory.setUsername("admin");
			factory.setPassword("admin");

			String queue = "testQueue";

			Connection connection = null;
			Channel channel = null;

			try {
				connection = factory.newConnection();
				channel = connection.createChannel();
				channel.basicQos(1);
				channel.basicConsume(queue, false, new QueueingConsumer(channel) {

					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
						// TODO Auto-generated method stub
						super.handleDelivery(consumerTag, envelope, properties, body);
						System.out.println(new String(body));
					}

				});
				Thread.sleep(100);
			} finally {
				if (channel != null)
					try {
						channel.close();
					} catch (Exception ex) {
						// ignored
					}
				if (connection != null)
					try {
						connection.close();
					} catch (Exception ex) {
						// ignored
					}
			}

		}
	}

}
