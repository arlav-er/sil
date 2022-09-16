/**
 * AdapterWSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coordinamento.wsClient.np;

public interface AdapterWSService extends javax.xml.rpc.Service {
	public java.lang.String getAdapterWSAddress();

	public it.eng.sil.coordinamento.wsClient.np.AdapterWS_PortType getAdapterWS() throws javax.xml.rpc.ServiceException;

	public it.eng.sil.coordinamento.wsClient.np.AdapterWS_PortType getAdapterWS(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
