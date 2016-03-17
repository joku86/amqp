package de.tiq.solutions.livemonitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

import de.tiq.solutions.amqp.QueueConsumer;
import de.tiq.solutions.gisaconnect.amqp.ConnectionAmqp;
import de.tiq.solutions.gisaconnect.amqp.FallbackOnError;
import de.tiq.solutions.gisaconnect.amqp.QueueType;
import de.tiq.solutions.gisaconnect.websocket.WebSocketFactory;

public class Main {
	private static final Logger logger = Logger
			.getLogger("GISA-Websocket-Bridge");
	public static boolean connectorRun = true;

	@SuppressWarnings("unused")
	private static enum OPTIONS {

		QUEUE("q", "Queue"), RECEIVER("r", "Receiver"), LOGFILE("p",
				"LogfilePath"), CONNECTION("c", "ConnectionDetails");

		private final String longDesc, desc;

		private OPTIONS(String desc, String longDesc) {
			this.desc = desc;
			this.longDesc = longDesc;
		}

		String getLongDesc() {
			return longDesc;
		}

		String getDesc() {
			return desc;
		}

	}

	private static void createOptions(Options options) {

		options.addOption(
				OPTIONS.QUEUE.getDesc(),
				true,
				"Queuename followed by ? and Type of the Queue(LOG or DATA). Queuename?Type e.g: exampleQueueName?LOG,ExampleQueue2?DATA ");
		options.addOption(OPTIONS.RECEIVER.getDesc(), true,
				"Websocket Adress of Receiver e.g ws://myhost.teest:8025/root/path");
		options.addOption(OPTIONS.LOGFILE.getDesc(), true,
				"Path where the logfile should be saved");
		options.addOption(
				OPTIONS.CONNECTION.getDesc(),
				true,
				"Connection details separated by comma in following order: host,port,usessl<true|false>,virtualhost,username,password");

	}

	private static FileAppender createNewAppender(String logFilePath) {
		FileAppender appender = new FileAppender();
		appender.setName("MyFileAppender");
		appender.setLayout(new PatternLayout(
				"%d{yyyy-MM-dd HH\\:mm\\:ss,SSS} %5p  %F\\:%L - %m%n"));
		appender.setFile(logFilePath);
		appender.setAppend(true);
		appender.setThreshold(Level.INFO);
		appender.activateOptions();
		Logger.getRootLogger().addAppender(appender);
		return appender;
	}

	final static Set<Channel> channels = new HashSet<Channel>();

	public static void main(String[] args) {
		logger.info("AMQP-Websocket bridge go up");

		final Connection connection;
		// Channel channel = null;

		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		Main.createOptions(options);

		CommandLine cmd = null;
		final URI uri2;
		String[] queue = null;

		try {
			cmd = parser.parse(options, args, false);
			if (!cmd.hasOption(OPTIONS.QUEUE.getDesc())
					|| !cmd.hasOption(OPTIONS.RECEIVER.getDesc()) | !cmd.hasOption(OPTIONS.CONNECTION.getDesc())) {
				throw new ParseException("Required Option is missing");
			}
			queue = ((String) cmd.getParsedOptionValue(OPTIONS.QUEUE.getDesc()))
					.split(",");
			String uri = (String) cmd.getParsedOptionValue(OPTIONS.RECEIVER
					.getDesc());
			String logFile = null;
			if (cmd.hasOption(OPTIONS.LOGFILE.getDesc())) {
				logFile = (String) cmd.getParsedOptionValue(OPTIONS.LOGFILE
						.getDesc());
				createNewAppender(logFile);
			} else {
				logger.info("Location for Logfile will be default /var/log/gisa-bridge.log");
			}
			uri2 = new URI(uri);
			Properties connectionDetails = readConnectiondetails((String) cmd.getParsedOptionValue(OPTIONS.CONNECTION
					.getDesc()));

			final ConnectionAmqp factory = new ConnectionAmqp.DefaultConnectionFactory();

			connection = factory.getConnection(connectionDetails);

			for (final String queueDef : queue) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						channels.add(addConsumer(connection, uri2, factory,
								queueDef));
					}
				}).start();

			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String line = "";
			System.out.println("Enter STOP to shutdown the bridge");
			while (!(line.trim().toLowerCase().equals("stop"))) {
				line = reader.readLine();
			}
		} catch (ParseException e) {
			logger.error("Unable to connect to the Queue. Parser Exception catched maybe wrong argument? " + e);
			HelpFormatter formatter = new HelpFormatter();
			try (PrintWriter pw = new PrintWriter(System.out)) {
				formatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH,
						"java -jar nameofthisjar.jar ", null, options,
						HelpFormatter.DEFAULT_LEFT_PAD,
						HelpFormatter.DEFAULT_DESC_PAD, null, true);
			}
			System.exit(1);
		} catch (URISyntaxException e) {
			logger.error("Unable to connect to the Queue. URISyntaxException catched maybe wrong format of URI? " + e);

		} catch (IOException | GeneralSecurityException | TimeoutException e) {
			logger.error("Unable to connect to the Queue " + e);
		} finally {
			closeAmqp(channels);
		}

	}

	private static Properties readConnectiondetails(String parsedOptionValue) {
		String[] conndetails = parsedOptionValue.split(",");
		for (String string : conndetails) {
			string.trim();
		}
		Properties prop = new Properties();
		prop.put("HOST", conndetails[0]);
		prop.put("PORT", conndetails[1]);
		prop.put("USESSL", conndetails[2]);
		prop.put("VHOST", conndetails[3]);
		prop.put("USER", conndetails[4]);
		prop.put("PASS", conndetails[5]);
		return prop;

	}

	private static Channel addConsumer(Connection connection, URI uri2,
			ConnectionAmqp factory, String queueDef) {

		String[] queuePar = queueDef.split("\\?");
		if (queuePar.length != 2) {
			logger.error("Define Queue folloved by?Type e.g. ExampleQueueTitle?LOG");
			System.exit(1);
		}
		QueueType type = QueueType.valueOf(queuePar[1].toUpperCase());
		Consumer consumentWithChannel = null;

		try {
			Channel channel = factory.appendConsumerAndGetChannelWitchManualAck(
					connection);

			System.out.println("nachtrichten auf der queue " + channel.queueDeclarePassive(queuePar[0]).getMessageCount());

			switch (type) {
			case LOG:

				consumentWithChannel = new QueueConsumer(channel,
						new WebSocketFactory.DefaultWebSocketFactory(uri2),
						new FallbackOnError() {

							@Override
							public void notifyShutdown(com.rabbitmq.client.Channel channel, String message) {

								Main.closeAmqp(channel);
							}

						}, QueueType.LOG);
				break;
			case DATA:
				consumentWithChannel = new QueueConsumer(channel,
						new WebSocketFactory.DefaultWebSocketFactory(uri2),
						new FallbackOnError() {
							@Override
							public void notifyShutdown(Channel channel, String message) {

								Main.closeAmqp(channel);
							}
						}, QueueType.DATA);
				break;
			default:
				break;
			}
			channel.basicConsume(queuePar[0], false, consumentWithChannel);

			return channel;
		} catch (Exception e) {
			logger.error("Unable to open new channel " + e);
		}
		return null;
	}

	public static void closeAmqp(com.rabbitmq.client.Channel channel) {
		// Connection connection = null;
		if (channel != null && channel.isOpen())
			try {
				// connection = channel.getConnection();
				channel.close();
				logger.info("Channel closed ");
			} catch (Exception e) {
				logger.error("Could not close the channel " + e.getMessage()
						+ " " + e);

			}
		channels.remove(channel);
		if (channels.size() == 0)
			System.exit(0);

		// if (connection != null)
		// try {
		// connection.close();
		// logger.info("Connection closed ");
		// } catch (IOException e) {
		// logger.error("Could not close the connection " + e.getMessage() + " "
		// + e);
		// }
	}

	public static void closeAmqp(Set<Channel> channels) {
		Connection connection = null;
		for (Channel channel : channels) {

			if (channel != null)
				try {
					connection = channel.getConnection();
					channel.close();
					logger.info("Channel closed ");
				} catch (Exception e) {
					logger.error("Could not close the channel "
							+ e.getMessage() + " " + e);
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
					logger.info("Connection closed ");
				} catch (IOException e) {
					logger.error("Could not close the connection "
							+ e.getMessage() + " " + e);
				}
		}
	}
}