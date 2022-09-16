/**
 * GestioneConsensi_RicercaConsensoServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

public class GestioneConsensi_RicercaConsensoServiceLocator extends org.apache.axis.client.Service
		implements com.adobe.idp.services.GestioneConsensi_RicercaConsensoService {

	public GestioneConsensi_RicercaConsensoServiceLocator() {
	}

	public GestioneConsensi_RicercaConsensoServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public GestioneConsensi_RicercaConsensoServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for RicercaConsenso
	private java.lang.String RicercaConsenso_address = "";

	public java.lang.String getRicercaConsensoAddress() {
		return RicercaConsenso_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String RicercaConsensoWSDDServiceName = "RicercaConsenso";

	public java.lang.String getRicercaConsensoWSDDServiceName() {
		return RicercaConsensoWSDDServiceName;
	}

	public void setRicercaConsensoWSDDServiceName(java.lang.String name) {
		RicercaConsensoWSDDServiceName = name;
	}

	public com.adobe.idp.services.GestioneConsensi_RicercaConsenso getRicercaConsenso()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(RicercaConsenso_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getRicercaConsenso(endpoint);
	}

	public com.adobe.idp.services.GestioneConsensi_RicercaConsenso getRicercaConsenso(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			com.adobe.idp.services.RicercaConsensoSoapBindingStub _stub = new com.adobe.idp.services.RicercaConsensoSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getRicercaConsensoWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setRicercaConsensoEndpointAddress(java.lang.String address) {
		RicercaConsenso_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (com.adobe.idp.services.GestioneConsensi_RicercaConsenso.class
					.isAssignableFrom(serviceEndpointInterface)) {
				com.adobe.idp.services.RicercaConsensoSoapBindingStub _stub = new com.adobe.idp.services.RicercaConsensoSoapBindingStub(
						new java.net.URL(RicercaConsenso_address), this);
				_stub.setPortName(getRicercaConsensoWSDDServiceName());
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
		if ("RicercaConsenso".equals(inputPortName)) {
			return getRicercaConsenso();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://adobe.com/idp/services",
				"GestioneConsensi_RicercaConsensoService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://adobe.com/idp/services", "RicercaConsenso"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("RicercaConsenso".equals(portName)) {
			setRicercaConsensoEndpointAddress(address);
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
