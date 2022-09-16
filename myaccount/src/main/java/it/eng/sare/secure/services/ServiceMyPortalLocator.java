/**
 * ServiceMyPortalLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sare.secure.services;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;

public class ServiceMyPortalLocator extends org.apache.axis.client.Service implements
		it.eng.sare.secure.services.ServiceMyPortal {

	private static final long serialVersionUID = 1086801187135662206L;

	public ServiceMyPortalLocator() {
	}

	public ServiceMyPortalLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ServiceMyPortalLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for ServiceMyPortalSoap
	private java.lang.String ServiceMyPortalSoap_address = "";

	public java.lang.String getServiceMyPortalSoapAddress() {
		return ServiceMyPortalSoap_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String ServiceMyPortalSoapWSDDServiceName = "ServiceMyPortalSoap";

	public java.lang.String getServiceMyPortalSoapWSDDServiceName() {
		return ServiceMyPortalSoapWSDDServiceName;
	}

	public void setServiceMyPortalSoapWSDDServiceName(java.lang.String name) {
		ServiceMyPortalSoapWSDDServiceName = name;
	}

	public it.eng.sare.secure.services.ServiceMyPortalSoap getServiceMyPortalSoap()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(ServiceMyPortalSoap_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getServiceMyPortalSoap(endpoint);
	}

	public it.eng.sare.secure.services.ServiceMyPortalSoap getServiceMyPortalSoap(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sare.secure.services.ServiceMyPortalSoapStub _stub = new it.eng.sare.secure.services.ServiceMyPortalSoapStub(
					portAddress, this);
			_stub.setPortName(getServiceMyPortalSoapWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setServiceMyPortalSoapEndpointAddress(java.lang.String address) {
		ServiceMyPortalSoap_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sare.secure.services.ServiceMyPortalSoap.class.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sare.secure.services.ServiceMyPortalSoapStub _stub = new it.eng.sare.secure.services.ServiceMyPortalSoapStub(
						new java.net.URL(ServiceMyPortalSoap_address), this);
				_stub.setPortName(getServiceMyPortalSoapWSDDServiceName());
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
		if ("ServiceMyPortalSoap".equals(inputPortName)) {
			return getServiceMyPortalSoap();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://regione.emilia-romagna.it/sare/secure/services/",
				"ServiceMyPortal");
	}

	private HashSet<QName> ports = null;

	public Iterator<QName> getPorts() {
		if (ports == null) {
			ports = new HashSet<QName>();
			ports.add(new QName("http://regione.emilia-romagna.it/sare/secure/services/", "ServiceMyPortalSoap"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("ServiceMyPortalSoap".equals(portName)) {
			setServiceMyPortalSoapEndpointAddress(address);
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
