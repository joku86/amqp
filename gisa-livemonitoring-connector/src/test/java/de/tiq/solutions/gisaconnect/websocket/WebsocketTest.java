package de.tiq.solutions.gisaconnect.websocket;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import de.tiq.solutions.gisaconnect.websocket.Sender.WebsocketSender;
import de.tiq.solutions.gisaconnect.websocket.WebSocketFactory.DefaultWebSocketFactory;
import de.tiq.solutions.gisaconnect.websocket.WebSocketFactory.DefaultWebSocketFactory.GisaEndpoint;

public class WebsocketTest {
	@Test
	public void send_data_to_websocket() throws Exception {
		WebSocketFactory webSocketFactory = mock(WebSocketFactory.class);
		Session session = mock(Session.class);
		Basic basicRemote = mock(Basic.class);

		WebsocketSender websocketSender = new WebsocketSender(webSocketFactory);
		when(webSocketFactory.createNewSession(websocketSender)).thenReturn(session);
		when(session.isOpen()).thenReturn(Boolean.TRUE);
		when(session.getBasicRemote()).thenReturn(basicRemote);

		websocketSender.send("test");

		verify(webSocketFactory).createNewSession(websocketSender);
		verify(session, atLeastOnce()).getBasicRemote();
		verify(basicRemote, atLeastOnce()).sendObject(any(String.class));
		doThrow(IOException.class).when(basicRemote).sendObject(any(String.class));
		try {
			websocketSender.send("test");

		} catch (IOException e) {

		}
		websocketSender.alertOnClose();

	}

	@Test
	public void could_not_connect_to_server() throws Exception {
		WebSocketFactory webSocketFactory = mock(WebSocketFactory.class);

		WebsocketSender sender = new WebsocketSender(webSocketFactory);

		try {
			when(webSocketFactory.createNewSession(sender)).thenThrow(IOException.class);
			sender.send("test");
			fail("Should not be reached");
		} catch (IOException e) {
		}
		verify(webSocketFactory, times(3)).createNewSession(sender);

	}

	@Test
	public void factoryTest() throws Exception {
		WebsocketSender mock2 = Mockito.mock(WebsocketSender.class);
		GisaEndpoint gisaEndpoint = new DefaultWebSocketFactory.GisaEndpoint(mock2);
		Session session = mock(Session.class);
		gisaEndpoint.onClose(session);
		gisaEndpoint.onError(new Throwable());
		try {
			DefaultWebSocketFactory defaultWebSocketFactory = new DefaultWebSocketFactory(new URI("ws://test:80"));
			defaultWebSocketFactory.createNewSession(mock2);
		} catch (IOException e) {
		}
	}

	// @Test
	// public void websocket_should_reconnect() throws Exception {
	// WebSocketFactory webSocketFactory = mock(WebSocketFactory.class);
	// Session session = mock(Session.class);
	// Basic basicRemote = mock(Basic.class);
	//
	// when(webSocketFactory.createNewSession()).thenReturn(session);
	// when(session.getBasicRemote()).thenReturn(basicRemote);
	// doThrow(IOException.class).doNothing().when(basicRemote).sendObject(any(EnergyArchiveData.class));
	//
	// hBaseSender.send(new EnergyArchiveData());
	//
	// verify(session).close(any(CloseReason.class));
	// verify(basicRemote, times(2)).sendObject(any(EnergyArchiveData.class));
	// }
	//
	// @Test
	// public void server_closing_session() throws Exception {
	// WebSocketFactory webSocketFactory = mock(WebSocketFactory.class);
	// Session session = mock(Session.class);
	// Basic basicRemote = mock(Basic.class);
	//
	// when(webSocketFactory.createNewSession()).thenReturn(session);
	// when(session.getBasicRemote()).thenReturn(basicRemote);
	// when(session.isOpen()).thenReturn(Boolean.FALSE, Boolean.TRUE);
	//
	// doThrow(IOException.class).doNothing().when(basicRemote).sendObject(any(EnergyArchiveData.class));
	//
	// HBaseSender hBaseSender = new HBaseSender(webSocketFactory);
	// hBaseSender.send(new EnergyArchiveData());
	//
	// verify(session).isOpen();
	// verify(basicRemote, times(2)).sendObject(any(EnergyArchiveData.class));
	// }

}
