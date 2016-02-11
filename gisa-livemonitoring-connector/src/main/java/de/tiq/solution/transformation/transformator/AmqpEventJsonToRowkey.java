package de.tiq.solution.transformation.transformator;

import de.tiq.solution.transformation.TransformationException;
import de.tiq.solution.transformation.TransformationStrategy;
import de.tiq.solutions.gisaconnect.basics.Definitions;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindLOGModel;

public class AmqpEventJsonToRowkey implements TransformationStrategy {

	@Override
	public String transformate(Object input, Long t) throws TransformationException {
		try {
			String outString = null;
			GisaEvermindLOGModel log = (GisaEvermindLOGModel) input;
			if (log.getDevice().equals("WebBox")) {
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
			throw new TransformationException("Event transformation fail");
		}
	}
}
