package com.sohungry.search.domain.context;

public abstract class RequestContext {
	
	ApplicationContext appContext;

	public ApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

}
