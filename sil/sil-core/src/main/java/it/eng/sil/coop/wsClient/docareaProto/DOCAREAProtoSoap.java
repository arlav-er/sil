/**
 * DOCAREAProtoSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto;

import javax.activation.DataSource;

public interface DOCAREAProtoSoap extends java.rmi.Remote {
	public it.eng.sil.coop.wsClient.docareaProto.LoginResponse login(
			it.eng.sil.coop.wsClient.docareaProto.Login parameters) throws java.rmi.RemoteException;

	public it.eng.sil.coop.wsClient.docareaProto.InserimentoResponse inserimento(
			it.eng.sil.coop.wsClient.docareaProto.Inserimento parameters, DataSource ds)
			throws java.rmi.RemoteException;

	public it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneResponse protocollazione(
			it.eng.sil.coop.wsClient.docareaProto.Protocollazione parameters, DataSource ds)
			throws java.rmi.RemoteException;
}
