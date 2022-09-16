/**
 * ServizioAslWSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.assister;

public interface AssistErWSService extends javax.xml.rpc.Service {
	public java.lang.String getSpilAslWsAddress();

	public it.eng.sil.coop.webservices.asl.SPILASLWSImpl getSpilAslWs() throws javax.xml.rpc.ServiceException;

	public it.eng.sil.coop.webservices.asl.SPILASLWSImpl getSpilAslWs(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
