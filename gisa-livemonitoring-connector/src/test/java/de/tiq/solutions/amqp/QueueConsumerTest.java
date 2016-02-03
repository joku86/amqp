package de.tiq.solutions.amqp;

import java.net.URI;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

import de.tiq.solution.amqp.QueueConsumer;
import de.tiq.solutions.gisaconnect.amqp.ConnectionAmqp;
import de.tiq.solutions.gisaconnect.amqp.ConnectionAmqpTest;
import de.tiq.solutions.gisaconnect.amqp.FallbackOnError;
import de.tiq.solutions.gisaconnect.amqp.QueueType;
import de.tiq.solutions.gisaconnect.websocket.WebSocketFactory;

public class QueueConsumerTest {
	Properties prop = new Properties();
	public static boolean runAMQP = true;

	@Before
	public void setUp() {
		prop.put("HOST", "test.connect.gisa.de");
		prop.put("PORT", "5671");
		prop.put("USESSL", "true");
		prop.put("VHOST", "gisa");
		prop.put("USER", "tiqsolutions");
		prop.put("PASS", "sae1yedu3Aid3ie");
	}

	@Test
	public void queueConsumerTest() throws Exception {
		ConnectionAmqp wrapper = new ConnectionAmqp.DefaultConnectionFactory();
		Connection connection = wrapper.getConnection(prop);
		Channel channel =
				wrapper.appendConsumerAndGetChannelWitchManualAck(connection);

		Consumer consumentWithChannel = new QueueConsumer(channel, new
				WebSocketFactory.DefaultWebSocketFactory(new URI(
						"nichts")),
				new FallbackOnError() {

					@Override
					public void notifyShutdown(Channel channel) {
						QueueConsumerTest.runAMQP = false;
					}
				}, QueueType.DATA);
		channel.basicConsume("tiqsolutions-q-Vertrag1AnlagendatenTest", false, consumentWithChannel);
		Assert.assertNotNull(channel);
		System.out.println(channel.queueDeclarePassive("tiqsolutions-q-Vertrag1AnlagendatenTest").getMessageCount());
		Assert.assertTrue(channel.isOpen());
		QueueConsumer.TIMETODIE = 2;
		while (runAMQP) {
			Thread.sleep(100);
		}
		if (channel.isOpen())
			channel.close();
		connection.close();
	}
}
