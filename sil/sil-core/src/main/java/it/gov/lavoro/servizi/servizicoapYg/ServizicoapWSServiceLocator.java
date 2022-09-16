/**
 * ServizicoapWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.servizicoapYg;

public class ServizicoapWSServiceLocator extends org.apache.axis.client.Service
		implements it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSService {

	public ServizicoapWSServiceLocator() {
	}

	public ServizicoapWSServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ServizicoapWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for servizicoapWS
	private java.lang.String servizicoapWS_address = "http://localhost";

	public java.lang.String getservizicoapWSAddress() {
		return servizicoapWS_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String servizicoapWSWSDDServiceName = "servizicoapWS";

	public java.lang.String getservizicoapWSWSDDServiceName() {
		return servizicoapWSWSDDServiceName;
	}

	public void setservizicoapWSWSDDServiceName(java.lang.String name) {
		servizicoapWSWSDDServiceName = name;
	}

	public it.gov.lavoro.servizi.servizicoapYg.ServizicoapWS getservizicoapWS() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(servizicoapWS_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getservizicoapWS(endpoint);
	}

	public it.gov.lavoro.servizi.servizicoapYg.ServizicoapWS getservizicoapWS(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSSoapBindingStub _stub = new it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getservizicoapWSWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setservizicoapWSEndpointAddress(java.lang.String address) {
		servizicoapWS_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.gov.lavoro.servizi.servizicoapYg.ServizicoapWS.class.isAssignableFrom(serviceEndpointInterface)) {
				it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSSoapBindingStub _stub = new it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSSoapBindingStub(
						new java.net.URL(servizicoapWS_address), this);
				_stub.setPortName(getservizicoapWSWSDDServiceName());
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
		if ("servizicoapWS".equals(inputPortName)) {
			return getservizicoapWS();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap", "servizicoapWSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap", "servizicoapWS"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("servizicoapWS".equals(portName)) {
			setservizicoapWSEndpointAddress(address);
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
