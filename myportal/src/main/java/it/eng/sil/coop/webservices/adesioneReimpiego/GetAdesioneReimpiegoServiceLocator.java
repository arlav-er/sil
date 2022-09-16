/**
 * GetAdesioneReimpiegoServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.adesioneReimpiego;

public class GetAdesioneReimpiegoServiceLocator extends org.apache.axis.client.Service implements it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiegoService {

    public GetAdesioneReimpiegoServiceLocator() {
    }


    public GetAdesioneReimpiegoServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GetAdesioneReimpiegoServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GetAdesioneReimpiego
    private java.lang.String GetAdesioneReimpiego_address = "";

    public java.lang.String getGetAdesioneReimpiegoAddress() {
        return GetAdesioneReimpiego_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GetAdesioneReimpiegoWSDDServiceName = "GetAdesioneReimpiego";

    public java.lang.String getGetAdesioneReimpiegoWSDDServiceName() {
        return GetAdesioneReimpiegoWSDDServiceName;
    }

    public void setGetAdesioneReimpiegoWSDDServiceName(java.lang.String name) {
        GetAdesioneReimpiegoWSDDServiceName = name;
    }

    public it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiego getGetAdesioneReimpiego() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GetAdesioneReimpiego_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGetAdesioneReimpiego(endpoint);
    }

    public it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiego getGetAdesioneReimpiego(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiegoSoapBindingStub _stub = new it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiegoSoapBindingStub(portAddress, this);
            _stub.setPortName(getGetAdesioneReimpiegoWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGetAdesioneReimpiegoEndpointAddress(java.lang.String address) {
        GetAdesioneReimpiego_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiego.class.isAssignableFrom(serviceEndpointInterface)) {
                it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiegoSoapBindingStub _stub = new it.eng.sil.coop.webservices.adesioneReimpiego.GetAdesioneReimpiegoSoapBindingStub(new java.net.URL(GetAdesioneReimpiego_address), this);
                _stub.setPortName(getGetAdesioneReimpiegoWSDDServiceName());
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
        if ("GetAdesioneReimpiego".equals(inputPortName)) {
            return getGetAdesioneReimpiego();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://adesionereimpiego.webservices.coop.sil.eng.it", "GetAdesioneReimpiegoService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://adesionereimpiego.webservices.coop.sil.eng.it", "GetAdesioneReimpiego"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GetAdesioneReimpiego".equals(portName)) {
            setGetAdesioneReimpiegoEndpointAddress(address);
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
