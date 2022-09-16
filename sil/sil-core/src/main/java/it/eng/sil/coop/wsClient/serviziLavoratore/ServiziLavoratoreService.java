/**
 * ServiziLavoratoreService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.serviziLavoratore;

public interface ServiziLavoratoreService extends javax.xml.rpc.Service {
	public java.lang.String getServiziLavoratoreAddress();

	public it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratore getServiziLavoratore()
			throws javax.xml.rpc.ServiceException;

	public it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratore getServiziLavoratore(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
