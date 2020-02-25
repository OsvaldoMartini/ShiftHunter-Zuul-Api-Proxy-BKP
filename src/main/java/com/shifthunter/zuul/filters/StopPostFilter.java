package com.shifthunter.zuul.filters;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

//Old
//import org.joda.time.Instant

//New Java Time type now
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class StopPostFilter extends ZuulFilter {

	@Override
	public Object run() {
		
		// Another Instant after the FilterType "Pre"
		Instant stop = Instant.now();

		//get start value
		RequestContext ctx = getCurrentContext();
		
		// This it will Yank That Value Off from the context
		Instant start = (Instant)ctx.get("starttime");
		
		long secondsDifference = ChronoUnit.MILLIS.between(start, stop);
		System.out.println("THE Call took >>>>  " + secondsDifference + " milliseconds.");

		
		return null;
	}

	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "post";
	}

}
