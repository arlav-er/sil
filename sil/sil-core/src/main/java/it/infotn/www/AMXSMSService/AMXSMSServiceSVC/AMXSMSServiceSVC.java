/**
 * AMXSMSServiceSVC.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.infotn.www.AMXSMSService.AMXSMSServiceSVC;

public interface AMXSMSServiceSVC extends javax.xml.rpc.Service {
    public java.lang.String getAMXSMSServiceSOAPsvcAddress();

    public it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type getAMXSMSServiceSOAPsvc() throws javax.xml.rpc.ServiceException;

    public it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type getAMXSMSServiceSOAPsvc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
