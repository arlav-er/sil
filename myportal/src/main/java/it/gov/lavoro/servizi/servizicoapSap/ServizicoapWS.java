/**
 * ServizicoapWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.servizicoapSap;

public interface ServizicoapWS extends java.rmi.Remote {
    public void invioSAP(java.lang.String SAP, it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder esito, javax.xml.rpc.holders.StringHolder messaggioErrore, javax.xml.rpc.holders.StringHolder codiceSAP) throws java.rmi.RemoteException;
    public java.lang.String richiestaSAP(java.lang.String codiceSAP) throws java.rmi.RemoteException;
    public java.lang.String[] recuperaListaSAPNonAttive(java.lang.String parametri) throws java.rmi.RemoteException;
    public java.lang.String verificaEsistenzaSAP(java.lang.String codiceFiscale) throws java.rmi.RemoteException;
    public void annullaSAP(java.lang.String codiceSAP, it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_annullaSAP_TypeEsitoHolder esito, javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException;
    public void notificaSAP(java.lang.String codiceSAP, java.lang.String motivoNotifica, it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_notificaSAP_TypeEsitoHolder esito, javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException;
}
