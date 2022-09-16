/**
 * SilSoapServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.siferAccreditamento.request;

public interface SilSoapServicePort extends java.rmi.Remote {

	/**
	 * Check soap service, display name when called
	 */
	public java.lang.Object request(it.eng.sil.coop.webservices.siferAccreditamento.request.Partecipante partecipante,
			it.eng.sil.coop.webservices.siferAccreditamento.request.Patto[] patti) throws java.rmi.RemoteException;
}
