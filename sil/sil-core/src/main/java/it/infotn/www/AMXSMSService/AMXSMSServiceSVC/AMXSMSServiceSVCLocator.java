/**
 * AMXSMSServiceSVCLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.infotn.www.AMXSMSService.AMXSMSServiceSVC;

public class AMXSMSServiceSVCLocator extends org.apache.axis.client.Service implements it.infotn.www.AMXSMSService.AMXSMSServiceSVC.AMXSMSServiceSVC {

    public AMXSMSServiceSVCLocator() {
    }


    public AMXSMSServiceSVCLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AMXSMSServiceSVCLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AMXSMSServiceSOAPsvc
    private java.lang.String AMXSMSServiceSOAPsvc_address = "";

    public java.lang.String getAMXSMSServiceSOAPsvcAddress() {
        return AMXSMSServiceSOAPsvc_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AMXSMSServiceSOAPsvcWSDDServiceName = "AMXSMSServiceSOAPsvc";

    public java.lang.String getAMXSMSServiceSOAPsvcWSDDServiceName() {
        return AMXSMSServiceSOAPsvcWSDDServiceName;
    }

    public void setAMXSMSServiceSOAPsvcWSDDServiceName(java.lang.String name) {
        AMXSMSServiceSOAPsvcWSDDServiceName = name;
    }

    public it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type getAMXSMSServiceSOAPsvc() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AMXSMSServiceSOAPsvc_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAMXSMSServiceSOAPsvc(endpoint);
    }

    public it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type getAMXSMSServiceSOAPsvc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.infotn.www.AMXSMSService.AMXSMSServiceSVC.AMXSMSServiceSOAPsvcStub _stub = new it.infotn.www.AMXSMSService.AMXSMSServiceSVC.AMXSMSServiceSOAPsvcStub(portAddress, this);
            _stub.setPortName(getAMXSMSServiceSOAPsvcWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAMXSMSServiceSOAPsvcEndpointAddress(java.lang.String address) {
        AMXSMSServiceSOAPsvc_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type.class.isAssignableFrom(serviceEndpointInterface)) {
                it.infotn.www.AMXSMSService.AMXSMSServiceSVC.AMXSMSServiceSOAPsvcStub _stub = new it.infotn.www.AMXSMSService.AMXSMSServiceSVC.AMXSMSServiceSOAPsvcStub(new java.net.URL(AMXSMSServiceSOAPsvc_address), this);
                _stub.setPortName(getAMXSMSServiceSOAPsvcWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AMXSMSServiceSOAPsvc".equals(inputPortName)) {
            return getAMXSMSServiceSOAPsvc();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.infotn.it/AMXSMSService/AMXSMSServiceSVC", "AMXSMSServiceSVC");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.infotn.it/AMXSMSService/AMXSMSServiceSVC", "AMXSMSServiceSOAPsvc"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AMXSMSServiceSOAPsvc".equals(portName)) {
            setAMXSMSServiceSOAPsvcEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
