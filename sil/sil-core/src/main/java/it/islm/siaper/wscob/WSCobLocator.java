/**
 * WSCobLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.islm.siaper.wscob;

public class WSCobLocator extends org.apache.axis.client.Service implements it.islm.siaper.wscob.WSCob {

    /**
	 * 
	 */
	private static final long serialVersionUID = -981130847228064987L;

	public WSCobLocator() {
    }


    public WSCobLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WSCobLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WSCobSoap
    private java.lang.String WSCobSoap_address = "";

    public java.lang.String getWSCobSoapAddress() {
        return WSCobSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WSCobSoapWSDDServiceName = "WSCobSoap";

    public java.lang.String getWSCobSoapWSDDServiceName() {
        return WSCobSoapWSDDServiceName;
    }

    public void setWSCobSoapWSDDServiceName(java.lang.String name) {
        WSCobSoapWSDDServiceName = name;
    }

    public it.islm.siaper.wscob.WSCobSoap getWSCobSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSCobSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSCobSoap(endpoint);
    }

    public it.islm.siaper.wscob.WSCobSoap getWSCobSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.islm.siaper.wscob.WSCobSoapStub _stub = new it.islm.siaper.wscob.WSCobSoapStub(portAddress, this);
            _stub.setPortName(getWSCobSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSCobSoapEndpointAddress(java.lang.String address) {
        WSCobSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.islm.siaper.wscob.WSCobSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                it.islm.siaper.wscob.WSCobSoapStub _stub = new it.islm.siaper.wscob.WSCobSoapStub(new java.net.URL(WSCobSoap_address), this);
                _stub.setPortName(getWSCobSoapWSDDServiceName());
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
        if ("WSCobSoap".equals(inputPortName)) {
            return getWSCobSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://wscob.siaper.islm.it/", "WSCob");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://wscob.siaper.islm.it/", "WSCobSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WSCobSoap".equals(portName)) {
            setWSCobSoapEndpointAddress(address);
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
