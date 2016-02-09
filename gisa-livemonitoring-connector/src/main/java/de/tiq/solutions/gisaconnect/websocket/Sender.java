package de.tiq.solutions.gisaconnect.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

public interface Sender {

	public static class WebsocketSender implements Sender {

		private WebSocketFactory webSocketFactory;
		private Session localSession;

		public WebsocketSender(WebSocketFactory webSocketFactory) {
			this.webSocketFactory = webSocketFactory;
		}

		@Override
		public void send(String data) throws IOException {
			sendRecursive(data, 2);
		}

		private void sendRecursive(String data, int i) throws IOException {
			try {
				Basic basicRemote = getSession().getBasicRemote();
				basicRemote.sendObject(data);
			} catch (IOException e) {
				if (i == 0) {
					localSession = null;
					throw e;
				} else {
					Session session = localSession;
					if (session != null) {
						session.close(new CloseReason(CloseCodes.GOING_AWAY, null));

					}
					sendRecursive(data, --i);
				}
			} catch (EncodeException e) {
				throw new IOException(e);
			}
		}

		private Session getSession() throws IOException {
			if (localSession == null) {
				localSession = webSocketFactory.createNewSession(this);
			}
			return localSession;
		}

		public void alertOnClose() {
			localSession = null;
		}

	}

	void send(String data) throws IOException;

}