package com.engiweb.framework.dispatching.httpchannel;

import java.net.MalformedURLException;
import java.net.URL;

public class AdapterHTTPProxy {
	private URL _url = null;
	private String stringURL = "http://localhost:8080/AFWeb/servlet/AdapterHTTP";

	public AdapterHTTPProxy() {
		super();
	} // public AdapterHTTPProxy()

	public synchronized void setEndPoint(URL url) {
		_url = url;
	} // public synchronized void setEndPoint(URL url)

	public synchronized URL getEndPoint() throws MalformedURLException {
		return getURL();
	} // public synchronized URL getEndPoint() throws MalformedURLException

	private URL getURL() throws MalformedURLException {
		if ((_url == null) && (stringURL != null))
			_url = new URL(stringURL);
		return _url;
	} // private URL getURL() throws MalformedURLException

	public synchronized String service(String request) throws Exception {
		return null;
	} // public synchronized String service(String request) throws Exception
} // public class AdapterHTTPProxy
