package it.eng.sil.security.handlers;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class MustUnderstand implements SOAPHandler<SOAPMessageContext> {

	@Override
	public void close(MessageContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<QName> getHeaders() {
		final QName securityHeader = new QName(
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security",
				"wsse");

		// ... "understand" the response, very complex logic goes here
		// ... "understand" the response, very complex logic goes here
		// ... "understand" the response, very complex logic goes here

		final HashSet headers = new HashSet();
		headers.add(securityHeader);

		// notify the runtime that this is handled
		return headers;
	}

}
