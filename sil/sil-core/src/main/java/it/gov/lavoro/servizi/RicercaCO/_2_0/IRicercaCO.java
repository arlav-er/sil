/**
 * IRicercaCO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.RicercaCO._2_0;

public interface IRicercaCO extends java.rmi.Remote {
	public it.gov.lavoro.servizi.RicercaCO._2_0.COPerLavoratoreResponseCOPerLavoratoreResult COPerLavoratore(
			it.gov.lavoro.servizi.RicercaCO._2_0.CodiceFiscaleSoggettoFisico_Type codiceFiscale,
			java.util.Date dataInizio, java.util.Date dataFine) throws java.rmi.RemoteException;
}
