/**
 * ConferimentoDID_BindingServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0;

public class ConferimentoDID_BindingServiceLocator extends org.apache.axis.client.Service
		implements it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingService {

	public ConferimentoDID_BindingServiceLocator() {
	}

	public ConferimentoDID_BindingServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ConferimentoDID_BindingServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for gestisciDID
	private java.lang.String gestisciDID_address = "";

	public java.lang.String getgestisciDIDAddress() {
		return gestisciDID_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String gestisciDIDWSDDServiceName = "gestisciDID";

	public java.lang.String getgestisciDIDWSDDServiceName() {
		return gestisciDIDWSDDServiceName;
	}

	public void setgestisciDIDWSDDServiceName(java.lang.String name) {
		gestisciDIDWSDDServiceName = name;
	}

	public it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType getgestisciDID()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(gestisciDID_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getgestisciDID(endpoint);
	}

	public it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType getgestisciDID(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingStub _stub = new it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingStub(
					portAddress, this);
			_stub.setPortName(getgestisciDIDWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setgestisciDIDEndpointAddress(java.lang.String address) {
		gestisciDID_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingStub _stub = new it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_BindingStub(
						new java.net.URL(gestisciDID_address), this);
				_stub.setPortName(getgestisciDIDWSDDServiceName());
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
		if ("gestisciDID".equals(inputPortName)) {
			return getgestisciDID();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://anpal.gov.it/Services/InformationDelivery/ConferimentoDID/1.0",
				"ConferimentoDID_BindingService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(
					"http://anpal.gov.it/Services/InformationDelivery/ConferimentoDID/1.0", "gestisciDID"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("gestisciDID".equals(portName)) {
			setgestisciDIDEndpointAddress(address);
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
