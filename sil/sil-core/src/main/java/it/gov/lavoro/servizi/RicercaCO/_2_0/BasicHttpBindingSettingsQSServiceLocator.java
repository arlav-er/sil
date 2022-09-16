/**
 * BasicHttpBindingSettingsQSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.RicercaCO._2_0;

public class BasicHttpBindingSettingsQSServiceLocator extends org.apache.axis.client.Service
		implements it.gov.lavoro.servizi.RicercaCO._2_0.BasicHttpBindingSettingsQSService {

	/**
	 * OSB Service
	 */

	public BasicHttpBindingSettingsQSServiceLocator() {
	}

	public BasicHttpBindingSettingsQSServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public BasicHttpBindingSettingsQSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for basicHttpBindingSettingsQSPort
	private java.lang.String basicHttpBindingSettingsQSPort_address = "";

	public java.lang.String getbasicHttpBindingSettingsQSPortAddress() {
		return basicHttpBindingSettingsQSPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String basicHttpBindingSettingsQSPortWSDDServiceName = "basicHttpBindingSettingsQSPort";

	public java.lang.String getbasicHttpBindingSettingsQSPortWSDDServiceName() {
		return basicHttpBindingSettingsQSPortWSDDServiceName;
	}

	public void setbasicHttpBindingSettingsQSPortWSDDServiceName(java.lang.String name) {
		basicHttpBindingSettingsQSPortWSDDServiceName = name;
	}

	public it.gov.lavoro.servizi.RicercaCO._2_0.IRicercaCO getbasicHttpBindingSettingsQSPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(basicHttpBindingSettingsQSPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getbasicHttpBindingSettingsQSPort(endpoint);
	}

	public it.gov.lavoro.servizi.RicercaCO._2_0.IRicercaCO getbasicHttpBindingSettingsQSPort(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.gov.lavoro.servizi.RicercaCO._2_0.BasicHttpBindingSettingsStub _stub = new it.gov.lavoro.servizi.RicercaCO._2_0.BasicHttpBindingSettingsStub(
					portAddress, this);
			_stub.setPortName(getbasicHttpBindingSettingsQSPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setbasicHttpBindingSettingsQSPortEndpointAddress(java.lang.String address) {
		basicHttpBindingSettingsQSPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.gov.lavoro.servizi.RicercaCO._2_0.IRicercaCO.class.isAssignableFrom(serviceEndpointInterface)) {
				it.gov.lavoro.servizi.RicercaCO._2_0.BasicHttpBindingSettingsStub _stub = new it.gov.lavoro.servizi.RicercaCO._2_0.BasicHttpBindingSettingsStub(
						new java.net.URL(basicHttpBindingSettingsQSPort_address), this);
				_stub.setPortName(getbasicHttpBindingSettingsQSPortWSDDServiceName());
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
		if ("basicHttpBindingSettingsQSPort".equals(inputPortName)) {
			return getbasicHttpBindingSettingsQSPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
				"basicHttpBindingSettingsQSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/RicercaCO/2.0",
					"basicHttpBindingSettingsQSPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("basicHttpBindingSettingsQSPort".equals(portName)) {
			setbasicHttpBindingSettingsQSPortEndpointAddress(address);
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
