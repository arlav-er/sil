/**
 * ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.siferPartecipanteGG;

public class ServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.siferPartecipanteGG.Service {

	public ServiceLocator() {
	}

	public ServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for requestService
	private java.lang.String requestService_address = "";

	public java.lang.String getrequestServiceAddress() {
		return requestService_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String requestServiceWSDDServiceName = "requestService";

	public java.lang.String getrequestServiceWSDDServiceName() {
		return requestServiceWSDDServiceName;
	}

	public void setrequestServiceWSDDServiceName(java.lang.String name) {
		requestServiceWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.siferPartecipanteGG.PortType getrequestService()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(requestService_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getrequestService(endpoint);
	}

	public it.eng.sil.coop.webservices.siferPartecipanteGG.PortType getrequestService(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.siferPartecipanteGG.BindingStub _stub = new it.eng.sil.coop.webservices.siferPartecipanteGG.BindingStub(
					portAddress, this);
			_stub.setPortName(getrequestServiceWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setrequestServiceEndpointAddress(java.lang.String address) {
		requestService_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.siferPartecipanteGG.PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.siferPartecipanteGG.BindingStub _stub = new it.eng.sil.coop.webservices.siferPartecipanteGG.BindingStub(
						new java.net.URL(requestService_address), this);
				_stub.setPortName(getrequestServiceWSDDServiceName());
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
		if ("requestService".equals(inputPortName)) {
			return getrequestService();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("https://sifer.regione.emilia-romagna.it/WebService", "service");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("https://sifer.regione.emilia-romagna.it/WebService",
					"requestService"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("requestService".equals(portName)) {
			setrequestServiceEndpointAddress(address);
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
