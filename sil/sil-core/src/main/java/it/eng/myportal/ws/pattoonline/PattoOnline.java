/**
 * PattoOnline.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.myportal.ws.pattoonline;

public interface PattoOnline extends javax.xml.rpc.Service {
	public java.lang.String getGestionePattoPortAddress();

	public it.eng.myportal.ws.pattoonline.GestionePattoOnline getGestionePattoPort()
			throws javax.xml.rpc.ServiceException;

	public it.eng.myportal.ws.pattoonline.GestionePattoOnline getGestionePattoPort(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
