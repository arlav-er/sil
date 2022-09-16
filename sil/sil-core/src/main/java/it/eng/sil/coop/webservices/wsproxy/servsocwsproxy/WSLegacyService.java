/**
 * WSLegacyService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public interface WSLegacyService extends javax.xml.rpc.Service {
	public java.lang.String getWSLegacyPortAddress();

	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.WSLegacy getWSLegacyPort()
			throws javax.xml.rpc.ServiceException;

	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.WSLegacy getWSLegacyPort(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
