package de.tiq.solutions.archive.connection;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.ConnectionFactory;

import de.tiq.solutions.archive.connection.Connection.AmqpConnection;

public class ConnectionTest {
	@Test
	public void aMQPConnectionTest() throws IOException, GeneralSecurityException, TimeoutException {
		try {
			com.rabbitmq.client.Connection connection = new Connection.AmqpConnection().getConnection("amqp,1234,true,test,test,test".split(","));
		} catch (UnknownHostException e) {
			assertEquals(e.getMessage(), "amqp");

		}
		try {
			com.rabbitmq.client.Connection connection2 = new
					Connection.AmqpConnection().getConnection("host,1234,false,test,test,test".split(","));
		} catch (UnknownHostException e) {
			assertEquals(e.getMessage(), "host");

		}
		try {
			com.rabbitmq.client.Connection connection2 = new
					Connection.AmqpConnection().getConnection("test,1234,".split(","));
		} catch (ArrayIndexOutOfBoundsException e) {
			assertEquals(e.getMessage(), "2");

		}
	}

	@Test
	public void aMQPConnection2Test() throws Exception {
		ConnectionFactory mock = Mockito.mock(com.rabbitmq.client.ConnectionFactory.class);
		AmqpConnection amqpC = Mockito.mock(AmqpConnection.class);
		when(amqpC.getFactory()).thenReturn(mock);
		when(mock.newConnection()).thenReturn(Mockito.mock(com.rabbitmq.client.Connection.class));
		doNothing().when(mock).useSslProtocol();
		doNothing().when(mock).setVirtualHost(any(String.class));
		doNothing().when(mock).setUsername(any(String.class));
		doNothing().when(mock).setPassword(any(String.class));
		com.rabbitmq.client.Connection connection =
				amqpC.getConnection("amqp,1234,true,test,test,test".split(","));

	}

	@Test
	public void hbaseConnectionTest() throws Exception {
		Connection.HbaseConnection hbaseConnection = new Connection.HbaseConnection();
		org.apache.hadoop.hbase.client.Connection hbaseConn = hbaseConnection.getConnection(this.getClass()
				.getResource("/hbase-site.xml").getFile());
		assertNotNull(hbaseConn);
		hbaseConn.close();
		assertTrue(hbaseConn.isClosed());
		org.apache.hadoop.hbase.client.Connection hbaseConn2 = hbaseConnection.getConnection(this.getClass()
				.getResource("/hbase-site.xml").getFile());

	}
}
