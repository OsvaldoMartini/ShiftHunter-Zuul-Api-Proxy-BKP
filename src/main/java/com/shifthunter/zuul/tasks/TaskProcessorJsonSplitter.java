package com.shifthunter.zuul.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.task.launcher.TaskLaunchRequest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(GenDataSource.class) // Bridging between here and the 'BUS' RabbitMQ source class for the stream
public class TaskProcessorJsonSplitter {

//	@Autowired
//	private Source source;
	
	@Autowired
	private GenDataSource genDataSource;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// Publish the Message to the Queue
	public void publishRequest(String payload, String operationName) {

		// maven://[groupid]:[artifactid]:jar:[version]
		// String url =
		// "maven://pluralsight.demo:pluralsight-springcloud-m3-task:jar:0.0.1-SNAPSHOT";

		String javaHome = System.getenv("JAVA_HOME");
		System.out.println("JAVA_HOME " + javaHome);

		String mavenHome = System.getenv("MAVEN_HOME");
		System.out.println("MAVEN_HOME " + mavenHome);

		//String url = "maven://com.shifthunter.tasks:shifthunter-task-json-splitter:jar:0.0.1-SNAPSHOT";
		String url = "maven://ch.corner.tasks:task-splitter-fields-in-file:jar:1.0.0";

		// List<String> input = new
		// ArrayList<String>(Arrays.asList(payload.split(",")));
		String[] arrArgs = payload.split("§");
		Map<String, String> cmdArg = new HashMap<String, String>();

		String encodedString = new String(Base64.encodeBase64(arrArgs[0].getBytes()));
		
		logger.info("EncodedString Length " + encodedString.length());
	
		
		//cmdArg.put("pathSchemaDestiny", arrArgs[1]);
		//cmdArg.put("environment", arrArgs[4]);
		
		
		cmdArg.put("bodyRequest", encodedString);
		cmdArg.put("pathSpecPrefix", arrArgs[1]);
		cmdArg.put("operationName", arrArgs[2]);
		cmdArg.put("isSoap", arrArgs[3]);
		
		System.out.println("input.size" + cmdArg.size());

		List<String> input = new ArrayList<String>(Arrays.asList(payload.split("§")));
		input.clear();
		//input.add("pathSource=Hello");
		TaskLaunchRequest request = new TaskLaunchRequest(url, null, cmdArg, null, operationName);

		System.out.println("created task launch request ...");

		// I am sending this message to the Source -> (RabbitMQ Exchange)
		GenericMessage<TaskLaunchRequest> message = new GenericMessage<TaskLaunchRequest>(request);

		//this.source.output().send(message);
		this.genDataSource.genDataBinario().send(message);
	}

}
