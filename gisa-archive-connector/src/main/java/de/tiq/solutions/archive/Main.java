package de.tiq.solutions.archive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.apache.log4j.RollingFileAppender;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import de.tiq.solutions.archive.connection.ArchiveConnector;
import de.tiq.solutions.archive.connection.Connection;
import de.tiq.solutions.archive.connection.HBaseArchiveConnectorDecorator;
import de.tiq.solutions.archive.writer.HbaseArchiveWriter;
import de.tiq.solutions.gisaconnect.amqp.FallbackOnError;
import de.tiq.solutions.gisaconnect.amqp.QueueConsumer;

public class Main {
	private static final Logger logger = Logger
			.getLogger("GISA-archive-service");
	private static List<ArchiveConnector> connectorDekorators;

	@SuppressWarnings("unused")
	private static enum OPTIONS {

		DESTINATIONTABLE("t", "Table"), QUEUE("q", "Queue"), RECEIVER("r", "Receiver"), LOGFILE("p",
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
				"Queuename followed by ? and Type of the Queue(LOG or DATA).");
		options.addOption(OPTIONS.RECEIVER.getDesc(), true,
				"Path to the configuration file for HBase e.g.:/tmp/hbase-site.xml");
		options.addOption(OPTIONS.LOGFILE.getDesc(), false,
				"Path where the logfile should be saved");
		options.addOption(OPTIONS.DESTINATIONTABLE.getDesc(), true,
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
		logger.info("Custom location for logfile: " + logFilePath);
		return appender;
	}

	public static void main(String[] args) {
		logger.info("--- Archivesystem goes up ---");
		connectorDekorators = new ArrayList<ArchiveConnector>();

		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		Main.createOptions(options);
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args, false);
			if (!cmd.hasOption(OPTIONS.DESTINATIONTABLE.getDesc()) || !cmd.hasOption(OPTIONS.QUEUE.getDesc())
					|| !cmd.hasOption(OPTIONS.CONNECTION.getDesc())) {
				throw new ParseException("Required Option is missing");
			}
			handleLogfile(cmd);
			String[] queue = ((String) cmd.getParsedOptionValue(OPTIONS.QUEUE.getDesc()))
					.split(",");
			final String hbaseSiteFile = (String) cmd.getParsedOptionValue(OPTIONS.RECEIVER.getDesc());
			final String table = (String) cmd.getParsedOptionValue(OPTIONS.DESTINATIONTABLE.getDesc());
			final String[] amqpServerConf = ((String) cmd.getParsedOptionValue(OPTIONS.CONNECTION.getDesc())).split(",");

			for (final String queueDef : queue) {
				try {
					connectorDekorators.add(consumeQueue(hbaseSiteFile,
							table, queueDef, amqpServerConf));
				} catch (Exception e) {
					logger.error("error in start");
				}
			}
			observeConsoleInput(connectorDekorators);

		} catch (ParseException e2) {
			logger.error("Unable to parse startarguments. Exception catched maybe wrong argument? " + e2);
			printStartHelp(options);

		}

	}

	private static void printStartHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		try (PrintWriter pw = new PrintWriter(System.out)) {
			formatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH,
					"java -jar nameofthisjar.jar ", null, options,
					HelpFormatter.DEFAULT_LEFT_PAD,
					HelpFormatter.DEFAULT_DESC_PAD, null, true);
		}
	}

	private static void observeConsoleInput(final List<ArchiveConnector> connectorDekorators) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String line = "";
			System.out.println("Enter STOP to shutdown the bridge");
			while (!(line.trim().toLowerCase().equals("stop"))) {
				line = reader.readLine();
			}
		} catch (Exception e) {
			logger.error("Some error happens " + e);
		} finally {
			try {
				shutDown(connectorDekorators);
			} catch (Exception e) {
				logger.error("Could not teardown the open connections " + e);
			}
			System.exit(0);
		}
	}

	private static void shutDown(List<ArchiveConnector> connectors) throws Exception {
		for (ArchiveConnector archiveConnector : connectors) {
			archiveConnector.shutDown();
		}
	}

	private static ArchiveConnector consumeQueue(String hbaseSiteFile, String table, String queueDef, String[] amqpServerConf) throws IOException,
			InterruptedException, GeneralSecurityException, TimeoutException {
		// Inhalt eines Threads

		String[] queuePar = queueDef.split("\\?");
		if (queuePar.length != 2) {
			logger.error("Define Queue folloved by?Type e.g. ExampleQueueTitle?LOG");
			System.exit(1);
		}
		QueueConsumer.QUEUETYPE type = QueueConsumer.QUEUETYPE.valueOf(queuePar[1]);

		org.apache.hadoop.hbase.client.Connection hbaseConnection = null;
		hbaseConnection = new Connection.HbaseConnection()
				.getConnection(hbaseSiteFile);

		final com.rabbitmq.client.Connection amqpConnection = new Connection.AmqpConnection()
				.getConnection(amqpServerConf);

		if (amqpConnection != null) {
			final ArchiveConnector hbaseAmqpDecorator = new
					HBaseArchiveConnectorDecorator(new
							HbaseArchiveWriter(hbaseConnection, table,
									type),
							amqpConnection, new FallbackOnError() {

								@Override
								public void notifyShutdown(Channel channel) {
									if (connectorDekorators.size() == 1)
										System.exit(1);
									connectorDekorators.remove(0);
								}
							});
			hbaseAmqpDecorator.setup(queuePar[0]);
			return hbaseAmqpDecorator;
		}
		return null;
	}

	private static void handleLogfile(CommandLine cmd) throws ParseException {
		String logFile;
		if (cmd.hasOption(OPTIONS.LOGFILE.getDesc())) {
			logFile = (String) cmd.getParsedOptionValue(OPTIONS.LOGFILE
					.getDesc());
			createNewAppender(logFile);
		}
		else {
			RollingFileAppender appender = (RollingFileAppender) Logger.getRootLogger().getAppender("R");
			logger.info("Use default Logfile location: " + appender.getFile());

		}
	}
}
