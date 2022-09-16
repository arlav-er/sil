/**
 * ServizicoapWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.coapadesioneGet.ministero;

public interface ServizicoapWS extends java.rmi.Remote {
	public void getStatoAdesioneYG(java.lang.String datiStatoAdesione,
			it.eng.sil.coop.webservices.coapadesioneGet.ministero.types.holders.Risposta_getStatoAdesioneYG_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore, org.apache.axis.holders.DateHolder dataAdesione,
			javax.xml.rpc.holders.StringHolder statoAdesione, javax.xml.rpc.holders.CalendarHolder dataStatoAdesione)
			throws java.rmi.RemoteException;
}
