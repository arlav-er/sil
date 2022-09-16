/**
 * PattoOnlineLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.myportal.ws.pattoonline;

public class PattoOnlineLocator extends org.apache.axis.client.Service
		implements it.eng.myportal.ws.pattoonline.PattoOnline {

	public PattoOnlineLocator() {
	}

	public PattoOnlineLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public PattoOnlineLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for GestionePattoPort
	private java.lang.String GestionePattoPort_address = "";

	public java.lang.String getGestionePattoPortAddress() {
		return GestionePattoPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String GestionePattoPortWSDDServiceName = "GestionePattoPort";

	public java.lang.String getGestionePattoPortWSDDServiceName() {
		return GestionePattoPortWSDDServiceName;
	}

	public void setGestionePattoPortWSDDServiceName(java.lang.String name) {
		GestionePattoPortWSDDServiceName = name;
	}

	public it.eng.myportal.ws.pattoonline.GestionePattoOnline getGestionePattoPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(GestionePattoPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getGestionePattoPort(endpoint);
	}

	public it.eng.myportal.ws.pattoonline.GestionePattoOnline getGestionePattoPort(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.myportal.ws.pattoonline.PattoOnlineSoapBindingStub _stub = new it.eng.myportal.ws.pattoonline.PattoOnlineSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getGestionePattoPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setGestionePattoPortEndpointAddress(java.lang.String address) {
		GestionePattoPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.myportal.ws.pattoonline.GestionePattoOnline.class.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.myportal.ws.pattoonline.PattoOnlineSoapBindingStub _stub = new it.eng.myportal.ws.pattoonline.PattoOnlineSoapBindingStub(
						new java.net.URL(GestionePattoPort_address), this);
				_stub.setPortName(getGestionePattoPortWSDDServiceName());
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
		if ("GestionePattoPort".equals(inputPortName)) {
			return getGestionePattoPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://pattoonline.ws.myportal.eng.it/", "PattoOnline");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://pattoonline.ws.myportal.eng.it/", "GestionePattoPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("GestionePattoPort".equals(portName)) {
			setGestionePattoPortEndpointAddress(address);
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
