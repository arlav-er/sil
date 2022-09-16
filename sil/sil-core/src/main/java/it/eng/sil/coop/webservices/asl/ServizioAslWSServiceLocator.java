/**
 * ServizioAslWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.asl;

public class ServizioAslWSServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.asl.ServizioAslWSService {

	public ServizioAslWSServiceLocator() {
	}

	public ServizioAslWSServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ServizioAslWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for SpilAslWs
	private java.lang.String SpilAslWs_address = "";

	public java.lang.String getSpilAslWsAddress() {
		return SpilAslWs_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String SpilAslWsWSDDServiceName = "SpilAslWs";

	public java.lang.String getSpilAslWsWSDDServiceName() {
		return SpilAslWsWSDDServiceName;
	}

	public void setSpilAslWsWSDDServiceName(java.lang.String name) {
		SpilAslWsWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.asl.SPILASLWSImpl getSpilAslWs() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(SpilAslWs_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getSpilAslWs(endpoint);
	}

	public it.eng.sil.coop.webservices.asl.SPILASLWSImpl getSpilAslWs(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.asl.SpilAslWsSoapBindingStub _stub = new it.eng.sil.coop.webservices.asl.SpilAslWsSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getSpilAslWsWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setSpilAslWsEndpointAddress(java.lang.String address) {
		SpilAslWs_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.asl.SPILASLWSImpl.class.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.asl.SpilAslWsSoapBindingStub _stub = new it.eng.sil.coop.webservices.asl.SpilAslWsSoapBindingStub(
						new java.net.URL(SpilAslWs_address), this);
				_stub.setPortName(getSpilAslWsWSDDServiceName());
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
		if ("SpilAslWs".equals(inputPortName)) {
			return getSpilAslWs();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "ServizioAslWSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://asl.webservices.coop.sil.eng.it", "SpilAslWs"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("SpilAslWs".equals(portName)) {
			setSpilAslWsEndpointAddress(address);
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
