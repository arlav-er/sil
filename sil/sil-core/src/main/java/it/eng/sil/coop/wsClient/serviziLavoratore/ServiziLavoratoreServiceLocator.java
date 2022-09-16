/**
 * ServiziLavoratoreServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.serviziLavoratore;

public class ServiziLavoratoreServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratoreService {

	public ServiziLavoratoreServiceLocator() {
	}

	public ServiziLavoratoreServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ServiziLavoratoreServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for ServiziLavoratore
	private java.lang.String ServiziLavoratore_address = "http://localhost:9090/idxReg/services/ServiziLavoratore";

	public java.lang.String getServiziLavoratoreAddress() {
		return ServiziLavoratore_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String ServiziLavoratoreWSDDServiceName = "ServiziLavoratore";

	public java.lang.String getServiziLavoratoreWSDDServiceName() {
		return ServiziLavoratoreWSDDServiceName;
	}

	public void setServiziLavoratoreWSDDServiceName(java.lang.String name) {
		ServiziLavoratoreWSDDServiceName = name;
	}

	public it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratore getServiziLavoratore()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(ServiziLavoratore_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getServiziLavoratore(endpoint);
	}

	public it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratore getServiziLavoratore(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratoreSoapBindingStub _stub = new it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratoreSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getServiziLavoratoreWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setServiziLavoratoreEndpointAddress(java.lang.String address) {
		ServiziLavoratore_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratore.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratoreSoapBindingStub _stub = new it.eng.sil.coop.wsClient.serviziLavoratore.ServiziLavoratoreSoapBindingStub(
						new java.net.URL(ServiziLavoratore_address), this);
				_stub.setPortName(getServiziLavoratoreWSDDServiceName());
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
		if ("ServiziLavoratore".equals(inputPortName)) {
			return getServiziLavoratore();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://serviziLavoratore.wsClient.coop.sil.eng.it",
				"ServiziLavoratoreService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://serviziLavoratore.wsClient.coop.sil.eng.it",
					"ServiziLavoratore"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("ServiziLavoratore".equals(portName)) {
			setServiziLavoratoreEndpointAddress(address);
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
