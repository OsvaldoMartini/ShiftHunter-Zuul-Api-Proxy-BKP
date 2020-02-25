package com.shifthunter.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.shifthunter.zuul.filters.GenericRequestEntityFilter;
import com.shifthunter.zuul.filters.MobileFilter;
import com.shifthunter.zuul.filters.StartPreFilter;
import com.shifthunter.zuul.filters.StopPostFilter;

@EnableZuulProxy
@SpringBootApplication
public class ZuulApiProxyApp {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApiProxyApp.class, args);
	}
	
	@Bean
	public MobileFilter proxyFilter() {
		return new MobileFilter();
	}
	
	@Bean
	public StartPreFilter startPreFilter() {
		return new StartPreFilter();
	}
	
	@Bean
	public StopPostFilter stopPostFilter() {
		return new StopPostFilter();
	}
	
	@Bean
	public GenericRequestEntityFilter genericRequestEntityFilter() {
		return new GenericRequestEntityFilter();
	}	
	
	
}
