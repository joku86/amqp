package de.tiq.solutions.archive.writer;

import java.io.IOException;
import java.util.Collection;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;

import de.tiq.solutions.archive.connection.ArchiveConnector;

public class HbaseArchiveWriter implements ArchiveConnector {

	private org.apache.hadoop.hbase.client.Connection hbaseConnection;

	public HbaseArchiveWriter(org.apache.hadoop.hbase.client.Connection hbaseConnection) {
		this.hbaseConnection = hbaseConnection;
	}

	public void transferData(Collection<String> test) {
		System.out.println("daten werden geschrieben");

	}

	public void shutDown() {

	}

	public void setup(String... args) {
		try {
			Admin admin = hbaseConnection.getAdmin();
			System.out.println("tabelle Eumonis wurde gefunden " + admin.tableExists(TableName.valueOf("EUMONIS_PV")));
			Table hTable = hbaseConnection.getTable(TableName.valueOf("EUMONIS_PV"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Habse wird verbunden");

	}
}
