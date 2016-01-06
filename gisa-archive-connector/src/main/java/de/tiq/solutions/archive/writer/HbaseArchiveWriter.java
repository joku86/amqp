package de.tiq.solutions.archive.writer;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import de.tiq.solutions.archive.connection.ArchiveConnector;

public class HbaseArchiveWriter implements ArchiveConnector {
	private String tableName = "GISA_ARCHIVE";
	private Table table;

	private Connection hbaseConnection;

	public HbaseArchiveWriter(Connection hbaseConnection, String tableName) {
		this.hbaseConnection = hbaseConnection;
		this.tableName = tableName;
	}

	public void transferData(Collection<String> test) {
		System.out.println("daten werden geschrieben");
		try {

			Put put = new Put(Bytes.toBytes("PVRTG000000001::Fuchshain Bauabschnitt I::Wechselrichter::STP 17000TL-10::2110144057::Wechselrichter"));

			put.add(Bytes.toBytes("mean"), Bytes.toBytes("Pac"), new Date().getTime(), Bytes.toBytes("10"));
			table.put(put);
			table.close();
			System.out.println("fertig geschrieben und closed");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("daten sind nun in der DB");

	}

	public void shutDown() {

	}

	public void setup(String... args) {

		// HBaseAdmin admin = new HBaseAdmin(hbaseConnection);

		Admin admin;
		try {
			admin = hbaseConnection.getAdmin();

			if (!admin.tableExists(TableName.valueOf(tableName))) {
				HTableDescriptor td = new HTableDescriptor(TableName.valueOf(tableName));
				HColumnDescriptor family = new HColumnDescriptor("mean");
				family.setMaxVersions(Integer.MAX_VALUE);
				td.addFamily(family);
				admin.createTable(td);
				admin.flush(TableName.valueOf(tableName));

			}
			table = hbaseConnection.getTable(TableName.valueOf(tableName));
			System.out.println("tabelle gebaut und gesetzt " + table.getName());
		} catch (IOException e) {

		}

		System.out.println("Habse wird verbunden");

	}
}
