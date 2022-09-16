/**
 * DMPantaReiSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto.v161;

import javax.activation.DataSource;

public interface DMPantaReiSoap extends java.rmi.Remote {
	public LoginResponse login(Login parameters) throws java.rmi.RemoteException;

	public DeleteResponse delete(Delete parameters) throws java.rmi.RemoteException;

	public ExportResponse export(Export parameters) throws java.rmi.RemoteException;

	public ImportResponse _import(Import parameters, DataSource ds[]) throws java.rmi.RemoteException;

	public GetDocumentsResponse getDocuments(GetDocuments parameters) throws java.rmi.RemoteException;

	public ModifyResponse modify(Modify parameters) throws java.rmi.RemoteException;
}
