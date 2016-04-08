package de.tiq.solutions;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;
import org.glassfish.tyrus.client.ClientManager;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import gisa.TiqData;

/**
 * 
 * @author johann.kunz Before execute this class configure frame conditions like
 *         LM-Server and DataCreator from Evermind. The Main-Class of Archive
 *         Connector can started but serve an Exception (Caused by:
 *         java.lang.IllegalAccessError: tried to access method
 *         com.google.common.base.Stopwatch.<init>()V from class
 *         org.apache.hadoop.hbase.zookeeper.MetaTableLocator)
 */
public class AmqpConnectorStressTest {
	private static Broker b;
	private Connection connection;
	private Channel channel;
	private static final Logger logger = Logger
			.getLogger("it");
	private TiqData data;

	@Before
	public void setUp() throws Exception {
		logger.info("Set-Up Broker");

		if (b == null) {
			b = new Broker();
			Path path = Paths.get(this.getClass().getResource("/config.json").toURI());
			String qpidHomeDir = path.getParent().toString();
			BrokerOptions bo = new BrokerOptions();
			bo.setConfigProperty("qpid.work_dir", qpidHomeDir);
			bo.setConfigProperty("qpid.home_dir", qpidHomeDir);

			bo.setConfigProperty("qpid.amqp_port", "5671");
			bo.setInitialConfigurationLocation(path.toString());
			b.startup(bo);
			logger.info("Empty Broker is running");
			channel = createConnection();
			fillData(channel, "amq.direct");
			logger.info("Broker full and running");

		}
	}

	@After
	public void tearDown() throws Exception {
		close();
		b.shutdown();
	}

	@Test
	public void testLivemonitoring() throws Exception {

		Thread t = new Thread(new Runnable() {

			public void run() {
				logger.info("Start Connector");
				// Das Funktioniert nicht:

				// de.tiq.solutions.archive.Main.main(
				// new String[] { "-t", "delete_me", "-q",
				// "dataQueue?DATA,logQueue?LOG", "-r",
				// "e:/var/hbase-site.xml", "-c",
				// "localhost,5671,true,secureVH,admin,admin" });

				de.tiq.solutions.livemonitoring.Main.main(
						new String[] { "-q", "dataQueue?DATA,logQueue?LOG", "-r",
								"ws://localhost:8025/tiq/hbase", "-c",
								"localhost,5671,true,secureVH,admin,admin" });

			}

		});
		int data = channel.queueDeclarePassive("dataQueue").getMessageCount();
		int log = channel.queueDeclarePassive("logQueue").getMessageCount();
		int i, dataneu;
		int j, logneu;
		if (serverAvailible())
			t.start();
		else
			fail("LM-Server läuft nicht oder ist nicht erreichbar ");

		while (!isEmptyQueue()) {
			Thread.sleep(1000);
			dataneu = channel.queueDeclarePassive("dataQueue").getMessageCount();
			i = data - dataneu;
			data = dataneu;
			logneu = channel.queueDeclarePassive("logQueue").getMessageCount();
			j = log - logneu;
			log = logneu;

			logger.info(
					i + " Nachrichten von der DatenQueue pro Sekunde abgenommen, verbleiben: " + dataneu);

			logger.info(j + " Nachrichten von der LogQueue pro Sekunde abgenommen, verbleiben : " + logneu);
			// endlosschleife
			// generate(channel, "amq.direct", "tiqsolar.An1Dat");

		}

	}

	private final boolean serverAvailible() {

		final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

		ClientManager client = ClientManager.createClient();
		try {
			client.connectToServer(new Endpoint() {
				@Override
				public void onOpen(Session session, EndpointConfig config) {
				}
			}, cec, new URI("ws://localhost:8025/tiq/hbase"));
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private boolean isEmptyQueue() throws IOException {

		return channel.queueDeclarePassive("dataQueue").getMessageCount() == 0 && channel.queueDeclarePassive("logQueue").getMessageCount() == 0;

	}

	public Channel createConnection() throws Exception {
		String[] args = new String[] { "localhost", "secureVH", "admin", "admin" };

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(args[0]);
		factory.setPort(5671);
		factory.useSslProtocol();
		factory.setVirtualHost(args[1]);
		factory.setUsername(args[2]);
		factory.setPassword(args[3]);

		connection = null;
		channel = null;
		connection = factory.newConnection();
		return connection.createChannel();
	}

	public void close() {
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

	private void fillData(Channel channel, String exchangeName) throws Exception

	{
		String routingKeyData = "tiqsolar.An1Dat";
		String routingKeyLogs = "tiqsolar.An1Log";
		data = new TiqData();
		generate(channel, exchangeName, routingKeyData, routingKeyLogs);

	}

	private void generate(Channel channel, String exchangeName, String routingKeyData, String routingKeyLogs) throws Exception, IOException {
		LocalDateTime nextLog = new LocalDateTime();
		nextLog = nextLog.minusHours(1);
		LocalDateTime nextSignal = new LocalDateTime();
		int sended = 0;
		for (int i = 0; i < 10000; i++) {
			String payload = TiqData.toJson(data.getDataFor(nextSignal));
			if (payload != null) {
				channel.basicPublish(exchangeName,
						routingKeyData, null, payload.getBytes());
				sended++;
			}
			nextSignal = nextSignal.plusSeconds(10);

			for (TiqData.LogEntry log : data.getLogsFor(nextLog)) {
				payload = TiqData.toJson(log);
				if (payload != null)
					channel.basicPublish(exchangeName,
							routingKeyLogs, null, payload.getBytes());
			}
			nextLog = nextLog.plusSeconds(1);
		}
		logger.info("Broker filled with: " + sended);

	}

}
