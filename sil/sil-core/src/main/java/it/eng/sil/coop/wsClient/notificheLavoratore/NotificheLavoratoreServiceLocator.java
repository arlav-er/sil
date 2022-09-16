/**
 * NotificheLavoratoreServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.notificheLavoratore;

public class NotificheLavoratoreServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratoreService {

	public NotificheLavoratoreServiceLocator() {
	}

	public NotificheLavoratoreServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public NotificheLavoratoreServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for NotificheLavoratore
	private java.lang.String NotificheLavoratore_address = "http://localhost:9080/sil/services/NotificheLavoratore";

	public java.lang.String getNotificheLavoratoreAddress() {
		return NotificheLavoratore_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String NotificheLavoratoreWSDDServiceName = "NotificheLavoratore";

	public java.lang.String getNotificheLavoratoreWSDDServiceName() {
		return NotificheLavoratoreWSDDServiceName;
	}

	public void setNotificheLavoratoreWSDDServiceName(java.lang.String name) {
		NotificheLavoratoreWSDDServiceName = name;
	}

	public it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratore getNotificheLavoratore()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(NotificheLavoratore_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getNotificheLavoratore(endpoint);
	}

	public it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratore getNotificheLavoratore(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratoreSoapBindingStub _stub = new it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratoreSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getNotificheLavoratoreWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setNotificheLavoratoreEndpointAddress(java.lang.String address) {
		NotificheLavoratore_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratore.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratoreSoapBindingStub _stub = new it.eng.sil.coop.wsClient.notificheLavoratore.NotificheLavoratoreSoapBindingStub(
						new java.net.URL(NotificheLavoratore_address), this);
				_stub.setPortName(getNotificheLavoratoreWSDDServiceName());
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
		if ("NotificheLavoratore".equals(inputPortName)) {
			return getNotificheLavoratore();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://notificheLavoratore.wsClient.coop.sil.eng.it",
				"NotificheLavoratoreService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://notificheLavoratore.wsClient.coop.sil.eng.it",
					"NotificheLavoratore"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("NotificheLavoratore".equals(portName)) {
			setNotificheLavoratoreEndpointAddress(address);
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
