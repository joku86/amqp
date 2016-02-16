package de.tiq.solutions.transformation.transformator;

import de.tiq.solutions.gisaconnect.basics.Definitions;
import de.tiq.solutions.gisaconnect.basics.Definitions.TempObject;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel.Val;
import de.tiq.solutions.transformation.TransformationException;
import de.tiq.solutions.transformation.TransformationStrategy;

public class AmqpDataJsonToRowkey implements TransformationStrategy {
	/**
	 * hier eventuell eine property Datei bauen die vll so was macht
	 * ...=PV..{}...{}
	 */
	@Override
	public String transformate(Object t) throws TransformationException {
		try {
			Val input = (Val) t;
			String serial = input.getKey().split(":")[1];
			String measuValueID = input.getKey().split(":")[2];
			TempObject def = Definitions.getDef(serial, measuValueID);

			String outString =
					"PVRTG000000001::Fuchshain Bauabschnitt I::";
			if (def != null) {
				return outString + def.getDeviceType() + "::" + def.getManufType() + "::" + serial + "::" + def.getMeasureLevel();

			} else {
				return outString + serial;

			}
		} catch (Exception e) {
			throw new TransformationException("Transformation not possible");
		}
	}

}
