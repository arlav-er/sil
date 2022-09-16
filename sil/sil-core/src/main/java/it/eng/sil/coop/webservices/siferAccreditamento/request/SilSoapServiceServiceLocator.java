/**
 * SilSoapServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.siferAccreditamento.request;

public class SilSoapServiceServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServiceService {

	public SilSoapServiceServiceLocator() {
	}

	public SilSoapServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public SilSoapServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for SilSoapServicePort
	private java.lang.String SilSoapServicePort_address = "http://sifer.local/app_dev.php/WebService/sil/partecipante";

	public java.lang.String getSilSoapServicePortAddress() {
		return SilSoapServicePort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String SilSoapServicePortWSDDServiceName = "SilSoapServicePort";

	public java.lang.String getSilSoapServicePortWSDDServiceName() {
		return SilSoapServicePortWSDDServiceName;
	}

	public void setSilSoapServicePortWSDDServiceName(java.lang.String name) {
		SilSoapServicePortWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServicePort getSilSoapServicePort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(SilSoapServicePort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getSilSoapServicePort(endpoint);
	}

	public it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServicePort getSilSoapServicePort(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServiceBindingStub _stub = new it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServiceBindingStub(
					portAddress, this);
			_stub.setPortName(getSilSoapServicePortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setSilSoapServicePortEndpointAddress(java.lang.String address) {
		SilSoapServicePort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServicePort.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServiceBindingStub _stub = new it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServiceBindingStub(
						new java.net.URL(SilSoapServicePort_address), this);
				_stub.setPortName(getSilSoapServicePortWSDDServiceName());
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
		if ("SilSoapServicePort".equals(inputPortName)) {
			return getSilSoapServicePort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"SilSoapServiceService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
					"SilSoapServicePort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("SilSoapServicePort".equals(portName)) {
			setSilSoapServicePortEndpointAddress(address);
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
