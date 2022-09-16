/**
 * AdapterWS_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coordinamento.wsClient.np;

import javax.activation.DataSource;

public interface AdapterWS_PortType extends java.rmi.Remote {
	public it.eng.sil.coordinamento.wsClient.np.ExecuteResponse execute(
			it.eng.sil.coordinamento.wsClient.np.Execute parameters, DataSource ds) throws java.rmi.RemoteException;
}
