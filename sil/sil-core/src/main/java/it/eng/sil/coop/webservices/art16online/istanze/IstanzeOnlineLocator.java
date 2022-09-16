/**
 * IstanzeOnlineLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class IstanzeOnlineLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.art16online.istanze.IstanzeOnline {

	public IstanzeOnlineLocator() {
	}

	public IstanzeOnlineLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public IstanzeOnlineLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for GetIstanze
	private java.lang.String GetIstanze_address = "";

	public java.lang.String getGetIstanzeAddress() {
		return GetIstanze_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String GetIstanzeWSDDServiceName = "GetIstanze";

	public java.lang.String getGetIstanzeWSDDServiceName() {
		return GetIstanzeWSDDServiceName;
	}

	public void setGetIstanzeWSDDServiceName(java.lang.String name) {
		GetIstanzeWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnline getGetIstanze()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(GetIstanze_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getGetIstanze(endpoint);
	}

	public it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnline getGetIstanze(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.art16online.istanze.IstanzeOnlineSoapBindingStub _stub = new it.eng.sil.coop.webservices.art16online.istanze.IstanzeOnlineSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getGetIstanzeWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setGetIstanzeEndpointAddress(java.lang.String address) {
		GetIstanze_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.art16online.istanze.GestioneIstanzeOnline.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.art16online.istanze.IstanzeOnlineSoapBindingStub _stub = new it.eng.sil.coop.webservices.art16online.istanze.IstanzeOnlineSoapBindingStub(
						new java.net.URL(GetIstanze_address), this);
				_stub.setPortName(getGetIstanzeWSDDServiceName());
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
		if ("GetIstanze".equals(inputPortName)) {
			return getGetIstanze();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "IstanzeOnline");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
					"GetIstanze"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("GetIstanze".equals(portName)) {
			setGetIstanzeEndpointAddress(address);
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
