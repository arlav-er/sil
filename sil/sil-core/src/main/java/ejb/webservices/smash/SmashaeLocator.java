/**
 * SmashaeLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ejb.webservices.smash;

public class SmashaeLocator extends org.apache.axis.client.Service implements ejb.webservices.smash.Smashae {

/**
 * todo
 */

    public SmashaeLocator() {
    }


    public SmashaeLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SmashaeLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SmashaePort
    private java.lang.String SmashaePort_address = "";

    public java.lang.String getSmashaePortAddress() {
        return SmashaePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SmashaePortWSDDServiceName = "SmashaePort";

    public java.lang.String getSmashaePortWSDDServiceName() {
        return SmashaePortWSDDServiceName;
    }

    public void setSmashaePortWSDDServiceName(java.lang.String name) {
        SmashaePortWSDDServiceName = name;
    }

    public ejb.webservices.smash.SmashaePortType getSmashaePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SmashaePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSmashaePort(endpoint);
    }

    public ejb.webservices.smash.SmashaePortType getSmashaePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ejb.webservices.smash.SmashaeBindingStub _stub = new ejb.webservices.smash.SmashaeBindingStub(portAddress, this);
            _stub.setPortName(getSmashaePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSmashaePortEndpointAddress(java.lang.String address) {
        SmashaePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ejb.webservices.smash.SmashaePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                ejb.webservices.smash.SmashaeBindingStub _stub = new ejb.webservices.smash.SmashaeBindingStub(new java.net.URL(SmashaePort_address), this);
                _stub.setPortName(getSmashaePortWSDDServiceName());
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
        if ("SmashaePort".equals(inputPortName)) {
            return getSmashaePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("java:smash.webservices.ejb", "Smashae");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("java:smash.webservices.ejb", "SmashaePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SmashaePort".equals(portName)) {
            setSmashaePortEndpointAddress(address);
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
