package com.shifthunter.zuul.filters;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class MobileFilter extends ZuulFilter {

	@Override
	public Object run() {
		System.out.println("calling a filter Match witch a Criteria in the URL");
		return null;
	}

	@Override
	public boolean shouldFilter() {
		boolean isMobile = false;
		RequestContext ctx = getCurrentContext();
		String param = ctx.getRequest().getParameter("source");
		
		// If Have this Param in the URL and is Mobile
		if(param !=null && param.equals("mobile")) {
			isMobile = true;
		}
		return isMobile;
	}

	@Override
	public int filterOrder() {
		return 2;
	}

	@Override
	public String filterType() {
		return "pre";
	}
}
