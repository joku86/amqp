package de.tiq.solutions.transformation;
//package de.tiq.solution.transformation;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel.Val;
//import de.tiq.solutions.gisaconnect.receipt.GisaEvermindLOGModel;
//
//public class FromToConverter {
//
//	public String apply(Object t, Long l) throws TransformationException {
//		String outString = null;
//		try {
//			if (t instanceof Val) {
//				Val input = (Val) t;
//				String serial = input.getKey().split(":")[1];
//				String measuValueID = input.getKey().split(":")[2];
//				TempObject def = getDef(serial, measuValueID);
//				if (def != null) {
//					if (serial.equals("37564"))
//
//						outString =
//								"{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
//										+ "\"deviceType\":\"Photovoltaikanlage\",\"manufactType\":\""
//										+ "Photovoltaikanlage"
//										+ "\",\"serialNo\":\""
//										+ "PVRTG000000001"
//										+ "\",\"measureLevel\":\"Photovoltaikanlage\","
//										+ "\"measureID\":\""
//										+ measuValueID
//										+ "\",\"measureValue\":\""
//										+ input.getValue()
//										+ "\",\"measureTime\":\"" + l + "\"}";
//					else
//						outString =
//								"{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
//										+ "\"deviceType\":\"" + def.getDeviceType() + "\",\"manufactType\":\""
//										+ def.getManufType()
//										+ "\",\"serialNo\":\""
//										+ serial
//										+ "\",\"measureLevel\":\"" + def.getMeasureLevel() + "\","
//										+ "\"measureID\":\""
//										+ measuValueID
//										+ "\",\"measureValue\":\""
//										+ input.getValue()
//										+ "\",\"measureTime\":\"" + l + "\"}";
//
//				} else
//					return null;
//
//			} else {
//				if (t instanceof GisaEvermindLOGModel) {
//					GisaEvermindLOGModel log = (GisaEvermindLOGModel) t;
//					if (log.getDevice().equals("WebBox")) {
//						String serial = log.getMessageArgs().split("\\|")[1];
//						if (!manTypes.containsKey(serial))
//							return null;
//						String manType = getManType(serial);
//						outString = "{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
//								+ "\"deviceType\":\"Wechselrichter\",\"manufactType\":\""
//								+ manType
//								+ "\",\"serialNo\":\""
//								+ serial
//								+ "\",\"measureLevel\":\"Wechselrichter\","
//								+ "\"measureID\":\""
//								+ "Log"
//								+ "\",\"measureValue\":\""
//								+ log.getMessageCode() + ".0"
//								+ "\",\"measureTime\":\"" + l + "\"}";
//
//					} else {
//						String serial = log.getDevice().split(":")[1];
//						if (!manTypes.containsKey(serial))
//							return null;
//						String manType = getManType(serial);
//						outString = "{\"plantTypeCode\":\"PV\",\"plantUUID\":\"PVRTG000000001\",\"plantName\":\"Fuchshain Bauabschnitt I\","
//								+ "\"deviceType\":\"Wechselrichter\",\"manufactType\":\""
//								+ manType
//								+ "\",\"serialNo\":\""
//								+ serial
//								+ "\",\"measureLevel\":\"Wechselrichter\","
//								+ "\"measureID\":\""
//								+ "Log"
//								+ "\",\"measureValue\":\""
//								+ log.getMessageCode() + ".0"
//								+ "\",\"measureTime\":\"" + l + "\"}";
//					}
//
//				}
//			}
//
//		} catch (Exception e) {
//			throw new TransformationException("Transformation fail");
//		}
//
//		return outString;
//	}
//
//	public String getRowKeyForData(Val input) throws TransformationException {
//		String serial = input.getKey().split(":")[1];
//		String measuValueID = input.getKey().split(":")[2];
//		TempObject def = getDef(serial, measuValueID);
//
//		String outString =
//				"PVRTG000000001::Fuchshain Bauabschnitt I::";
//		if (def != null) {
//			return outString + def.getDeviceType() + "::" + def.getManufType() + "::" + serial + "::" + def.getMeasureLevel();
//
//		} else {
//			return outString + serial;
//			// throw new TransformationException("No Rowkey for " +
//			// input.getKey() + "  " + input.getValue() + "  build");
//		}
//
//	}
//
//	// Bsp1
//	// PV PVRTG000000001 Fuchshain Wechselrichter STP 15000TL-10 2110595689
//	// DEVMON Info 16001 ALARMING_LogEntry
//
//	public String getRowKeyForData(GisaEvermindLOGModel log) {
//		String outString =
//				"PV::PVRTG000000001::Fuchshain Bauabschnitt I::";
//		String serial = null;
//		if (log.getDevice().equals("WebBox")) {
//			if (!log.getMessageArgs().contains("|")) {
//				outString = outString + log.getDevice() + "::Sunny WebBox GSM::0150130436::" + log.getCategory() + "::" + log.getEventType();
//
//			} else {
//				serial = log.getMessageArgs().split("\\|")[1];
//				if (log.getMessageArgs().startsWith("WRTP"))
//					outString = outString + "Wechselrichter" + "::" + getManType(serial) + "::" + serial + "::" + log.getCategory() + "::"
//							+ log.getEventType();
//				else
//					outString = outString + log.getDevice() + "::Sunny WebBox GSM::0150130436::" + log.getCategory() + "::" + log.getEventType();
//
//			}
//
//		} else {
//			if (log.getDevice().contains(":")) {
//				serial = log.getDevice().split(":")[1];
//				TempObject def = getDef(serial, "Event");
//				if (def != null)
//					outString = outString + "Wechselrichter::" + def.getManufType() + "::" + serial + "::" + log.getCategory() + "::"
//							+ log.getEventType();
//				else {
//					outString = outString + log.getDevice() + "::" + log.getCategory() + "::" + log.getEventType();
//				}
//			} else {
//				outString = outString + log.getDevice() + "::" + log.getCategory() + "::" + log.getEventType();
//			}
//		}
//		return outString;
//
//	}
//
//	private String getManType(String serial) {
//		String manufType = null;
//		switch (serial) {
//		case "2110144057":
//			manufType = "STP 17000TL-10";
//			break;
//		case "2110595689":
//			manufType = "STP 15000TL-10";
//			break;
//		case "2110319822":
//			manufType = "STP 15000TL-10";
//			break;
//		case "32069":
//			manufType = "Sunny SensorBox";
//			break;
//		case "37564":
//			manufType = "Sunny SensorBox";
//			break;
//		case "0150130436":
//			manufType = "Sunny WebBox GSM";
//			break;
//		case "":
//			manufType = "SPP 235";
//		}
//		return manufType;
//	}
//
//	public static class TempObject {
//		private String deviceType;
//		private String manufType;
//		private String measureLevel;
//
//		public TempObject(String deviceType, String manufType, String measureLevel) {
//			this.deviceType = deviceType;
//			this.manufType = manufType;
//			this.measureLevel = measureLevel;
//		}
//
//		public String getDeviceType() {
//			return deviceType;
//		}
//
//		public void setDeviceType(String deviceType) {
//			this.deviceType = deviceType;
//		}
//
//		public String getManufType() {
//			return manufType;
//		}
//
//		public void setManufType(String manufType) {
//			this.manufType = manufType;
//		}
//
//		public String getMeasureLevel() {
//			return measureLevel;
//		}
//
//		public void setMeasureLevel(String measureLevel) {
//			this.measureLevel = measureLevel;
//		}
//
//	}
//
//	public TempObject getDef(String serialNo, String measureValueId) {
//		Map<String, TempObject> content = manTypes.get(serialNo);
//		if (content != null) {
//			TempObject tempObject = content.get(measureValueId);
//			if (tempObject != null)
//				return tempObject;
//
//		}
//		return null;
//
//	}
//
//	private Map<String, Map<String, TempObject>> manTypes = new HashMap<String, Map<String, TempObject>>() {
//		private static final long serialVersionUID = 1L;
//		{
//			Map<String, TempObject> measuredVals = new HashMap<String, TempObject>();
//			measuredVals.put("Event", new TempObject("Wetterstation", "SensorBox", "Einstrahlungssensor"));
//			measuredVals.put("IntSolIrr", new TempObject("Wetterstation", "SensorBox", "Einstrahlungssensor"));
//			measuredVals.put("TmpAmb C", new TempObject("Wetterstation", "SensorBox", "Umgebungstemperatursensor"));
//			measuredVals.put("ExlSolIrr", new TempObject("Wetterstation", "SensorBox", "Einstrahlungssensor"));
//			measuredVals.put("WindVel m/s", new TempObject("Wetterstation", "SensorBox", "Umgebungstemperatursensor"));
//			measuredVals.put("SMA-h-On", new TempObject("Wetterstation", "SensorBox", "Wetterstation"));
//			measuredVals.put("TmpMdul C", new TempObject("Wetterstation", "SensorBox", "Modultemperatursensor"));
//			measuredVals.put("TmpMdul F", new TempObject("Wetterstation", "SensorBox", "Modultemperatursensor"));
//			measuredVals.put("TmpMdul K", new TempObject("Wetterstation", "SensorBox", "Modultemperatursensor"));
//			measuredVals.put("TmpAmb F", new TempObject("Wetterstation", "SensorBox", "Umgebungstemperatursensor"));
//			measuredVals.put("TmpAmb K", new TempObject("Wetterstation", "SensorBox", "Umgebungstemperatursensor"));
//			measuredVals.put("WindVel km/h", new TempObject("Wetterstation", "SensorBox", "Umgebungstemperatursensor"));
//
//			put("37564", measuredVals);
//			put("32069", measuredVals);
//			// ----17
//			measuredVals = new HashMap<String, TempObject>();
//			TempObject wechselrichter17 = new TempObject("Wechselrichter", "STP 17000TL-10", "Wechselrichter");
//			measuredVals.put("Event", wechselrichter17);
//			measuredVals.put("Pac", wechselrichter17);
//			measuredVals.put("E-Total", wechselrichter17);
//			measuredVals.put("Serial Number", wechselrichter17);
//			measuredVals.put("Riso", wechselrichter17);
//			measuredVals.put("PlntCtl.Stt", wechselrichter17);
//			measuredVals.put("Op.TmsRmg", wechselrichter17);
//			measuredVals.put("Op.GriSwStt", wechselrichter17);
//			measuredVals.put("Op.GriSwCnt", wechselrichter17);
//			measuredVals.put("Op.EvtNo", wechselrichter17);
//			measuredVals.put("Op.EvtCntUsr", wechselrichter17);
//			measuredVals.put("Op.EvtCntIstl", wechselrichter17);
//			measuredVals.put("Mt.TotTmh", wechselrichter17);
//			measuredVals.put("Mt.TotOpTmh", wechselrichter17);
//			measuredVals.put("Mode", wechselrichter17);
//			measuredVals.put("Iso.FltA", wechselrichter17);
//			measuredVals.put("InvCtl.Stt", wechselrichter17);
//			measuredVals.put("Inv.TmpLimStt", wechselrichter17);
//			measuredVals.put("GridMs.W.phsC", wechselrichter17);
//			measuredVals.put("GridMs.W.phsB", wechselrichter17);
//			measuredVals.put("GridMs.W.phsA", wechselrichter17);
//			measuredVals.put("GridMs.TotPFPrc", wechselrichter17);
//			measuredVals.put("GridMs.PhV.phsC", wechselrichter17);
//			measuredVals.put("GridMs.PhV.phsB", wechselrichter17);
//			measuredVals.put("GridMs.PhV.phsA", wechselrichter17);
//			measuredVals.put("GridMs.Hz", wechselrichter17);
//			measuredVals.put("GridMs.A.phsC", wechselrichter17);
//			measuredVals.put("GridMs.A.phsB", wechselrichter17);
//			measuredVals.put("GridMs.A.phsA", wechselrichter17);
//			measuredVals.put("Error", wechselrichter17);
//			measuredVals.put("E.Invert_Wh", wechselrichter17);
//			measuredVals.put("DC.PmaxTot.W", wechselrichter17);
//			TempObject strang17 = new TempObject("Wechselrichter", "STP 17000TL-10", "Strang");
//			measuredVals.put("B1.Ms.Amp", strang17);
//			measuredVals.put("A5.Ms.Amp", strang17);
//			measuredVals.put("A4.Ms.Amp", strang17);
//			measuredVals.put("A3.Ms.Amp", strang17);
//			measuredVals.put("A2.Ms.Amp", strang17);
//			measuredVals.put("A1.Ms.Amp", strang17);
//			TempObject leistungsteil17 = new TempObject("Wechselrichter", "STP 17000TL-10", "Leistungsteil");
//			measuredVals.put("PacPU2", leistungsteil17);
//			measuredVals.put("PacPU1", leistungsteil17);
//			measuredVals.put("B.Ms.Watt", leistungsteil17);
//			measuredVals.put("B.Ms.Vol", leistungsteil17);
//			measuredVals.put("B.Ms.Amp", leistungsteil17);
//			measuredVals.put("A.Ms.Watt", leistungsteil17);
//			measuredVals.put("A.Ms.Vol", leistungsteil17);
//			measuredVals.put("A.Ms.Amp", leistungsteil17);
//			put("2110144057", measuredVals);
//
//			// ----15
//			measuredVals = new HashMap<String, TempObject>();
//			TempObject wechselrichter15 = new TempObject("Wechselrichter", "STP 15000TL-10", "Wechselrichter");
//			measuredVals.put("Event", wechselrichter15);
//			measuredVals.put("Pac", wechselrichter15);
//			measuredVals.put("E-Total", wechselrichter15);
//			measuredVals.put("Serial Number", wechselrichter15);
//			measuredVals.put("Riso", wechselrichter15);
//			measuredVals.put("PlntCtl.Stt", wechselrichter15);
//			measuredVals.put("Op.TmsRmg", wechselrichter15);
//			measuredVals.put("Op.GriSwStt", wechselrichter15);
//			measuredVals.put("Op.GriSwCnt", wechselrichter15);
//			measuredVals.put("Op.EvtNo", wechselrichter15);
//			measuredVals.put("Op.EvtCntUsr", wechselrichter15);
//			measuredVals.put("Op.EvtCntIstl", wechselrichter15);
//			measuredVals.put("Mt.TotTmh", wechselrichter15);
//			measuredVals.put("Mt.TotOpTmh", wechselrichter15);
//			measuredVals.put("Mode", wechselrichter15);
//			measuredVals.put("Iso.FltA", wechselrichter15);
//			measuredVals.put("InvCtl.Stt", wechselrichter15);
//			measuredVals.put("Inv.TmpLimStt", wechselrichter15);
//			measuredVals.put("GridMs.W.phsC", wechselrichter15);
//			measuredVals.put("GridMs.W.phsB", wechselrichter15);
//			measuredVals.put("GridMs.W.phsA", wechselrichter15);
//			// measuredVals.put("GridMs.TotPFPrc", wechselrichter17);
//			measuredVals.put("GridMs.PhV.phsC", wechselrichter15);
//			measuredVals.put("GridMs.PhV.phsB", wechselrichter15);
//			measuredVals.put("GridMs.PhV.phsA", wechselrichter15);
//			measuredVals.put("GridMs.Hz", wechselrichter15);
//			measuredVals.put("GridMs.A.phsC", wechselrichter15);
//			measuredVals.put("GridMs.A.phsB", wechselrichter15);
//			measuredVals.put("GridMs.A.phsA", wechselrichter15);
//			measuredVals.put("Error", wechselrichter15);
//			measuredVals.put("E.Invert_Wh", wechselrichter15);
//			measuredVals.put("DC.PmaxTot.W", wechselrichter15);
//			TempObject strang15 = new TempObject("Wechselrichter", "STP 15000TL-10", "Strang");
//			measuredVals.put("B1.Ms.Amp", strang15);
//			measuredVals.put("A5.Ms.Amp", strang15);
//			measuredVals.put("A4.Ms.Amp", strang15);
//			measuredVals.put("A3.Ms.Amp", strang15);
//			measuredVals.put("A2.Ms.Amp", strang15);
//			measuredVals.put("A1.Ms.Amp", strang15);
//			TempObject leistungsteil15 = new TempObject("Wechselrichter", "STP 15000TL-10", "Leistungsteil");
//			measuredVals.put("PacPU2_W", leistungsteil15);
//			measuredVals.put("PacPU1_W", leistungsteil15);
//			measuredVals.put("B.Ms.Watt", leistungsteil15);
//			measuredVals.put("B.Ms.Vol", leistungsteil15);
//			measuredVals.put("B.Ms.Amp", leistungsteil15);
//			measuredVals.put("A.Ms.Watt", leistungsteil15);
//			measuredVals.put("A.Ms.Vol", leistungsteil15);
//			measuredVals.put("A.Ms.Amp", leistungsteil15);
//			put("2110595689", measuredVals);
//
//		}
//
//	};
// }
