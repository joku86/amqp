package de.tiq.solution.transformation.transformator;

import de.tiq.solution.transformation.TransformationException;
import de.tiq.solution.transformation.TransformationStrategy;
import de.tiq.solutions.gisaconnect.basics.Definitions;
import de.tiq.solutions.gisaconnect.basics.Definitions.TempObject;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel.Val;

public class AmqpDataJsonToRowkey implements TransformationStrategy {

	@Override
	public String transformate(Object t, Long l) throws TransformationException {
		try {
			String outString = null;
			Val input = (Val) t;
			String serial = input.getKey().split(":")[1];
			String measuValueID = input.getKey().split(":")[2];
			TempObject def = Definitions.getDef(serial, measuValueID);
			if (def != null) {
				if (serial.equals("37564"))
					outString =
							"{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
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
					outString =
							"{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
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
			throw new TransformationException("Event transformation fail");
		}
	}

}
