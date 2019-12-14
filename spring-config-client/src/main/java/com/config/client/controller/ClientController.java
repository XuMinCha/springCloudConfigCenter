package com.config.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Scope(value="refresh", proxyMode=ScopedProxyMode.TARGET_CLASS)
@RestController
@RequestMapping("/test")
public class ClientController {
	
	@Value("${client.propA}")
	private String propA;
	
	@Value("${client.propB}")
	private String propB;
	
	@Value("${client.propC}")
	private String propC;
	
	@RequestMapping("/getProps")
	public String getProps() {
		StringBuilder sb = new StringBuilder();
		sb.append("propA:").append(propA).append(", ")
		  .append("propB:").append(propB).append(", ")
		  .append("propC:").append(propC);
		return sb.toString();
	}
	
}
