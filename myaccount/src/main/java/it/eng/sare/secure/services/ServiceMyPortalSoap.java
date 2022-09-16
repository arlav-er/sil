/**
 * ServiceMyPortalSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sare.secure.services;

public interface ServiceMyPortalSoap extends java.rmi.Remote {

    /**
     * Metodo per registrare l'utente di MyPortal sul SARE
     */
    public java.lang.String registraUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException;

    /**
     * Metodo per aggiornare le informazioni di un utente proveniente
     * da MyPortal
     */
    public java.lang.String aggiornaUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException;

    /**
     * Metodo per abilitare l'utente di MyPortal sul SARE
     */
    public java.lang.String abilitaUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException;

    /**
     * Metodo per modificare la password dell'utente di MyPortal sul
     * SARE
     */
    public java.lang.String modificaPasswordUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException;

    /**
     * Metodo per modificare il tipo utente di MyPortal sul SARE
     */
    public java.lang.String modificaTipoUtente(java.lang.String username, java.lang.String password, java.lang.String xmlUtenteAzienda) throws java.rmi.RemoteException;
}
