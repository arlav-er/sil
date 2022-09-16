/**
 * GetProtocolloServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.sintesi.protocollo.webservices;

public class GetProtocolloServiceLocator extends org.apache.axis.client.Service implements GetProtocolloService {

    public GetProtocolloServiceLocator() {
    }


    public GetProtocolloServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GetProtocolloServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GetProtocolloServiceSoap
    private String GetProtocolloServiceSoap_address = "x";

    public String getGetProtocolloServiceSoapAddress() {
        return GetProtocolloServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private String GetProtocolloServiceSoapWSDDServiceName = "GetProtocolloServiceSoap";

    public String getGetProtocolloServiceSoapWSDDServiceName() {
        return GetProtocolloServiceSoapWSDDServiceName;
    }

    public void setGetProtocolloServiceSoapWSDDServiceName(String name) {
        GetProtocolloServiceSoapWSDDServiceName = name;
    }

    public GetProtocolloServiceSoap getGetProtocolloServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GetProtocolloServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGetProtocolloServiceSoap(endpoint);
    }

    public GetProtocolloServiceSoap getGetProtocolloServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
           GetProtocolloServiceSoapStub _stub = new GetProtocolloServiceSoapStub(portAddress, this);
            _stub.setPortName(getGetProtocolloServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGetProtocolloServiceSoapEndpointAddress(String address) {
        GetProtocolloServiceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (GetProtocolloServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                GetProtocolloServiceSoapStub _stub = new GetProtocolloServiceSoapStub(new java.net.URL(GetProtocolloServiceSoap_address), this);
                _stub.setPortName(getGetProtocolloServiceSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
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
        String inputPortName = portName.getLocalPart();
        if ("GetProtocolloServiceSoap".equals(inputPortName)) {
            return getGetProtocolloServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://sintesi.provincia.milano.it/ServiziSapNP", "GetProtocolloService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://sintesi.provincia.milano.it/ServiziSapNP", "GetProtocolloServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("GetProtocolloServiceSoap".equals(portName)) {
            setGetProtocolloServiceSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
