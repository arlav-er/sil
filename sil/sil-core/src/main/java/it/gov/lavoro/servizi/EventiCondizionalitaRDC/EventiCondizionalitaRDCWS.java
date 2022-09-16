/**
 * EventiCondizionalitaRDCWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.EventiCondizionalitaRDC;

public interface EventiCondizionalitaRDCWS extends java.rmi.Remote {
	public void loadEventiCondizionalitaRDC(java.lang.String cod_cpi,
			it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento cod_evento, java.lang.String cod_fiscale,
			java.lang.String cod_fiscale_ope, java.lang.String cod_protocollo_inps, java.util.Calendar dtt_domanda,
			java.util.Calendar dtt_evento, java.lang.String txt_note, javax.xml.rpc.holders.StringHolder cod_esito,
			javax.xml.rpc.holders.StringHolder des_esito) throws java.rmi.RemoteException;

	public void deleteEventiCondizionalitaRDC(java.lang.String cod_cpi,
			it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento cod_evento, java.lang.String cod_fiscale,
			java.lang.String cod_fiscale_ope, java.lang.String cod_protocollo_inps, java.util.Date dtt_domanda,
			java.util.Date dtt_evento, javax.xml.rpc.holders.StringHolder cod_esito,
			javax.xml.rpc.holders.StringHolder des_esito) throws java.rmi.RemoteException;
}
