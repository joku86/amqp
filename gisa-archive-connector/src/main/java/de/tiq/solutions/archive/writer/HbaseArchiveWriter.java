package de.tiq.solutions.archive.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.tiq.solutions.archive.connection.ArchiveConnector;
import de.tiq.solutions.gisaconnect.amqp.QueueConsumer.QUEUETYPE;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel.Val;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindLOGModel;
import de.tiq.solutions.gisaconnect.util.FromToConverter;

public class HbaseArchiveWriter implements ArchiveConnector {

	private String tableName = "GISA_ARCHIVE";
	private Table table;
	private FromToConverter converter = new FromToConverter();
	ObjectMapper mapper = new ObjectMapper();
	private Connection hbaseConnection;
	private QUEUETYPE type;

	public HbaseArchiveWriter(Connection hbaseConnection, String tableName, QUEUETYPE type) {
		this.hbaseConnection = hbaseConnection;
		this.tableName = tableName;
		this.type = type;
	}

	public boolean transferData(String message) {
		try {
			switch (type) {
			case DATA:
				GisaEvermindDATAModel readValue = mapper.readValue(message, GisaEvermindDATAModel.class);
				List<Put> puts = new ArrayList<Put>();
				for (Val val : readValue.getVal()) {
					Put put = new Put(Bytes.toBytes(converter.getRowKeyForData(val)));
					put.addColumn(Bytes.toBytes("measuredvalue"), Bytes.toBytes(val.getKey().split(":")[2]), readValue.getTs().getTime(),
							Bytes.toBytes(Double.toString(val.getValue())));
					// wenn Bytes.toBytes(double) in hbase steht das ByteArray
					puts.add(put);
				}
				table.put(puts);
				return true;
			case LOG:
				GisaEvermindLOGModel log = mapper.readValue(message, GisaEvermindLOGModel.class);
				Put put = new Put(Bytes.toBytes(converter.getRowKeyForData(log)));
				put.addColumn(Bytes.toBytes("event"), Bytes.toBytes(log.getMessageCode()), log.getTs().getTime(),
						Bytes.toBytes(log.getMessage()));
				table.put(put);
				return true;

			}
			return false;
		} catch (Exception e) {
			return false;
		}

	}

	public void shutDown() {
		try {
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Semaphore _sem = new Semaphore(1);

	public void setup(String... args) throws IOException {

		Admin admin = hbaseConnection.getAdmin();

		try {
			_sem.acquire();
			if (!admin.tableExists(TableName.valueOf(tableName))) {
				HTableDescriptor td = new HTableDescriptor(TableName.valueOf(tableName));
				HColumnDescriptor family = new HColumnDescriptor("measuredvalue");
				family.setMaxVersions(Integer.MAX_VALUE);
				HColumnDescriptor family2 = new HColumnDescriptor("event");
				family2.setMaxVersions(Integer.MAX_VALUE);
				td.addFamily(family);
				td.addFamily(family2);
				if (!admin.tableExists(TableName.valueOf(tableName)))
					admin.createTable(td);
				admin.flush(TableName.valueOf(tableName));
			}
			_sem.release();
			table = hbaseConnection.getTable(TableName.valueOf(tableName));
			System.out.println("tabelle gebaut und gesetzt " + table.getName());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
