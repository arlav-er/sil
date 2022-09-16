/**
 * ConferimentoDID_BindingQSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0;

public class ConferimentoDID_BindingQSServiceLocator extends org.apache.axis.client.Service implements it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingQSService {

/**
 * OSB Service
 */

    public ConferimentoDID_BindingQSServiceLocator() {
    }


    public ConferimentoDID_BindingQSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ConferimentoDID_BindingQSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ConferimentoDID_BindingQSPort
    private java.lang.String ConferimentoDID_BindingQSPort_address = "";

    public java.lang.String getConferimentoDID_BindingQSPortAddress() {
        return ConferimentoDID_BindingQSPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ConferimentoDID_BindingQSPortWSDDServiceName = "ConferimentoDID_BindingQSPort";

    public java.lang.String getConferimentoDID_BindingQSPortWSDDServiceName() {
        return ConferimentoDID_BindingQSPortWSDDServiceName;
    }

    public void setConferimentoDID_BindingQSPortWSDDServiceName(java.lang.String name) {
        ConferimentoDID_BindingQSPortWSDDServiceName = name;
    }

    public it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType getConferimentoDID_BindingQSPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ConferimentoDID_BindingQSPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getConferimentoDID_BindingQSPort(endpoint);
    }

    public it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType getConferimentoDID_BindingQSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingStub _stub = new it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingStub(portAddress, this);
            _stub.setPortName(getConferimentoDID_BindingQSPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setConferimentoDID_BindingQSPortEndpointAddress(java.lang.String address) {
        ConferimentoDID_BindingQSPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingStub _stub = new it.gov.mlps.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingStub(new java.net.URL(ConferimentoDID_BindingQSPort_address), this);
                _stub.setPortName(getConferimentoDID_BindingQSPortWSDDServiceName());
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
        if ("ConferimentoDID_BindingQSPort".equals(inputPortName)) {
            return getConferimentoDID_BindingQSPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://mlps.gov.it/Services/InformationDelivery/ConferimentoDID/1.0", "ConferimentoDID_BindingQSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://mlps.gov.it/Services/InformationDelivery/ConferimentoDID/1.0", "ConferimentoDID_BindingQSPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ConferimentoDID_BindingQSPort".equals(portName)) {
            setConferimentoDID_BindingQSPortEndpointAddress(address);
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
