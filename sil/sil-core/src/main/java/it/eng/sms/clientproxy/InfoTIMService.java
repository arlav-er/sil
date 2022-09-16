/**
 * InfoTIMService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sms.clientproxy;

public interface InfoTIMService extends javax.xml.rpc.Service {
    public java.lang.String getInfoTIMAddress();

    public it.eng.sms.clientproxy.InfoTIM getInfoTIM() throws javax.xml.rpc.ServiceException;

    public it.eng.sms.clientproxy.InfoTIM getInfoTIM(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
