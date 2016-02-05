package de.tiq.solutions.gisaconnect.websocket;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import de.tiq.solutions.gisaconnect.websocket.Sender.WebsocketSender;

public interface WebSocketFactory {
	public static class DefaultWebSocketFactory implements WebSocketFactory {

		@ClientEndpoint
		public static class GisaEndpoint {
			private WebsocketSender websocketSender;

			public GisaEndpoint(WebsocketSender websocketSender) {
				this.websocketSender = websocketSender;
			}

			@OnClose
			public void onClose(Session session) {
				websocketSender.alertOnClose();
			}

			@OnError
			public void onError(Throwable t) {
				websocketSender.alertOnClose();
			}

		}

		private final URI webSocketURI;

		public DefaultWebSocketFactory(URI webSocketURI) {
			this.webSocketURI = webSocketURI;
		}

		@Override
		public Session createNewSession(WebsocketSender websocketSender) throws IOException {
			try {
				return ClientManager.createClient().connectToServer(new DefaultWebSocketFactory.GisaEndpoint(websocketSender), webSocketURI);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	Session createNewSession(WebsocketSender websocketSender) throws IOException;
}