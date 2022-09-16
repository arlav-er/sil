/**
 * EventiCondizionalitaRDCWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.EventiCondizionalitaRDC;

public class EventiCondizionalitaRDCWSServiceLocator extends org.apache.axis.client.Service
		implements it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWSService {

	public EventiCondizionalitaRDCWSServiceLocator() {
	}

	public EventiCondizionalitaRDCWSServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public EventiCondizionalitaRDCWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for EventiCondizionalitaRDCWS
	private java.lang.String EventiCondizionalitaRDCWS_address = "http://servizi.lavoro.gov.it/";

	public java.lang.String getEventiCondizionalitaRDCWSAddress() {
		return EventiCondizionalitaRDCWS_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String EventiCondizionalitaRDCWSWSDDServiceName = "EventiCondizionalitaRDCWS";

	public java.lang.String getEventiCondizionalitaRDCWSWSDDServiceName() {
		return EventiCondizionalitaRDCWSWSDDServiceName;
	}

	public void setEventiCondizionalitaRDCWSWSDDServiceName(java.lang.String name) {
		EventiCondizionalitaRDCWSWSDDServiceName = name;
	}

	public it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWS getEventiCondizionalitaRDCWS()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(EventiCondizionalitaRDCWS_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getEventiCondizionalitaRDCWS(endpoint);
	}

	public it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWS getEventiCondizionalitaRDCWS(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWSSoapBindingStub _stub = new it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWSSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getEventiCondizionalitaRDCWSWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setEventiCondizionalitaRDCWSEndpointAddress(java.lang.String address) {
		EventiCondizionalitaRDCWS_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWS.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWSSoapBindingStub _stub = new it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWSSoapBindingStub(
						new java.net.URL(EventiCondizionalitaRDCWS_address), this);
				_stub.setPortName(getEventiCondizionalitaRDCWSWSDDServiceName());
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
		if ("EventiCondizionalitaRDCWS".equals(inputPortName)) {
			return getEventiCondizionalitaRDCWS();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC",
				"EventiCondizionalitaRDCWSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC",
					"EventiCondizionalitaRDCWS"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("EventiCondizionalitaRDCWS".equals(portName)) {
			setEventiCondizionalitaRDCWSEndpointAddress(address);
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
