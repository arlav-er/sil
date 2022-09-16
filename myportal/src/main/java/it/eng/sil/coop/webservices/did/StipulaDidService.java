/**
 * StipulaDidService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.did;

public interface StipulaDidService extends javax.xml.rpc.Service {
    public java.lang.String getStipulaDidAddress();

    public it.eng.sil.coop.webservices.did.StipulaDid getStipulaDid() throws javax.xml.rpc.ServiceException;

    public it.eng.sil.coop.webservices.did.StipulaDid getStipulaDid(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
