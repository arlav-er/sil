/**
 * SmashAe4Locator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class SmashAe4Locator extends org.apache.axis.client.Service implements com.tim.smashng.fe.ws.services.SmashAe4 {

    public SmashAe4Locator() {
    }


    public SmashAe4Locator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SmashAe4Locator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SmashAe4EndpointPort
    private java.lang.String SmashAe4EndpointPort_address = "";

    public java.lang.String getSmashAe4EndpointPortAddress() {
        return SmashAe4EndpointPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SmashAe4EndpointPortWSDDServiceName = "SmashAe4EndpointPort";

    public java.lang.String getSmashAe4EndpointPortWSDDServiceName() {
        return SmashAe4EndpointPortWSDDServiceName;
    }

    public void setSmashAe4EndpointPortWSDDServiceName(java.lang.String name) {
        SmashAe4EndpointPortWSDDServiceName = name;
    }

    public com.tim.smashng.fe.ws.services.SmashAe4Endpoint getSmashAe4EndpointPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SmashAe4EndpointPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSmashAe4EndpointPort(endpoint);
    }

    public com.tim.smashng.fe.ws.services.SmashAe4Endpoint getSmashAe4EndpointPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.tim.smashng.fe.ws.services.SmashAe4EndpointPortBindingStub _stub = new com.tim.smashng.fe.ws.services.SmashAe4EndpointPortBindingStub(portAddress, this);
            _stub.setPortName(getSmashAe4EndpointPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSmashAe4EndpointPortEndpointAddress(java.lang.String address) {
        SmashAe4EndpointPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.tim.smashng.fe.ws.services.SmashAe4Endpoint.class.isAssignableFrom(serviceEndpointInterface)) {
                com.tim.smashng.fe.ws.services.SmashAe4EndpointPortBindingStub _stub = new com.tim.smashng.fe.ws.services.SmashAe4EndpointPortBindingStub(new java.net.URL(SmashAe4EndpointPort_address), this);
                _stub.setPortName(getSmashAe4EndpointPortWSDDServiceName());
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
        if ("SmashAe4EndpointPort".equals(inputPortName)) {
            return getSmashAe4EndpointPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "SmashAe4");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "SmashAe4EndpointPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SmashAe4EndpointPort".equals(portName)) {
            setSmashAe4EndpointPortEndpointAddress(address);
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
