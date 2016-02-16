package de.tiq.solutions.amqp;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;

import de.tiq.solutions.amqp.QueueConsumer;
import de.tiq.solutions.gisaconnect.amqp.ConnectionAmqp;
import de.tiq.solutions.gisaconnect.amqp.FallbackOnError;
import de.tiq.solutions.gisaconnect.amqp.QueueType;
import de.tiq.solutions.gisaconnect.websocket.Sender.WebsocketSender;
import de.tiq.solutions.gisaconnect.websocket.WebSocketFactory;
import de.tiq.solutions.gisaconnect.websocket.WebSocketFactory.DefaultWebSocketFactory;

public class QueueConsumerTest {
	Properties prop = new Properties();
	public static boolean runAMQP = true;
	private Channel channelMock;
	private DefaultWebSocketFactory wsFactoryMock;
	private FallbackOnError fallbackMock;

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
	public void constuctorsTest() throws Exception {
		channelMock = Mockito.mock(Channel.class);
		wsFactoryMock = Mockito.mock(DefaultWebSocketFactory.class);
		fallbackMock = Mockito.mock(FallbackOnError.class);
		Consumer consumentWithChannelData = new QueueConsumer(channelMock, wsFactoryMock, fallbackMock, QueueType.DATA);
		Consumer consumentWithChannelLog = new QueueConsumer(channelMock, wsFactoryMock, fallbackMock, QueueType.LOG);
		Assert.assertNotNull(consumentWithChannelData);
		Assert.assertNotNull(consumentWithChannelLog);

	}

	@Test
	public void getWSTest() throws Exception {
		channelMock = Mockito.mock(Channel.class);
		wsFactoryMock = Mockito.mock(DefaultWebSocketFactory.class);
		fallbackMock = Mockito.mock(FallbackOnError.class);
		QueueConsumer consumentWithChannelData = new QueueConsumer(channelMock, wsFactoryMock, fallbackMock, QueueType.DATA);
		QueueConsumer consumentWithChannelLog = new QueueConsumer(channelMock, wsFactoryMock, fallbackMock, QueueType.LOG);
		Assert.assertNotNull(consumentWithChannelData.getWebsocketSender());
		Assert.assertNotNull(consumentWithChannelLog.getWebsocketSender());
	}

	@Test
	public void handleDeliveryDataTest() throws Exception {
		String data = "{\"ts\":\"2015-10-06T05:42:30.119+0000\",\"val\":[{\"key\":\"WRTP468C:2110595689:A.Ms.Amp\",\"value\":2.419}]}";
		String data2 = "{\"ts\":\"2015-10-06T05:42:30.119+0000\",\"val\":[{\"key\":\"WRTP468C:37564:A.Ms.Amp\",\"value\":2.419}]}";
		String data3 = "{\"ts\":\"2015-10-06T05:42:30.119+0000\",\"val\":[{\"key\":\"WRTP468C:234:A.Ms.Amp\",\"value\":2.419}]}";
		channelMock = Mockito.mock(Channel.class);
		Envelope envelopeMock = Mockito.mock(Envelope.class);
		wsFactoryMock = Mockito.mock(DefaultWebSocketFactory.class);
		WebsocketSender wsSenderMock = Mockito.mock(WebsocketSender.class);
		fallbackMock = Mockito.mock(FallbackOnError.class);

		QueueConsumer consumentWithChannelData = new QueueConsumer(channelMock, wsFactoryMock, fallbackMock, QueueType.DATA);
		consumentWithChannelData.setWebsocketSender(wsSenderMock);
		doNothing().when(wsSenderMock).send(any(String.class));
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, data.getBytes());
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, data2.getBytes());
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, data3.getBytes());
		doThrow(IOException.class).when(wsSenderMock).send(any(String.class));
		consumentWithChannelData.TIMETODIE = 1;
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, data.getBytes());
		data = "{\"ts\":\"2015-10-06T05:42:30.119+0000\",\"val\":[{\"key\":\"RTP468CA.Ms.Amp\",\"value\":2.419}]}";
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, data.getBytes());

	}

	@Test
	public void handleDeliveryLogTest() throws Exception {
		String event =
				"{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WebBox\",\"module\":\"RasClient\",\"messageCode\":\"13000\",\"messageArgs\":\"WRTP4Q39|2110144057\",\"message\":\"RAS_Start_Dialing_1\"}";
		String event2 =
				"{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WRTP4Q39:2110144057\",\"module\":\"WRTP4Q39:2110144057\",\"messageCode\":\"13000\",\"messageArgs\":\"WeRTP4Q39|2144057\",\"message\":\"RAS_Start_Dialing_1\"}";
		String event3 =
				"{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WebBox\",\"module\":\"RasClient\",\"messageCode\":\"13000\",\"messageArgs\":\"WRTP4Q39|20144057\",\"message\":\"RAS_Start_Dialing_1\"}";
		String event4 =
				"{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WRTP4Q39:2110144\",\"module\":\"WRTP4Q39:2144057\",\"messageCode\":\"13000\",\"messageArgs\":\"WTP4Q39|20144057\",\"message\":\"RAS_Start_Dialing_1\"}";

		channelMock = Mockito.mock(Channel.class);
		Envelope envelopeMock = Mockito.mock(Envelope.class);
		wsFactoryMock = Mockito.mock(DefaultWebSocketFactory.class);
		WebsocketSender wsSenderMock = Mockito.mock(WebsocketSender.class);
		fallbackMock = Mockito.mock(FallbackOnError.class);

		QueueConsumer consumentWithChannelData = new QueueConsumer(channelMock, wsFactoryMock, fallbackMock, QueueType.LOG);
		consumentWithChannelData.setWebsocketSender(wsSenderMock);
		doNothing().when(wsSenderMock).send(any(String.class));
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, event.getBytes());
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, event2.getBytes());
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, event3.getBytes());
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, event4.getBytes());
		doThrow(IOException.class).when(wsSenderMock).send(any(String.class));
		consumentWithChannelData.TIMETODIE = 1;
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, event.getBytes());
		event =
				"{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WebBox\",\"module\":\"RasClient\",\"messageCode\":\"13000\",\"messageArgs\":\"RTP4Q392110144057\",\"message\":\"RAS_Start_Dialing_1\"}";
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, event.getBytes());
	}

	@Test
	public void channelBranchTest() throws Exception {

		String data = "{\"ts\":\"2015-10-06T05:42:30.119+0000\",\"val\":[{\"key\":\"WRTP468C:2110595689:A.Ms.Amp\",\"value\":2.419}]}";

		Envelope envelopeMock = Mockito.mock(Envelope.class);
		wsFactoryMock = Mockito.mock(DefaultWebSocketFactory.class);
		WebsocketSender wsSenderMock = Mockito.mock(WebsocketSender.class);
		fallbackMock = Mockito.mock(FallbackOnError.class);
		doNothing().when(wsSenderMock).send(any(String.class));
		QueueConsumer consumentWithoutChannelData = new QueueConsumer(null,
				wsFactoryMock, fallbackMock, QueueType.DATA);
		consumentWithoutChannelData.setWebsocketSender(wsSenderMock);
		consumentWithoutChannelData.handleDelivery("ct", envelopeMock, null,
				data.getBytes());
		Channel mock = Mockito.mock(Channel.class);
		QueueConsumer consumentWithChannelData = new QueueConsumer(mock, wsFactoryMock, fallbackMock, QueueType.DATA);
		consumentWithChannelData.setWebsocketSender(wsSenderMock);
		when(mock.isOpen()).thenReturn(false);
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null, data.getBytes());
		when(mock.isOpen()).thenReturn(true);
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null,
				data.getBytes());
		byte[] bytes = data.getBytes("UTF-16");
		consumentWithChannelData.handleDelivery("ct", envelopeMock, null,
				bytes);
	}

	@Ignore
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
					public void notifyShutdown(Channel channel, String mesage) {
						QueueConsumerTest.runAMQP = false;
					}
				}, QueueType.DATA);
		channel.basicConsume("tiqsolutions-q-Vertrag1AnlagendatenTest", false, consumentWithChannel);
		Assert.assertNotNull(channel);
		Assert.assertTrue(channel.isOpen());
		QueueConsumer.TIMETODIE = 2;
		while (runAMQP) {
			Thread.sleep(100);
		}
		if (channel.isOpen())
			channel.close();
		connection.close();
	}

	@Ignore
	@Test
	public void queueConsumer2Test() throws Exception {
		ConnectionAmqp wrapper = new ConnectionAmqp.DefaultConnectionFactory();
		Connection connection = wrapper.getConnection(prop);
		Channel channel =
				wrapper.appendConsumerAndGetChannelWitchManualAck(connection);

		Consumer consumentWithChannel = new QueueConsumer(channel, new
				WebSocketFactory.DefaultWebSocketFactory(new URI(
						"nichts")),
				new FallbackOnError() {

					@Override
					public void notifyShutdown(Channel channel, String mesage) {
						QueueConsumerTest.runAMQP = false;
					}
				}, QueueType.LOG);
		channel.basicConsume("tiqsolutions-q-Vertrag1LogmeldungenTest", false, consumentWithChannel);
		Assert.assertNotNull(channel);
		Assert.assertTrue(channel.isOpen());
		QueueConsumer.TIMETODIE = 1;
		while (runAMQP) {
			Thread.sleep(100);
		}
		if (channel.isOpen())
			channel.close();
		connection.close();
	}
}
