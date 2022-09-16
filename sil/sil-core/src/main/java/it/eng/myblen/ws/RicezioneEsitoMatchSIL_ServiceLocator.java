/**
 * RicezioneEsitoMatchSIL_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.myblen.ws;

public class RicezioneEsitoMatchSIL_ServiceLocator extends org.apache.axis.client.Service
		implements it.eng.myblen.ws.RicezioneEsitoMatchSIL_Service {

	public RicezioneEsitoMatchSIL_ServiceLocator() {
	}

	public RicezioneEsitoMatchSIL_ServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public RicezioneEsitoMatchSIL_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for RicezioneEsitoMatchSILPort
	private java.lang.String RicezioneEsitoMatchSILPort_address = "";

	public java.lang.String getRicezioneEsitoMatchSILPortAddress() {
		return RicezioneEsitoMatchSILPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String RicezioneEsitoMatchSILPortWSDDServiceName = "RicezioneEsitoMatchSILPort";

	public java.lang.String getRicezioneEsitoMatchSILPortWSDDServiceName() {
		return RicezioneEsitoMatchSILPortWSDDServiceName;
	}

	public void setRicezioneEsitoMatchSILPortWSDDServiceName(java.lang.String name) {
		RicezioneEsitoMatchSILPortWSDDServiceName = name;
	}

	public it.eng.myblen.ws.RicezioneEsitoMatchSIL_PortType getRicezioneEsitoMatchSILPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(RicezioneEsitoMatchSILPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getRicezioneEsitoMatchSILPort(endpoint);
	}

	public it.eng.myblen.ws.RicezioneEsitoMatchSIL_PortType getRicezioneEsitoMatchSILPort(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.myblen.ws.RicezioneEsitoMatchSILSoapBindingStub _stub = new it.eng.myblen.ws.RicezioneEsitoMatchSILSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getRicezioneEsitoMatchSILPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setRicezioneEsitoMatchSILPortEndpointAddress(java.lang.String address) {
		RicezioneEsitoMatchSILPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.myblen.ws.RicezioneEsitoMatchSIL_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.myblen.ws.RicezioneEsitoMatchSILSoapBindingStub _stub = new it.eng.myblen.ws.RicezioneEsitoMatchSILSoapBindingStub(
						new java.net.URL(RicezioneEsitoMatchSILPort_address), this);
				_stub.setPortName(getRicezioneEsitoMatchSILPortWSDDServiceName());
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
		if ("RicezioneEsitoMatchSILPort".equals(inputPortName)) {
			return getRicezioneEsitoMatchSILPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://ws.myblen.eng.it/", "RicezioneEsitoMatchSIL");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://ws.myblen.eng.it/", "RicezioneEsitoMatchSILPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("RicezioneEsitoMatchSILPort".equals(portName)) {
			setRicezioneEsitoMatchSILPortEndpointAddress(address);
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
