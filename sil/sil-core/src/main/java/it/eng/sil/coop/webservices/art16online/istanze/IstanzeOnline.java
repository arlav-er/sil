/**
 * IstanzeOnline.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public interface IstanzeOnline extends javax.xml.rpc.Service {
	public java.lang.String getGetIstanzeAddress();

	public it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnline getGetIstanze()
			throws javax.xml.rpc.ServiceException;

	public it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnline getGetIstanze(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
