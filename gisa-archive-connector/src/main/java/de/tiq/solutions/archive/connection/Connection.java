package de.tiq.solutions.archive.connection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeoutException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.ConnectionFactory;

public interface Connection<T> {
	T getConnection(String... resourcen) throws IOException, GeneralSecurityException, TimeoutException;

	public static class AmqpConnection implements Connection<com.rabbitmq.client.Connection> {

		public com.rabbitmq.client.Connection getConnection(String... resourcen) throws IOException, GeneralSecurityException, TimeoutException {
			com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
			factory.setHost(resourcen[0]);
			factory.setPort(Integer.parseInt(resourcen[1]));
			if (Boolean.parseBoolean(resourcen[2]))
				factory.useSslProtocol();
			factory.setVirtualHost(resourcen[3]);
			factory.setUsername(resourcen[4]);
			factory.setPassword(resourcen[5]);
			com.rabbitmq.client.Connection newConnection = factory.newConnection();
			return newConnection;
		}

		public void close() throws IOException {
			// TODO Auto-generated method stub

		}

	}

	public static class HbaseConnection implements Connection<org.apache.hadoop.hbase.client.Connection> {
		private static Configuration config;
		private org.apache.hadoop.hbase.client.Connection createdConnection;

		private Configuration getHbaseConfig(String hbaseSitePath) {
			if (config == null) {
				config = HBaseConfiguration.create();
				config.addResource(new Path(hbaseSitePath));

				return config;
			}
			return config;
		}

		public org.apache.hadoop.hbase.client.Connection getConnection(String... resourcen) throws IOException {
			Configuration hbaseConfig = getHbaseConfig(resourcen[0]);
			getConnection(hbaseConfig);
			return createdConnection;
		}

		private void getConnection(Configuration hbaseConfig) throws IOException {
			if (createdConnection == null)
				createdConnection = ConnectionFactory.createConnection(hbaseConfig);
			else {
				if (createdConnection.isClosed()) {
					// unknown how to open
					createdConnection = ConnectionFactory.createConnection(hbaseConfig);
				}
			}
		}

		public void close() throws IOException {
			createdConnection.close();
		}

	}

	void close() throws IOException;

}
