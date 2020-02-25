package com.shifthunter.zuul.tasks;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface GenDataSource {
	
	@Output("genDataBinarioChannel")
	MessageChannel genDataBinario();
	
//	//	If I Not Provide the name, it will be the Method Name of the this Interface
//	@Output("standardTollChannel")
//	MessageChannel standardToll();
}
