package de.tiq.solutions.transtormation.transformator;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindDATAModel.Val;
import de.tiq.solutions.transformation.TransformationException;
import de.tiq.solutions.transformation.transformator.AmqpDataJsonToRowkey;
import de.tiq.solutions.transformation.transformator.AmqpEventJsonToRowkey;
import de.tiq.solutions.gisaconnect.receipt.GisaEvermindLOGModel;

public class JsonTransformatorTest {

	@Test
	public void jsonTransformationTest() throws Exception {
		String event = "{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WebBox\",\"module\":\"RasClient\",\"messageCode\":\"13000\",\"messageArgs\":\"*99***1#\",\"message\":\"RAS_Start_Dialing_1\"}";
		String transformate = getEvent(event);
		Assert.assertEquals("PV::PVRTG000000001::Fuchshain Bauabschnitt I::WebBox::Sunny WebBox GSM::0150130436::RAS::Info", transformate);

	}

	@Test
	public void jsonTransformation2Test() throws Exception {
		String event = "{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WebBox\",\"module\":\"RasClient\",\"messageCode\":\"13000\",\"messageArgs\":\"WRTP468C|2110595689\",\"message\":\"RAS_Start_Dialing_1\"}";
		String transformate = getEvent(event);
		Assert.assertEquals("PV::PVRTG000000001::Fuchshain Bauabschnitt I::Wechselrichter::STP 15000TL-10::2110595689::RAS::Info", transformate);
	}

	@Test
	public void jsonTransformation3Test() throws Exception {
		String event = "{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WebBox\",\"module\":\"RasClient\",\"messageCode\":\"13000\",\"messageArgs\":\"WRPP468C|2110595689\",\"message\":\"RAS_Start_Dialing_1\"}";
		String transformate = getEvent(event);
		Assert.assertEquals("PV::PVRTG000000001::Fuchshain Bauabschnitt I::WebBox::Sunny WebBox GSM::0150130436::RAS::Info", transformate);
	}

	@Test
	public void jsonTransformation4Test() throws Exception {
		String event = "{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WRTP4Q39:2110144057\",\"module\":\"WRTP4Q39:2110144057\",\"messageCode\":\"13000\",\"messageArgs\":\"WRPP468C|2110595689\",\"message\":\"RAS_Start_Dialing_1\"}";
		String transformate = getEvent(event);
		Assert.assertEquals("PV::PVRTG000000001::Fuchshain Bauabschnitt I::Wechselrichter::STP 17000TL-10::2110144057::RAS::Info", transformate);
	}

	@Test
	public void jsonTransformation5Test() throws Exception {
		String event = "{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WRTP4Q39:310144057\",\"module\":\"WRTP4Q39:2110144057\",\"messageCode\":\"13000\",\"messageArgs\":\"WRPP468C|2110595689\",\"message\":\"RAS_Start_Dialing_1\"}";
		String transformate = getEvent(event);
		Assert.assertEquals("PV::PVRTG000000001::Fuchshain Bauabschnitt I::WRTP4Q39:310144057::RAS::Info", transformate);
	}

	@Test
	public void jsonTransformation6Test() throws Exception {
		String event = "{\"ts\":\"2016-01-13T15:49:45.000+0000\",\"eventType\":\"Info\",\"accessLevel\":\"USER\",\"category\":\"RAS\",\"device\":\"WRTP4Q39310144057\",\"module\":\"WRTP4Q39:2110144057\",\"messageCode\":\"13000\",\"messageArgs\":\"WRPP468C|2110595689\",\"message\":\"RAS_Start_Dialing_1\"}";
		String transformate = getEvent(event);
		Assert.assertEquals("PV::PVRTG000000001::Fuchshain Bauabschnitt I::WRTP4Q39310144057::RAS::Info", transformate);
	}

	@Test(expected = TransformationException.class)
	public void jsonTransformation7Test() throws JsonParseException, JsonMappingException, IOException, TransformationException {
		AmqpEventJsonToRowkey eventTranstormator = new AmqpEventJsonToRowkey();
		eventTranstormator.transformate(null);
	}

	@Test(expected = TransformationException.class)
	public void jsonTransformation8Test() throws JsonParseException, JsonMappingException, IOException, TransformationException {
		AmqpDataJsonToRowkey eventTranstormator = new AmqpDataJsonToRowkey();
		eventTranstormator.transformate(null);
	}

	@Test
	public void jsonTransformation9Test() throws JsonParseException, JsonMappingException, IOException, TransformationException {
		String data = "{\"ts\":\"2015-10-06T05:42:30.119+0000\",\"val\":[{\"key\":\"WRTP468C211:0595689:A.Ms.Amp\",\"value\":2.419}]}";
		AmqpDataJsonToRowkey dataTranstormator = new AmqpDataJsonToRowkey();
		ObjectMapper mapper = new ObjectMapper();
		GisaEvermindDATAModel readValue = mapper.readValue(data, GisaEvermindDATAModel.class);
		for (Val iterable_element : readValue.getVal()) {
			String transformate = dataTranstormator.transformate(iterable_element);
			Assert.assertEquals("PVRTG000000001::Fuchshain Bauabschnitt I::0595689", transformate);
		}
	}

	private String getEvent(String event) throws IOException, JsonParseException, JsonMappingException, TransformationException {
		AmqpEventJsonToRowkey eventTranstormator = new AmqpEventJsonToRowkey();
		ObjectMapper mapper = new ObjectMapper();
		GisaEvermindLOGModel readValue = mapper.readValue(event, GisaEvermindLOGModel.class);
		String transformate = eventTranstormator.transformate(readValue);
		return transformate;
	}

}
