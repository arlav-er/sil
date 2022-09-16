/**
 * SmashAe4Endpoint.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public interface SmashAe4Endpoint extends java.rmi.Remote {
    public com.tim.smashng.fe.ws.services.AeGetCreditoResiduoResponse getCreditoResiduo(com.tim.smashng.fe.ws.services.AeGetCreditoResiduoRequest arg0) throws java.rmi.RemoteException;
    public com.tim.smashng.fe.ws.services.AeGetInvioResponse getInvio(com.tim.smashng.fe.ws.services.AeGetInvioRequest arg0) throws java.rmi.RemoteException;
    public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggi(com.tim.smashng.fe.ws.services.AeInviaMessaggiRequest arg0) throws java.rmi.RemoteException;
    public com.tim.smashng.fe.ws.services.AeRicercaInviiResponse ricercaInvii(com.tim.smashng.fe.ws.services.AeRicercaInviiRequest arg0) throws java.rmi.RemoteException;
    public com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiResponse ricercaMessaggiRicevuti(com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiRequest arg0) throws java.rmi.RemoteException;
    public com.tim.smashng.fe.ws.services.AePullNotifyResponse pullNotify(com.tim.smashng.fe.ws.services.AePullNotifyRequest arg0) throws java.rmi.RemoteException;
    public com.tim.smashng.fe.ws.services.AePullNotifyMOResponse pullNotifyMO(com.tim.smashng.fe.ws.services.AePullNotifyRequest arg0) throws java.rmi.RemoteException;
    public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggiMittenteTestuale(com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest arg0) throws java.rmi.RemoteException;
    public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggiMittenteNumerico(com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest arg0) throws java.rmi.RemoteException;
    public java.lang.String getVersion() throws java.rmi.RemoteException;
}
