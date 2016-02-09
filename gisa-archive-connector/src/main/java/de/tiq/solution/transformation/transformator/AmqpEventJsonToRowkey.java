package de.tiq.solution.transformation.transformator;

import de.tiq.solution.transformation.TransformationStrategy;
import de.tiq.solutions.gisaconnect.basics.Definitions;
import de.tiq.solutions.gisaconnect.basics.Definitions.TempObject;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindLOGModel;

public class AmqpEventJsonToRowkey implements TransformationStrategy {

	@Override
	public String transformate(Object input) {

		GisaEvermindLOGModel log = (GisaEvermindLOGModel) input;
		String outString =
				"PV::PVRTG000000001::Fuchshain Bauabschnitt I::";
		String serial = null;
		if (log.getDevice().equals("WebBox")) {
			if (!log.getMessageArgs().contains("|")) {
				outString = outString + log.getDevice() + "::Sunny WebBox GSM::0150130436::" + log.getCategory() + "::" + log.getEventType();

			} else {
				serial = log.getMessageArgs().split("\\|")[1];
				if (log.getMessageArgs().startsWith("WRTP"))
					outString = outString + "Wechselrichter" + "::" + Definitions.getManType(serial) + "::" + serial + "::" + log.getCategory()
							+ "::"
							+ log.getEventType();
				else
					outString = outString + log.getDevice() + "::Sunny WebBox GSM::0150130436::" + log.getCategory() + "::" + log.getEventType();

			}

		} else {
			if (log.getDevice().contains(":")) {
				serial = log.getDevice().split(":")[1];
				TempObject def = Definitions.getDef(serial, "Event");
				if (def != null)
					outString = outString + "Wechselrichter::" + def.getManufType() + "::" + serial + "::" + log.getCategory() + "::"
							+ log.getEventType();
				else {
					outString = outString + log.getDevice() + "::" + log.getCategory() + "::" + log.getEventType();
				}
			} else {
				outString = outString + log.getDevice() + "::" + log.getCategory() + "::" + log.getEventType();
			}
		}
		return outString;
	}

}
