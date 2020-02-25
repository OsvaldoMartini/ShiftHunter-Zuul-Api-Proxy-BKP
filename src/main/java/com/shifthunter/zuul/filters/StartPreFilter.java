package com.shifthunter.zuul.filters;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

import java.time.Instant;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class StartPreFilter extends ZuulFilter {

	@Override
	public Object run() {
		
		RequestContext ctx = getCurrentContext();
		System.out.println(Instant.now());
		
		// this WILL became Available to the Others Filters
		// Inject New  Param into the context
		ctx.set("starttime", Instant.now());
		
		return null;
	}

	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		
		//RequestContext context = getCurrentContext();
		//return context.getRequest().getParameter("service") != null;
		
		return true;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "pre";
	}
	
}
