/**
 * GestioneConsensi_InserimentoConsensoServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

public class GestioneConsensi_InserimentoConsensoServiceLocator extends org.apache.axis.client.Service
		implements com.adobe.idp.services.GestioneConsensi_InserimentoConsensoService {

	public GestioneConsensi_InserimentoConsensoServiceLocator() {
	}

	public GestioneConsensi_InserimentoConsensoServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public GestioneConsensi_InserimentoConsensoServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for InserimentoConsenso
	private java.lang.String InserimentoConsenso_address = "";

	public java.lang.String getInserimentoConsensoAddress() {
		return InserimentoConsenso_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String InserimentoConsensoWSDDServiceName = "InserimentoConsenso";

	public java.lang.String getInserimentoConsensoWSDDServiceName() {
		return InserimentoConsensoWSDDServiceName;
	}

	public void setInserimentoConsensoWSDDServiceName(java.lang.String name) {
		InserimentoConsensoWSDDServiceName = name;
	}

	public com.adobe.idp.services.GestioneConsensi_InserimentoConsenso getInserimentoConsenso()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(InserimentoConsenso_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getInserimentoConsenso(endpoint);
	}

	public com.adobe.idp.services.GestioneConsensi_InserimentoConsenso getInserimentoConsenso(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			com.adobe.idp.services.InserimentoConsensoSoapBindingStub _stub = new com.adobe.idp.services.InserimentoConsensoSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getInserimentoConsensoWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setInserimentoConsensoEndpointAddress(java.lang.String address) {
		InserimentoConsenso_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (com.adobe.idp.services.GestioneConsensi_InserimentoConsenso.class
					.isAssignableFrom(serviceEndpointInterface)) {
				com.adobe.idp.services.InserimentoConsensoSoapBindingStub _stub = new com.adobe.idp.services.InserimentoConsensoSoapBindingStub(
						new java.net.URL(InserimentoConsenso_address), this);
				_stub.setPortName(getInserimentoConsensoWSDDServiceName());
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
		if ("InserimentoConsenso".equals(inputPortName)) {
			return getInserimentoConsenso();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://adobe.com/idp/services",
				"GestioneConsensi_InserimentoConsensoService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://adobe.com/idp/services", "InserimentoConsenso"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("InserimentoConsenso".equals(portName)) {
			setInserimentoConsensoEndpointAddress(address);
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
