package com.engiweb.framework.dispatching.service;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpRequestContextIFace extends RequestContextIFace {
	HttpServletRequest getHttpRequest();

	HttpServletResponse getHttpResponse();

	ServletConfig getServletConfig();

	void freezeHttpResponse();

	void unfreezeHttpResponse();

	boolean isHttpResponseFreezed();
} // public interface HttpRequestContextIFace extends RequestContextIFace
