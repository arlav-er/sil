/**
 * ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

public class ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloServiceLocator
		extends org.apache.axis.client.Service
		implements com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloService {

	public ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloServiceLocator() {
	}

	public ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloServiceLocator(
			org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloServiceLocator(java.lang.String wsdlLoc,
			javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for GetAttachmentsListByProtocollo
	private java.lang.String GetAttachmentsListByProtocollo_address = "";

	public java.lang.String getGetAttachmentsListByProtocolloAddress() {
		return GetAttachmentsListByProtocollo_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String GetAttachmentsListByProtocolloWSDDServiceName = "GetAttachmentsListByProtocollo";

	public java.lang.String getGetAttachmentsListByProtocolloWSDDServiceName() {
		return GetAttachmentsListByProtocolloWSDDServiceName;
	}

	public void setGetAttachmentsListByProtocolloWSDDServiceName(java.lang.String name) {
		GetAttachmentsListByProtocolloWSDDServiceName = name;
	}

	public com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo getGetAttachmentsListByProtocollo()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(GetAttachmentsListByProtocollo_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getGetAttachmentsListByProtocollo(endpoint);
	}

	public com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo getGetAttachmentsListByProtocollo(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			com.adobe.idp.services.GetAttachmentsListByProtocolloSoapBindingStub _stub = new com.adobe.idp.services.GetAttachmentsListByProtocolloSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getGetAttachmentsListByProtocolloWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setGetAttachmentsListByProtocolloEndpointAddress(java.lang.String address) {
		GetAttachmentsListByProtocollo_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocollo.class
					.isAssignableFrom(serviceEndpointInterface)) {
				com.adobe.idp.services.GetAttachmentsListByProtocolloSoapBindingStub _stub = new com.adobe.idp.services.GetAttachmentsListByProtocolloSoapBindingStub(
						new java.net.URL(GetAttachmentsListByProtocollo_address), this);
				_stub.setPortName(getGetAttachmentsListByProtocolloWSDDServiceName());
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
		if ("GetAttachmentsListByProtocollo".equals(inputPortName)) {
			return getGetAttachmentsListByProtocollo();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://adobe.com/idp/services",
				"ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://adobe.com/idp/services", "GetAttachmentsListByProtocollo"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("GetAttachmentsListByProtocollo".equals(portName)) {
			setGetAttachmentsListByProtocolloEndpointAddress(address);
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
