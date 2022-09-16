package com.engiweb.framework.test;

import java.net.URL;

import com.engiweb.framework.dispatching.httpchannel.AdapterHTTPProxy;

public class AdapterHTTPClient {
	public AdapterHTTPClient() {
		super();
	} // public AdapterHTTPClient()

	public static void main(String[] args) {
		try {
			AdapterHTTPProxy adapterHTTP = new AdapterHTTPProxy();
			adapterHTTP.setEndPoint(new URL("http://sil.sigma.it/silEngine/sil"));
			String request = "";
			System.out.println("AdapterHTTPClient::main: request\n" + request);
			String response = adapterHTTP.service(request);
			System.out.println("AdapterHTTPClient::main: response\n" + response);
		} // try
		catch (Exception ex) {
			System.out.println("AdapterHTTPClient::main: errore in adapterHTTP.service(request)");
			ex.printStackTrace();
		} // catch (Exception ex) try
	} // public static void main(String[] args)
} // public class AdapterHTTPClient
