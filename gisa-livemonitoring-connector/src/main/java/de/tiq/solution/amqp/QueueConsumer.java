package de.tiq.solution.amqp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.websocket.Session;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import de.tiq.solution.transformation.Context;
import de.tiq.solution.transformation.TransformationException;
import de.tiq.solution.transformation.transformator.AmqpDataJsonToRowkey;
import de.tiq.solution.transformation.transformator.AmqpEventJsonToRowkey;
import de.tiq.solutions.gisaconnect.amqp.FallbackOnError;
import de.tiq.solutions.gisaconnect.amqp.QueueType;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindLOGModel;
import de.tiq.solutions.gisaconnect.websocket.Sender;
import de.tiq.solutions.gisaconnect.websocket.Sender.WebsocketSender;
import de.tiq.solutions.gisaconnect.websocket.WebSocketFactory;

public class QueueConsumer implements Consumer {

	private Context context;
	ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = Logger.getLogger("QueueConsumer");
	private Channel channel;
	private WebSocketFactory webSocketFactory;
	private WebsocketSender websocketSender;
	public static int TIMETODIE = 5400; // 5400 x 2 sekunden im
										// Wartemodus(max. 3 Stunden)
	private FallbackOnError fallbackOnError;
	private QueueType type;

	public QueueConsumer(Channel channel, WebSocketFactory wsfactory,
			FallbackOnError fallbackOnError2, QueueType type) {
		if (type == QueueType.DATA)
			context = new Context(new AmqpDataJsonToRowkey());
		else
			context = new Context(new AmqpEventJsonToRowkey());
		// this(null, wsfactory);
		this.fallbackOnError = fallbackOnError2;
		this.type = type;
		this.webSocketFactory = wsfactory;
		websocketSender = new Sender.WebsocketSender(wsfactory);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) {
		String message = null;
		try {
			message = new String(body, "UTF-8");
			switch (type) {
			case DATA:
				GisaEvermindDATAModel readValue = mapJsonToObject(message);
				int sendedToServer = 0;
				try {
					sendedToServer = sendToServer(readValue, 0);
				} catch (IOException | IllegalStateException e) {
					logger.error("Could not send DATA to the WS-Server. Switch to the waiting mode.  "
							+ e.getLocalizedMessage() + "  " + e);

					if (!waitOnWebsocketServer(sendedToServer, readValue)) {
						logger.error("------------------ During 3 hours the LM-Server for DATA could not be arrived. Process transmitter shut down --------------------");
						fallbackOnError.notifyShutdown(channel, "LM-Server not availible");
					}
				} catch (TransformationException e) {
					logger.error("Error by tranfformation " + e);
				}
				break;
			case LOG:
				GisaEvermindLOGModel readedValue = mapJsonToLOGObject(message);
				try {
					sendLOGToServer(readedValue);
				} catch (IOException | IllegalStateException e) {
					logger.error("Could not send LOG to the WS-Server. Switch to the waiting mode.  "
							+ e.getLocalizedMessage() + "  " + e);

					if (!waitOnWebsocketServer(readedValue)) {
						logger.error("------------------ During 3 hours the LM-Server could not be arrived. Log transmitter shut down --------------------");
						fallbackOnError.notifyShutdown(channel, "LM-Server not availible");
					}
				} catch (TransformationException e) {
					logger.error("Error by transformation " + e);
				}
				break;

			default:
				break;
			}

			confirm(envelope);
		} catch (UnsupportedEncodingException e) {
			logger.error("could not read received mesage "
					+ e.getLocalizedMessage() + "  " + e);
		} catch (IOException e) {
			logger.error("Error on  " + e.getLocalizedMessage() + "  " + e);
		}

	}

	private void checkSended(int sendedToServer, GisaEvermindDATAModel readValue)
			throws IllegalStateException, IOException, TransformationException {
		int size = readValue.getVal().size();
		if (sendedToServer != size) {
			logger.error(sendedToServer + "/" + size
					+ " transmitted to the Server ");
			int resended = sendToServer(readValue, sendedToServer);
			logger.info("retry to send " + resended + "/" + size
					+ " transmitted to the Server ");
		}

	}

	private void confirm(Envelope envelope) throws IOException {
		if (channel != null && channel.isOpen()) {
			// confirm
			channel.basicAck(envelope.getDeliveryTag(), false);
			if (logger.isEnabledFor(Level.DEBUG))
				logger.debug("Message confirm");

		}
	}

	private boolean waitOnWebsocketServer(int sendedToServer,
			GisaEvermindDATAModel readValue) {
		Session newCreaded = null;
		int times = 0;
		while (newCreaded == null && times < TIMETODIE) {
			try {
				times++;
				Thread.sleep(2000);
				newCreaded = webSocketFactory.createNewSession(websocketSender);
				checkSended(sendedToServer, readValue);
				return true;
			} catch (Exception e) {
				logger.error("Reconnect/Resend to websocket Server fail "
						+ e.getLocalizedMessage() + "  " + e);
			}
		}
		return false;

	}

	private boolean waitOnWebsocketServer(GisaEvermindLOGModel readValue) {
		Session newCreaded = null;
		int times = 0;
		while (newCreaded == null && times < TIMETODIE) {
			try {
				times++;
				Thread.sleep(2000);
				newCreaded = webSocketFactory.createNewSession(websocketSender);
				sendLOGToServer(readValue);
				return true;
			} catch (Exception e) {
				logger.error("-->LOG<--  Reconnect/Resend  to websocket Server fail "
						+ e.getLocalizedMessage() + "  " + e);
			}
		}
		return false;

	}

	private int sendToServer(GisaEvermindDATAModel readValue, int offset)
			throws IOException, IllegalStateException, TransformationException {
		int sent = offset;
		for (int i = offset; i < readValue.getVal().size(); i++) {
			String apply = context.executeStrategy(readValue.getVal().get(i), readValue
					.getTs().getTime());
			if (apply != null) {
				logger.info("versendet data: " + apply);
				websocketSender.send(apply);
			}
			sent++;
		}
		return sent;
	}

	private void sendLOGToServer(GisaEvermindLOGModel readValue)
			throws IOException, IllegalStateException, TransformationException {

		String apply = context.executeStrategy(readValue, readValue.getTs().getTime());
		if (apply != null) {
			logger.info("versendet LOG: " + apply);
			websocketSender.send(apply);
		}
	}

	private GisaEvermindDATAModel mapJsonToObject(String message)
			throws IOException, JsonParseException, JsonMappingException {

		GisaEvermindDATAModel readValue = null;
		readValue = mapper.readValue(message, GisaEvermindDATAModel.class);
		return readValue;
	}

	private GisaEvermindLOGModel mapJsonToLOGObject(String message)
			throws IOException, JsonParseException, JsonMappingException {

		GisaEvermindLOGModel readValue = null;
		readValue = mapper.readValue(message, GisaEvermindLOGModel.class);
		return readValue;
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		System.out.println("handle handleShutdownSignal " + consumerTag
				+ "  -::- " + sig.getLocalizedMessage());

	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		System.out.println("handle handleRecoverOk " + consumerTag);
	}

	public void setChannel(Channel channel2) {
		this.channel = channel2;
	}

	public WebsocketSender getWebsocketSender() {
		return websocketSender;
	}

	@Override
	public void handleConsumeOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCancelOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		// TODO Auto-generated method stub

	}
}
