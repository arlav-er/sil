/**
 * ServizicoapWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.servizicoap;

public interface ServizicoapWS extends java.rmi.Remote {
    public void invioUtenteYG(java.lang.String utenteYG, it.gov.lavoro.servizi.servizicoap.types.holders.Risposta_invioUtenteYG_TypeEsitoHolder esito, javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException;
    public void checkUtenteYG(java.lang.String codiceFiscale, it.gov.lavoro.servizi.servizicoap.types.holders.Risposta_checkUtenteYG_TypeEsitoHolder esito, javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException;
}
