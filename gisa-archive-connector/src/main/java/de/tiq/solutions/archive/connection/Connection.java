package de.tiq.solutions.archive.connection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeoutException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import de.tiq.solutions.gisaconnect.amqp.ConnectionAmqp;

public interface Connection<T> {
	T getConnection(String... resourcen) throws IOException, GeneralSecurityException, TimeoutException;

	public static class AmqpConnection implements Connection<com.rabbitmq.client.Connection> {

		public com.rabbitmq.client.Connection getConnection(String... resourcen) throws IOException, GeneralSecurityException, TimeoutException {
			return new ConnectionAmqp.DefaultConnectionFactory().getConnection(resourcen[0], Integer.parseInt(resourcen[1]),
					Boolean.parseBoolean(resourcen[2]),
					resourcen[3], resourcen[4], resourcen[5]);
		}

		public void close() throws IOException {

		}

	}

	public static class HbaseConnection implements Connection<org.apache.hadoop.hbase.client.Connection> {
		private static Configuration config;
		private org.apache.hadoop.hbase.client.Connection createdConnection;

		private Configuration getHbaseConfig(String hbaseSitePath) {
			config = HBaseConfiguration.create();
			config.addResource(new Path(hbaseSitePath));
			return config;

		}

		public org.apache.hadoop.hbase.client.Connection getConnection(String... resourcen) throws IOException {
			Configuration hbaseConfig = getHbaseConfig(resourcen[0]);
			return ConnectionFactory.createConnection(hbaseConfig);
			// return screatedConnection;
		}

		// private void getConnection(Configuration hbaseConfig) throws
		// IOException {
		// if (createdConnection == null)
		// createdConnection = ConnectionFactory.createConnection(hbaseConfig);
		// else {
		// if (createdConnection.isClosed()) {
		// // unknown how to open
		// createdConnection = ConnectionFactory.createConnection(hbaseConfig);
		// }
		// }
		// }

		public void close() throws IOException {
			createdConnection.close();
		}

	}

	void close() throws IOException;

}
