/**
 * DMPantaReiLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto.v161;

public class DMPantaReiLocator extends org.apache.axis.client.Service implements DMPantaRei {

	public DMPantaReiLocator() {
	}

	public DMPantaReiLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public DMPantaReiLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for DMPantaReiSoap
	private java.lang.String DMPantaReiSoap_address = "http://localhost/pantarei/dmpantarei.asmx";

	public java.lang.String getDMPantaReiSoapAddress() {
		return DMPantaReiSoap_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String DMPantaReiSoapWSDDServiceName = "DMPantaReiSoap";

	public java.lang.String getDMPantaReiSoapWSDDServiceName() {
		return DMPantaReiSoapWSDDServiceName;
	}

	public void setDMPantaReiSoapWSDDServiceName(java.lang.String name) {
		DMPantaReiSoapWSDDServiceName = name;
	}

	public DMPantaReiSoap getDMPantaReiSoap() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(DMPantaReiSoap_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getDMPantaReiSoap(endpoint);
	}

	public DMPantaReiSoap getDMPantaReiSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			DMPantaReiSoapStub _stub = new DMPantaReiSoapStub(portAddress, this);
			_stub.setPortName(getDMPantaReiSoapWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setDMPantaReiSoapEndpointAddress(java.lang.String address) {
		DMPantaReiSoap_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (DMPantaReiSoap.class.isAssignableFrom(serviceEndpointInterface)) {
				DMPantaReiSoapStub _stub = new DMPantaReiSoapStub(new java.net.URL(DMPantaReiSoap_address), this);
				_stub.setPortName(getDMPantaReiSoapWSDDServiceName());
				return _stub;
			}
		} catch (java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
				+ (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("DMPantaReiSoap".equals(inputPortName)) {
			return getDMPantaReiSoap();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://tempuri.org/", "DMPantaRei");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "DMPantaReiSoap"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("DMPantaReiSoap".equals(portName)) {
			setDMPantaReiSoapEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
