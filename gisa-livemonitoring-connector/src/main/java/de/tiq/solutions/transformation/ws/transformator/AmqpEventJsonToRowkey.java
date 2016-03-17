package de.tiq.solutions.transformation.ws.transformator;

import de.tiq.solutions.gisaconnect.basics.Definitions;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindLOGModel;
import de.tiq.solutions.transformation.ws.TransformationException;
import de.tiq.solutions.transformation.ws.TransformationStrategy;

public class AmqpEventJsonToRowkey implements TransformationStrategy {

	@Override
	public String transformate(Object input, long t) throws TransformationException {
		try {
			String outString = null;
			GisaEvermindLOGModel log = (GisaEvermindLOGModel) input;
			if (log.getDevice().equals("WebBox")) {
				if (log.getMessageArgs().split("\\|").length > 1) {
					String serial = log.getMessageArgs().split("\\|")[1];
					if (!Definitions.manTypes.containsKey(serial))
						return null;
					String manType = Definitions.getManType(serial);
					outString = "{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
							+ "\"deviceType\":\"Wechselrichter\",\"manufactType\":\""
							+ manType
							+ "\",\"serialNo\":\""
							+ serial
							+ "\",\"measureLevel\":\"Wechselrichter\","
							+ "\"measureID\":\""
							+ "Log"
							+ "\",\"measureValue\":\""
							+ log.getMessageCode() + ".0"
							+ "\",\"measureTime\":\"" + t + "\"}";
				} else
					return null;

			} else {
				String serial = log.getDevice().split(":")[1];
				if (!Definitions.manTypes.containsKey(serial))
					return null;
				String manType = Definitions.getManType(serial);
				outString = "{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
						+ "\"deviceType\":\"Wechselrichter\",\"manufactType\":\""
						+ manType
						+ "\",\"serialNo\":\""
						+ serial
						+ "\",\"measureLevel\":\"Wechselrichter\","
						+ "\"measureID\":\""
						+ "Log"
						+ "\",\"measureValue\":\""
						+ log.getMessageCode() + ".0"
						+ "\",\"measureTime\":\"" + t + "\"}";

			}
			return outString;
		} catch (Exception e) {
			throw new TransformationException("Event transformation fail", e);
		}
	}
}
