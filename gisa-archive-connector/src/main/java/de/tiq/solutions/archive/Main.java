package de.tiq.solutions.archive;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import de.tiq.solutions.archive.connection.ArchiveConnector;
import de.tiq.solutions.archive.connection.Connection;
import de.tiq.solutions.archive.connection.Connection.HbaseConnection;
import de.tiq.solutions.archive.connection.HBaseArchiveConnectorDecorator;
import de.tiq.solutions.archive.writer.HbaseArchiveWriter;

public class Main {
	private static final Logger logger = Logger
			.getLogger("GISA-archive-service");

	@SuppressWarnings("unused")
	private static enum OPTIONS {

		DESTINATIONTABLE("t", "Table"), QUEUE("q", "Queue"), HBASECONF("r", "Database"), LOGFILE("p",
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
		options.addOption(OPTIONS.HBASECONF.getDesc(), true,
				"Path to the configuration file for HBase e.g.:/tmp/hbase-site.xml");
		options.addOption(OPTIONS.LOGFILE.getDesc(), false,
				"Path where the logfile should be saved");
		options.addOption(OPTIONS.DESTINATIONTABLE.getDesc(), true,
				"Table where the data will be stored");
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

		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		Main.createOptions(options);

		CommandLine cmd = null;
		String hbaseSiteFile = null;
		String[] queue = null;
		String table = null;

		try {
			cmd = parser.parse(options, args, false);
			if (!cmd.hasOption(OPTIONS.DESTINATIONTABLE.getDesc()) || !cmd.hasOption(OPTIONS.QUEUE.getDesc())
					|| !cmd.hasOption(OPTIONS.CONNECTION.getDesc())) {
				throw new ParseException("Required Option is missing");
			}
			handleLogfile(cmd);
			queue = ((String) cmd.getParsedOptionValue(OPTIONS.QUEUE.getDesc()))
					.split(",");
			hbaseSiteFile = (String) cmd.getParsedOptionValue(OPTIONS.HBASECONF.getDesc());
			table = (String) cmd.getParsedOptionValue(OPTIONS.DESTINATIONTABLE.getDesc());

		} catch (ParseException e2) {
			logger.error("Unable to parse startarguments. Exception catched maybe wrong argument? " + e2);
			HelpFormatter formatter = new HelpFormatter();
			try (PrintWriter pw = new PrintWriter(System.out)) {
				formatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH,
						"java -jar nameofthisjar.jar ", null, options,
						HelpFormatter.DEFAULT_LEFT_PAD,
						HelpFormatter.DEFAULT_DESC_PAD, null, true);
			}
			System.exit(1);
		}

		org.apache.hadoop.hbase.client.Connection hbaseConnection = null;
		try {
			hbaseConnection = new Connection.HbaseConnection()
					.getConnection(hbaseSiteFile);
		} catch (IOException e) {
			logger.error("Could not create Connection ");
		}
		com.rabbitmq.client.Connection amqpConnection = null;
		try {
			amqpConnection = new Connection.AmqpConnection()
					.getConnection();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArchiveConnector hbaseAmqpDecorator = new HBaseArchiveConnectorDecorator(new HbaseArchiveWriter(hbaseConnection, table), amqpConnection);
		// Beispiel
		// ArchiveConnector mysql = new HBaseArchiveConnectorDecorator(new
		// MySqlWriter(), null, null);
		hbaseAmqpDecorator.setup("tiqsolutions-q-Vertrag1Anlagendaten");
		try {
			Thread.sleep(5000);
			hbaseAmqpDecorator.shutDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
