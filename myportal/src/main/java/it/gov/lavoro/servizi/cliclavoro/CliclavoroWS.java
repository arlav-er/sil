/**
 * CliclavoroWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.cliclavoro;

public interface CliclavoroWS extends java.rmi.Remote {
    public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type invioMessaggio(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioMessaggio_Type parameters) throws java.rmi.RemoteException;
    public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type invioVacancy(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioVacancy_Type parameters) throws java.rmi.RemoteException;
    public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type invioCandidatura(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioCandidatura_Type parameters) throws java.rmi.RemoteException;
}
