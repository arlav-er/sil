package com.engiweb.framework.dispatching.service;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpServiceIFace {
	HttpServletRequest getHttpRequest();

	// TOWSAD
	// void setHttpRequest(HttpServletRequest httpRequest);
	HttpServletResponse getHttpResponse();

	// TOWSAD
	// void setHttpResponse(HttpServletResponse httpSession);
	ServletConfig getServletConfig();

	// TOWSAD
	// void setServletConfig(ServletConfig servletConfig);
	void freezeHttpResponse();

	void unfreezeHttpResponse();

	boolean isHttpResponseFreezed();
} // public interface HttpServiceIFace
