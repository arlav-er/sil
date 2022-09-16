/**
 * GetStatoOccupazionaleServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.statooccupazionale;

public class GetStatoOccupazionaleServiceLocator extends org.apache.axis.client.Service implements it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleService {

    public GetStatoOccupazionaleServiceLocator() {
    }


    public GetStatoOccupazionaleServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GetStatoOccupazionaleServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GetStatoOccupazionale
    private java.lang.String GetStatoOccupazionale_address = "";

    public java.lang.String getGetStatoOccupazionaleAddress() {
        return GetStatoOccupazionale_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GetStatoOccupazionaleWSDDServiceName = "GetStatoOccupazionale";

    public java.lang.String getGetStatoOccupazionaleWSDDServiceName() {
        return GetStatoOccupazionaleWSDDServiceName;
    }

    public void setGetStatoOccupazionaleWSDDServiceName(java.lang.String name) {
        GetStatoOccupazionaleWSDDServiceName = name;
    }

    public it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionale getGetStatoOccupazionale() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GetStatoOccupazionale_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGetStatoOccupazionale(endpoint);
    }

    public it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionale getGetStatoOccupazionale(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleSoapBindingStub _stub = new it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleSoapBindingStub(portAddress, this);
            _stub.setPortName(getGetStatoOccupazionaleWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGetStatoOccupazionaleEndpointAddress(java.lang.String address) {
        GetStatoOccupazionale_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionale.class.isAssignableFrom(serviceEndpointInterface)) {
                it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleSoapBindingStub _stub = new it.eng.sil.coop.webservices.statooccupazionale.GetStatoOccupazionaleSoapBindingStub(new java.net.URL(GetStatoOccupazionale_address), this);
                _stub.setPortName(getGetStatoOccupazionaleWSDDServiceName());
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
        if ("GetStatoOccupazionale".equals(inputPortName)) {
            return getGetStatoOccupazionale();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://statooccupazionale.webservices.coop.sil.eng.it", "GetStatoOccupazionaleService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://statooccupazionale.webservices.coop.sil.eng.it", "GetStatoOccupazionale"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GetStatoOccupazionale".equals(portName)) {
            setGetStatoOccupazionaleEndpointAddress(address);
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
