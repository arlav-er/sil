/**
 * WSCob.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.islm.siaper.wscob;

public interface WSCob extends javax.xml.rpc.Service {
    public java.lang.String getWSCobSoapAddress();

    public it.islm.siaper.wscob.WSCobSoap getWSCobSoap() throws javax.xml.rpc.ServiceException;

    public it.islm.siaper.wscob.WSCobSoap getWSCobSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
