package de.tiq.solutions.archive.connection;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.ConnectionFactory;

public interface Connection<T> {
	T getConnection(String... resourcen);

	public static class HbaseConnection implements Connection<org.apache.hadoop.hbase.client.Connection> {
		private static Configuration config;
		private org.apache.hadoop.hbase.client.Connection createdConnection;

		private static Configuration getHbaseConfig(String hbaseSitePath) {
			if (config == null) {
				Configuration that = new Configuration();
				that.addResource(new Path(hbaseSitePath));
				config = HBaseConfiguration.create(that);
				return config;
			}
			return config;

		}

		public org.apache.hadoop.hbase.client.Connection getConnection(String... resourcen) {
			try {
				createdConnection = ConnectionFactory.createConnection(getHbaseConfig(resourcen[0]));
				return createdConnection;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public void close() throws IOException {
			createdConnection.close();

		}

	}

	void close() throws IOException;

}
