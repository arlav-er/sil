/**
 * GestionePattoOnline.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.myportal.ws.pattoonline;

public interface GestionePattoOnline extends java.rmi.Remote {
	public void invioPatto(it.eng.myportal.ws.pattoonline.PattoType patto, byte[] PDFPatto,
			javax.xml.rpc.holders.StringHolder esito, javax.xml.rpc.holders.StringHolder descrizione)
			throws java.rmi.RemoteException;

	public it.eng.myportal.ws.pattoonline.PattoPortaleType richiestaPatto(
			it.eng.myportal.ws.pattoonline.PattoType patto) throws java.rmi.RemoteException;
}
