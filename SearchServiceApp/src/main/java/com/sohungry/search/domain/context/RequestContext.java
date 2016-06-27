package com.sohungry.search.domain.context;

public abstract class RequestContext {
	
	ImmutableApplicationContext appContext;

	public ImmutableApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(ImmutableApplicationContext appContext) {
		this.appContext = appContext;
	}

}
