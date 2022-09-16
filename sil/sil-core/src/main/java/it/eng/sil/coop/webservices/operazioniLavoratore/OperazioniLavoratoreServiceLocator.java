/**
 * OperazioniLavoratoreServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.operazioniLavoratore;

public class OperazioniLavoratoreServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratoreService {

	public OperazioniLavoratoreServiceLocator() {
	}

	public OperazioniLavoratoreServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public OperazioniLavoratoreServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for OperazioniLavoratore
	private java.lang.String OperazioniLavoratore_address = "http://172.28.101.21:28080/SilRerWeb/services/OperazioniLavoratore";

	public java.lang.String getOperazioniLavoratoreAddress() {
		return OperazioniLavoratore_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String OperazioniLavoratoreWSDDServiceName = "OperazioniLavoratore";

	public java.lang.String getOperazioniLavoratoreWSDDServiceName() {
		return OperazioniLavoratoreWSDDServiceName;
	}

	public void setOperazioniLavoratoreWSDDServiceName(java.lang.String name) {
		OperazioniLavoratoreWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratore getOperazioniLavoratore()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(OperazioniLavoratore_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getOperazioniLavoratore(endpoint);
	}

	public it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratore getOperazioniLavoratore(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratoreSoapBindingStub _stub = new it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratoreSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getOperazioniLavoratoreWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setOperazioniLavoratoreEndpointAddress(java.lang.String address) {
		OperazioniLavoratore_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratore.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratoreSoapBindingStub _stub = new it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratoreSoapBindingStub(
						new java.net.URL(OperazioniLavoratore_address), this);
				_stub.setPortName(getOperazioniLavoratoreWSDDServiceName());
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
		if ("OperazioniLavoratore".equals(inputPortName)) {
			return getOperazioniLavoratore();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://operazioniLavoratore.webservices.coop.sil.eng.it",
				"OperazioniLavoratoreService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://operazioniLavoratore.webservices.coop.sil.eng.it",
					"OperazioniLavoratore"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("OperazioniLavoratore".equals(portName)) {
			setOperazioniLavoratoreEndpointAddress(address);
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
