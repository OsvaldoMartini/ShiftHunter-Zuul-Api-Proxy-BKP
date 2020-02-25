package com.shifthunter.zuul.filters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Joiner;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.shifthunter.zuul.tasks.TaskProcessorGenData;
import com.shifthunter.zuul.tasks.TaskProcessorJsonSplitter;

@RefreshScope
@Component
public class GenericRequestEntityFilter extends ZuulFilter {

	@Value("${pathSpecPrefix}")
	String pathSpecPrefix;

	@Value("${environment}")
	String environment;

	@Autowired
	private TaskProcessorGenData taskGenData;

	@Autowired
	private TaskProcessorJsonSplitter taskJsonSplitter;

	public static int PRETTY_PRINT_INDENT_FACTOR = 4;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final ObjectMapper mapper = new ObjectMapper();

	public String filterType() {
		return "pre";
	}

	public int filterOrder() {
		return 1;
	}

	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		String requestUri = request.getRequestURI();

		logger.info("requestURI: ", requestUri.toString());

		return true; // context.get("proxy").equals(SERVICE_WS2I45);
	}

	private String getBody(final RequestContext context) throws Exception {
		InputStream in = (InputStream) context.get("requestEntity");
		if (in == null) {
			in = context.getRequest().getInputStream();
		}
		return StreamUtils.copyToString(in, Charset.forName("UTF-8"));
	}

	public Object run() {
		try {
			RequestContext context = RequestContext.getCurrentContext();
			if (context.getRequest().getMethod().equals(HttpMethod.OPTIONS.toString())) {
				return null;
			}

			HttpServletRequest request = context.getRequest();

			String methodType = request.getMethod();

			// logger.info(String.format("%s request to %s", request.getMethod(),
			// request.getRequestURL().toString()));
			logger.info(String.format("%s request to %s", request.getMethod(), request.getRequestURI().toString()));

//	        Object accessToken = request.getParameter("accessToken");
//	        if(accessToken == null) {
//	        	logger.warn("access token is empty");
//	        	context.setSendZuulResponse(false);
//	        	context.setResponseBody("401");
//	        	context.setResponseStatusCode(401);
//	            return null;
//	        }
//	        logger.info("access token ok");

			// Split URI
			String[] uriArr = request.getRequestURI().toString().split("/");

			String mainOper = uriArr[uriArr.length - 1];

			String body = getBody(context);
			XmlMapper xmlMapper = new XmlMapper();
			boolean isSoap = false;
			try {
				// It Tries to Convert XML to Json
				xmlMapper.readTree(body.getBytes());

				InputStream in = (InputStream) context.get("requestEntity");
				if (in == null) {
					in = context.getRequest().getInputStream();

					String jsonMoreCleanObject = XMLToJson(body);

					XMLInputStreamToJson(in);

					body = jsonMoreCleanObject;
				}

				isSoap = true;
			} catch (Exception e) {
				logger.info(String.format(System.getProperty("line.separator") + "Pure Json Request : %s ", body));
			}

			// It Resolves any get Parameters
			// TreeMap<String, List<String>> newParameterMap = new TreeMap<>();
			Map<String, String[]> parameterMap = context.getRequest().getParameterMap();
			// getting the current parameter
			JsonNode root = mapper.readTree(body); // An Empty Object rep in Json

			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();
				String[] values = entry.getValue();
				// newParameterMap.put("#QUERY#." + key, Arrays.asList(values));
				((ObjectNode) root).put("#QUERY#." + key, values[0]);

			}

			body = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);

			String resultBlock = "";
			// if (methodType.equalsIgnoreCase("POST")) {

			// resultBlock = splitterFields(body, pathSchemaDestiny, mainOper, isSoap);

			String columnRequest = Joiner.on("§").skipNulls().join(body, pathSpecPrefix, mainOper, isSoap);

			taskJsonSplitter.publishRequest(columnRequest, mainOper);
			logger.info("TaskJsonSplitter  --> Was sended to the \"BUS\"");

//			add a new parameter
//			String authenticatedKey = "authenticated";
//			String authenticatedValue = "true";
//			newParameterMap.put(authenticatedKey, Arrays.asList(authenticatedValue));
//			context.setRequestQueryParams(newParameterMap);
//			return null;
//
//			body = body.replaceFirst("trxTimeStamp", "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");

			logger.info(body);

			context.set("requestEntity", new ByteArrayInputStream(body.getBytes("UTF-8")));

			// if(accessToken == null) {
			logger.warn("Proxy Stops here the Response");
			context.setSendZuulResponse(false);

			ResponseProxy responseProxy = new ResponseProxy();
			responseProxy.status = "SUCCESS";
			responseProxy.msg = "RESPONSE SUCCESS";
			responseProxy.code = "307";

			context.setResponseBody(resultBlock); // Temporary Result Response
			context.setResponseStatusCode(307);

			// Reverse fieldsInLine
			// ReverseTextToJson(resultBlock);

			TimeUnit.SECONDS.sleep(15);
			columnRequest = Joiner.on(",").skipNulls().join(pathSpecPrefix, mainOper);
			taskGenData.publishRequest(columnRequest, mainOper);
			logger.info("TaskGenData  --> Was sended to the \"BUS\"");

			return null;

//			 Google JsonObject
//			JsonParser parser = new JsonParser();
//			JsonObject json = parser.parse(body).getAsJsonObject();
//		    Set<Map.Entry<String, JsonElement>> entries = json.entrySet();//will return members of your object
//		    for (Map.Entry<String, JsonElement> entry: entries) {
//		        logger.info(entry.getKey());
//		    }
//			
//			//json.addProperty("firstname", json.get("firstname").toString().toUpperCase().replace("\"", StringUtils.EMPTY));
//			//json.addProperty("lastname", json.get("lastname").toString().toUpperCase().replace("\"", StringUtils.EMPTY));
//			//json.addProperty("userID", json.get("userID").toString().toUpperCase().replace("\"", StringUtils.EMPTY));
//			//body = body.replaceFirst("trxTimeStamp", "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
//			
//			context.set("requestEntity", new ByteArrayInputStream(json.toString().getBytes("UTF-8")));

		} catch (Exception ex) {
			logger.error("Error", ex);
		}
		return null;
	}

	private String XMLInputStreamToJson(InputStream inputStream) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();

		String inputStr;
		while ((inputStr = bufferedReader.readLine()) != null) {
			responseStrBuilder.append(inputStr);
		}

		JSONObject jsonObject = XML.toJSONObject(responseStrBuilder.toString());
		String jsonPrettyPrintString = jsonObject.toString(PRETTY_PRINT_INDENT_FACTOR);
		logger.info("jsonObject:");
		logger.info(jsonPrettyPrintString);

		return jsonPrettyPrintString;
	}

	public String XMLToJson(String requestXML) {
		XmlMapper xmlMapper = new XmlMapper();
		JsonNode root = null;
		try {
			root = xmlMapper.readTree(requestXML.getBytes());
			String jsonPrettyPrintString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
			logger.info("More Clean Conversiont of with \"XmlMapper\" to JsonObject:");
			logger.info(jsonPrettyPrintString);
			requestXML = jsonPrettyPrintString;
		} catch (Exception e) {
			logger.info(String.format(System.getProperty("line.separator") + "Pure Json Request : %s ", requestXML));
		}
		return requestXML;
	}

}

class BodyRequest {
	public String prefix;
	public String mobilePhoneNumber;
	public String confirmPrefix;
	public String confirmMobilePhoneNumber;
	public String serialNumber;
	public String lastFourDigits;
	public String partnerId;
	public String language;
}

class ResponseSuccess {
	public String status;
	public String message;
	public String code;
	public String giftCardFlag;
	public String partnerId;
	public String agreementId;
	public String balance;
	public String currency;
	public String msisdb;
}

class ResponseError {
	public String status;
	public String code;
	public String msg;
	public String[] errors;
}

class ResponseProxy {
	public String status;
	public String code;
	public String msg;
	public String[] fields;
}
