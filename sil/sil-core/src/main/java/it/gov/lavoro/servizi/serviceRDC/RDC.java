/**
 * RDC.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.serviceRDC;

public interface RDC extends java.rmi.Remote {
	public it.gov.lavoro.servizi.serviceRDC.types.Risposta_servizio_RDC_Type ricevi_RDC_by_codiceFiscale(
			it.gov.lavoro.servizi.serviceRDC.types.Richiesta_RDC_beneficiari_dato_CodiceFiscale parameters)
			throws java.rmi.RemoteException;

	public it.gov.lavoro.servizi.serviceRDC.types.Risposta_servizio_RDC_Type ricevi_RDC_by_codProtocolloInps(
			it.gov.lavoro.servizi.serviceRDC.types.Richiesta_RDC_beneficiari_dato_codProtocolloInps parameters)
			throws java.rmi.RemoteException;
}
