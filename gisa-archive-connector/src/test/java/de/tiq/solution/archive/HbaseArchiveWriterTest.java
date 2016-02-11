package de.tiq.solution.archive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tiq.solutions.archive.writer.HbaseArchiveWriter;
import de.tiq.solutions.gisaconnect.amqp.QueueType;

public class HbaseArchiveWriterTest {
	public static String TEST_TABLE = "testTable";
	public static String TEST_QUEUE = "tiqsolutions-q-Vertrag1AnlagendatenTest";
	public static String TEST_EVENT_QUEUE = "tiqsolutions-q-Vertrag1LogmeldungenTest";
	private org.apache.hadoop.hbase.client.Connection hbaseTestConnection;
	private HbaseArchiveWriter writer;
	private Admin admin;
	private Table table;

	@Before
	public void before() {
		hbaseTestConnection = Mockito.mock(org.apache.hadoop.hbase.client.Connection.class);
		writer = Mockito.mock(HbaseArchiveWriter.class);
		admin = Mockito.mock(Admin.class);
		table = Mockito.mock(Table.class);
	}

	@Test
	public void constuctorTest() throws Exception {
		HbaseArchiveWriter w = new HbaseArchiveWriter(hbaseTestConnection, "testTable", QueueType.DATA);
		HbaseArchiveWriter w2 = new HbaseArchiveWriter(hbaseTestConnection, "testTable", QueueType.LOG);
		when(admin.tableExists(TableName.valueOf(TEST_TABLE))).thenReturn(true);
		assertNotNull(w);
	}

	@Test
	public void setUpTest() throws Exception {
		HbaseArchiveWriter w = new HbaseArchiveWriter(hbaseTestConnection, "testTable", QueueType.DATA);
		when(hbaseTestConnection.getAdmin()).thenReturn(admin);
		when(admin.tableExists(TableName.valueOf(TEST_TABLE))).thenReturn(false);
		when(writer.getTable()).thenReturn(table);
		w.setup("");
		assertNull(w.getTable());

		when(admin.tableExists(TableName.valueOf(TEST_TABLE))).thenReturn(true);
		assertNotNull(w);
	}

	@Test
	public void transferTest() throws Exception {
		when(hbaseTestConnection.getAdmin()).thenReturn(admin);
		when(admin.tableExists(any(TableName.class))).thenReturn(true);
		when(hbaseTestConnection.getTable(any(TableName.class))).thenReturn(table);
		doNothing().when(table).put(any(Put.class));

		HbaseArchiveWriter hbaseArchiveWriter = new HbaseArchiveWriter(hbaseTestConnection, "testTable", QueueType.LOG);
		hbaseArchiveWriter.setup("");
		assertTrue(hbaseArchiveWriter
				.transferData("{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WebBox\",\"module\":\"RasClient\",\"messageCode\":\"13000\",\"messageArgs\":\"*99***1#\",\"message\":\"RAS_Start_Dialing_1\"}"));
		//
		// ArgumentCaptor<Put> captor = ArgumentCaptor.forClass(Put.class);
		// verify(table).put(captor.capture());
		// System.out.println(captor.getValue().getClass());

		HbaseArchiveWriter hbaseArchiveWriterData = new HbaseArchiveWriter(hbaseTestConnection, "testTable", QueueType.DATA);
		hbaseArchiveWriterData.setup("");
		assertTrue(hbaseArchiveWriterData
				.transferData("{\"ts\":\"2015-10-06T05:42:30.119+0000\",\"val\":[{\"key\":\"WRTP468C:2110595689:A.Ms.Amp\",\"value\":2.419}]}"));
	}

	// @Test
	// public void hbaseDecoratorTest() throws Exception {
	// QueueType type = QueueType.valueOf("DATA");
	//
	// org.apache.hadoop.hbase.client.Connection hbaseConnection = new
	// Connection.HbaseConnection()
	// .getConnection(this.getClass()
	// .getResource("/hbase-site.xml").getFile());
	// assertFalse(hbaseConnection.getAdmin().tableExists(TableName.valueOf(TEST_TABLE)));
	//
	// com.rabbitmq.client.Connection amqpConnection = new
	// Connection.AmqpConnection()
	// .getConnection("test.connect.gisa.de,5671,true,gisa,tiqsolutions,sae1yedu3Aid3ie".split(","));
	// assertNotNull(amqpConnection);
	//
	// ArchiveConnector hbaseAmqpDecorator = new
	// HBaseArchiveConnectorDecorator(new HbaseArchiveWriter(hbaseConnection,
	// TEST_TABLE,
	// type),
	// amqpConnection);
	// hbaseAmqpDecorator.setup(TEST_QUEUE);
	// assertTrue(hbaseConnection.getAdmin().tableExists(TableName.valueOf(TEST_TABLE)));
	// Channel createChannel = amqpConnection.createChannel();
	// assertTrue(createChannel.isOpen());
	// Thread.sleep(1000);
	// hbaseConnection.getAdmin().disableTable(TableName.valueOf(TEST_TABLE));
	// hbaseConnection.getAdmin().deleteTable(TableName.valueOf(TEST_TABLE));
	//
	// }
	//
	// @Test
	// public void hbaseDecoratorLogTest() throws Exception {
	// QueueType type = QueueType.valueOf("LOG");
	//
	// org.apache.hadoop.hbase.client.Connection hbaseConnection = new
	// Connection.HbaseConnection()
	// .getConnection(this.getClass()
	// .getResource("/hbase-site.xml").getFile());
	// assertFalse(hbaseConnection.getAdmin().tableExists(TableName.valueOf(TEST_TABLE)));
	//
	// com.rabbitmq.client.Connection amqpConnection = new
	// Connection.AmqpConnection()
	// .getConnection("test.connect.gisa.de,5671,true,gisa,tiqsolutions,sae1yedu3Aid3ie".split(","));
	// assertNotNull(amqpConnection);
	//
	// ArchiveConnector hbaseAmqpDecorator = new
	// HBaseArchiveConnectorDecorator(new HbaseArchiveWriter(hbaseConnection,
	// TEST_TABLE,
	// type),
	// amqpConnection);
	// hbaseAmqpDecorator.setup(TEST_EVENT_QUEUE);
	// assertTrue(hbaseConnection.getAdmin().tableExists(TableName.valueOf(TEST_TABLE)));
	// Channel createChannel = amqpConnection.createChannel();
	// assertTrue(createChannel.isOpen());
	// hbaseConnection.getAdmin().disableTable(TableName.valueOf(TEST_TABLE));
	// hbaseConnection.getAdmin().deleteTable(TableName.valueOf(TEST_TABLE));
	//
	// }
	//
	// @Test
	// public void testLogfileArguments() throws Exception {
	// // String[] args = new String[] { "-q", "Queuname?DATA,Quename2?LOG",
	// // "-p", "e:/tmp/test.log",
	// // "-c", "AMQP-Server details",
	// // "-t", "Tabelle" };
	// // Main.main(args);
	// // System.setIn(new StringBufferInputStream("stop"));
	//
	// }
}
