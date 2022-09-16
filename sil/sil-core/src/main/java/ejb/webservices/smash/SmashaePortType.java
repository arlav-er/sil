/**
 * SmashaePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ejb.webservices.smash;

public interface SmashaePortType extends java.rmi.Remote {
    public java.lang.String getVersion() throws java.rmi.RemoteException;
    public bean.webservices.smash.RisultatoArchivioBean ricercaArchivio(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String arg4, java.lang.String arg5, java.lang.String arg6, java.lang.String arg7) throws java.rmi.RemoteException;
    public bean.webservices.smash.PacchettoBean pb() throws java.rmi.RemoteException;
    public bean.webservices.smash.RisultatoInvioBean invioSpot(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String arg4, java.lang.String arg5, java.lang.String arg6, java.lang.String arg7) throws java.rmi.RemoteException;
    public bean.webservices.smash.ArchivioBean ab() throws java.rmi.RemoteException;
    public bean.webservices.smash.RisultatoCreditoBean creditoResiduo(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String arg4, java.lang.String arg5) throws java.rmi.RemoteException;
}
