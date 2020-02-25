package com.shifthunter.zuul;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Joiner;

import com.shifthunter.zuul.tasks.TaskProcessorGenData;
import com.shifthunter.zuul.tasks.TaskProcessorJsonSplitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(TestBeansConfig.class)
public class Tests_Splitter_And_GenBinario {
	private static final Logger logger = LoggerFactory.getLogger(Tests_Splitter_And_GenBinario.class);
	// private static final Logger logger = LoggerFactory.getLogger(Settings.class);

	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	public TaskProcessorGenData taskGenData;

	@Autowired
	public TaskProcessorJsonSplitter taskSplitter;
	
	@Ignore
	@Test
	public void taskGenData_ViaBUS_Test() {
		String columnRequest = Joiner.on(",").skipNulls().join(
				"JsonSchemaNet", "Martini");

		// taskP.publishRequest("D:/Projects/X-DynamicGenerator-SoapUI/DataTypeGen-Files,D:/Projects/X-DynamicGenerator-SoapUI/DataTypeGen-Files,D:/Projects/PST2635-COP1-ConvenienceCardWebSite2020,Swagger,Martini");
		taskGenData.publishRequest(columnRequest, "Martini");

		logger.info("request made");
	}
	
	@Ignore
	@Test
	public void tasSpliterJson_ViaBUS_Test() {
		
		String jsonSample1 = "{\r\n" + 
				"	\"callInfo.clientID\":\"AAA\",\r\n" + 
				"	\"callInfo.userID\":34543,\r\n" + 
				"	\"callInfo.requestID\":12.54\r\n" + 
				"}";


		// Try to Recognize as JSONObject doing Validation
		try {
			JsonNode root = mapper.readTree(jsonSample1);
			jsonSample1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			assert false;
		} // An Empty Object rep in Json

		//String encodedString = new String(Base64.encodeBase64(jsonSample1.getBytes()));

		String columnRequest = Joiner.on("§").skipNulls().join(jsonSample1,
				"JsonSchemaNet", "Martini", "false");

		taskSplitter.publishRequest(columnRequest, "Martini");
		
		logger.info("request made");
		}

//	@Ignore
	@Test
	public void taskProcessorTestBasicSOAP_Case_2() {

		String soapSample1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\r\n" + 
				"                  xmlns:con=\"http://configuration.conveniencecard.service.corner.ch\">\r\n" + 
				"   <soapenv:Body>\r\n" + 
				"      <con:getNapAndCityList>\r\n" + 
				"         <callInfo>\r\n" + 
				"            <timestamp>#TimeStampF1#</timestamp>\r\n" + 
				"         </callInfo>\r\n" + 
				"      </con:getNapAndCityList>\r\n" + 
				"   </soapenv:Body>\r\n" + 
				"</soapenv:Envelope>";

		String jsonSample1 = XMLToJson(soapSample1);

		// Try to Recognize as JSONObject doing Validation
		try {
			JsonNode root = mapper.readTree(jsonSample1);
			jsonSample1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			assert false;
		} // An Empty Object rep in Json
		
		String columnRequest = Joiner.on("§").skipNulls().join(jsonSample1,
				"WSDL", "getNapAndCityList", "true");

		try {
			taskSplitter.publishRequest(columnRequest, "Martini");
			
			logger.info("request made");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assert false;
		}
	}
	
	

	public String XMLToJson(String requestXML) {
		// JSONObject soapDatainJsonObject = XML.toJSONObject(body);
		// logger.info(soapDatainJsonObject);
		XmlMapper xmlMapper = new XmlMapper();
		JsonNode root = null;
		try {
			root = xmlMapper.readTree(requestXML.getBytes());
			String jsonPrettyPrintString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
			logger.info("jsonObject:");
			logger.info(jsonPrettyPrintString);
			
			requestXML = jsonPrettyPrintString;

			// logger.info(String.format("WSDL Converted: %s + " +
			// System.getProperty("line.separator") + " %s", body,
			// converted));
			// body = converted;
		} catch (Exception e) {
			logger.info(String.format(System.getProperty("line.separator") + "Pure Json Request : %s ", requestXML));
		}

		return requestXML;
	}
	
	
	
}