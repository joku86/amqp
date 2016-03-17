package de.tiq.solutions.transformation.ws.transformator;

import de.tiq.solutions.gisaconnect.basics.Definitions;
import de.tiq.solutions.gisaconnect.basics.Definitions.TempObject;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel.Val;
import de.tiq.solutions.transformation.ws.TransformationException;
import de.tiq.solutions.transformation.ws.TransformationStrategy;

public class AmqpDataJsonToRowkey implements TransformationStrategy {

	@Override
	public String transformate(Object t, long l) throws TransformationException {
		try {
			String outString = null;
			Val input = (Val) t;
			String serial = input.getKey().split(":")[1];
			String measuValueID = input.getKey().split(":")[2];
			TempObject def = Definitions.getDef(serial, measuValueID);
			if (def != null) {
				if (serial.equals("37564"))
					outString = "{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
							+ "\"deviceType\":\"Photovoltaikanlage\",\"manufactType\":\""
							+ "Photovoltaikanlage"
							+ "\",\"serialNo\":\""
							+ "PVRTG000000001"
							+ "\",\"measureLevel\":\"Photovoltaikanlage\","
							+ "\"measureID\":\""
							+ measuValueID
							+ "\",\"measureValue\":\""
							+ input.getValue()
							+ "\",\"measureTime\":\"" + l + "\"}";
				else
					outString = "{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
							+ "\"deviceType\":\"" + def.getDeviceType() + "\",\"manufactType\":\""
							+ def.getManufType()
							+ "\",\"serialNo\":\""
							+ serial
							+ "\",\"measureLevel\":\"" + def.getMeasureLevel() + "\","
							+ "\"measureID\":\""
							+ measuValueID
							+ "\",\"measureValue\":\""
							+ input.getValue()
							+ "\",\"measureTime\":\"" + l + "\"}";

			} else
				return null;

			return outString;

		} catch (Exception e) {
			throw new TransformationException("Event transformation fail", e);
		}
	}

}
