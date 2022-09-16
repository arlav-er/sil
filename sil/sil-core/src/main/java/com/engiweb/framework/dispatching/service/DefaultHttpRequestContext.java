package com.engiweb.framework.dispatching.service;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;

public class DefaultHttpRequestContext extends DefaultRequestContext implements HttpRequestContextIFace {
	private HttpServletRequest _httpRequest = null;
	private HttpServletResponse _httpResponse = null;
	private ServletConfig _servletConfig = null;
	private boolean _httpResponseFreezed = false;

	public DefaultHttpRequestContext() {
		super();
		_httpRequest = null;
		_httpResponse = null;
		_servletConfig = null;
		_httpResponseFreezed = false;
	} // public DefaultHttpRequestContext()

	public DefaultHttpRequestContext(RequestContainer requestContainer, ResponseContainer responseContainer) {
		super(requestContainer, responseContainer);
		_httpRequest = (HttpServletRequest) requestContainer.getAttribute(Constants.HTTP_REQUEST);
		_httpResponse = (HttpServletResponse) requestContainer.getAttribute(Constants.HTTP_RESPONSE);
		_servletConfig = (ServletConfig) requestContainer.getAttribute(Constants.SERVLET_CONFIG);
		_httpResponseFreezed = false;
	} // public DefaultHttpRequestContext(RequestContainer requestContainer,

	// ResponseContainer responseContainer)
	public void setRequestContext(RequestContextIFace requestContext) {
		super.setRequestContext(requestContext);
		if (requestContext == null) {
			_httpRequest = null;
			_httpResponse = null;
			_servletConfig = null;
		} // if (requestContext == null)
		else {
			_httpRequest = (HttpServletRequest) requestContext.getRequestContainer()
					.getAttribute(Constants.HTTP_REQUEST);
			_httpResponse = (HttpServletResponse) requestContext.getRequestContainer()
					.getAttribute(Constants.HTTP_RESPONSE);
			_servletConfig = (ServletConfig) requestContext.getRequestContainer()
					.getAttribute(Constants.SERVLET_CONFIG);
		} // if (requestContext == null) else
	} // public void setRequestContext(RequestContextIFace requestContext)

	public HttpServletRequest getHttpRequest() {
		return _httpRequest;
	} // public HttpServletRequest getHttpRequest()

	public HttpServletResponse getHttpResponse() {
		return _httpResponse;
	} // public HttpServletResponse getHttpResponse()

	public ServletConfig getServletConfig() {
		return _servletConfig;
	} // public ServletConfig getServletConfig()

	public void freezeHttpResponse() {
		_httpResponseFreezed = true;
		getResponseContainer().setAttribute(Constants.HTTP_RESPONSE_FREEZED, new Boolean(_httpResponseFreezed));
	} // public void freezeHttpResponse

	public void unfreezeHttpResponse() {
		_httpResponseFreezed = false;
		getResponseContainer().setAttribute(Constants.HTTP_RESPONSE_FREEZED, new Boolean(_httpResponseFreezed));
	} // public void unfreezeHttpResponse

	public boolean isHttpResponseFreezed() {
		return _httpResponseFreezed;
	} // public boolean isHttpResponseFreezed()
} // public class DefaultHttpRequestContext extends DefaultRequestContext
	// implements HttpRequestContextIFace
