package com.sohungry.search.domain.context.builder;

import javax.servlet.http.HttpServletRequest;

import com.sohungry.search.domain.context.ImmutableApplicationContext;

public class ApplicationContextBuilder {
	
	private HttpServletRequest request;
	
	public ApplicationContextBuilder(HttpServletRequest request) {
		this.request = request;
	}
	
	public ImmutableApplicationContext build() {
		return null;
	}

}
