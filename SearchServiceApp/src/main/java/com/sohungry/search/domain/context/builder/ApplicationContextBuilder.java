package com.sohungry.search.domain.context.builder;

import javax.servlet.http.HttpServletRequest;

import com.sohungry.search.domain.context.ApplicationContext;

public class ApplicationContextBuilder {
	
	private HttpServletRequest request;
	private int appVersion = 0;
	
	public ApplicationContextBuilder(HttpServletRequest request, int appVersion) {
		this.request = request;
		this.appVersion = appVersion;
	}
	
	public ApplicationContext build() {
		boolean debugMode = "1".equals(request.getHeader("debugMode"));
		String language = request.getLocale().getLanguage();
		if (language == null) {
			language = "zh";
		}
		ApplicationContext appContext = new ApplicationContext();
		appContext.setDebugMode(debugMode);
		appContext.setLanguage(language);
		appContext.setAppVersion(appVersion);
		return appContext;
	}

}
