/**
 * SchedaAnagraficaProfessionaleWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public interface SchedaAnagraficaProfessionaleWS extends java.rmi.Remote {
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader[] getListaSap(
			java.lang.String arg0)
			throws java.rmi.RemoteException, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException;

	public it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO getSapUtenteDTO(java.lang.Integer arg0,
			java.lang.String arg1)
			throws java.rmi.RemoteException, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException;
}
