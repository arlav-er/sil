/**
 * DOCAREAProtoLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto;

public class DOCAREAProtoLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.wsClient.docareaProto.DOCAREAProto {

	// configurazione namespace della busta soap
	private static String targetNamespace = DocAreaWSConfig.getTargetNamespace();

	public DOCAREAProtoLocator() {
	}

	public DOCAREAProtoLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public DOCAREAProtoLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for DOCAREAProtoSoap
	private java.lang.String DOCAREAProtoSoap_address = "http://localhost/docarea/DOCAREAProto";

	public java.lang.String getDOCAREAProtoSoapAddress() {
		return DOCAREAProtoSoap_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String DOCAREAProtoSoapWSDDServiceName = "DOCAREAProtoSoap";

	public java.lang.String getDOCAREAProtoSoapWSDDServiceName() {
		return DOCAREAProtoSoapWSDDServiceName;
	}

	public void setDOCAREAProtoSoapWSDDServiceName(java.lang.String name) {
		DOCAREAProtoSoapWSDDServiceName = name;
	}

	public it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoap getDOCAREAProtoSoap()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(DOCAREAProtoSoap_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getDOCAREAProtoSoap(endpoint);
	}

	public it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoap getDOCAREAProtoSoap(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoapStub _stub = new it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoapStub(
					portAddress, this);
			_stub.setPortName(getDOCAREAProtoSoapWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setDOCAREAProtoSoapEndpointAddress(java.lang.String address) {
		DOCAREAProtoSoap_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoap.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoapStub _stub = new it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoapStub(
						new java.net.URL(DOCAREAProtoSoap_address), this);
				_stub.setPortName(getDOCAREAProtoSoapWSDDServiceName());
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
		if ("DOCAREAProtoSoap".equals(inputPortName)) {
			return getDOCAREAProtoSoap();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(targetNamespace, "DOCAREAProto");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(targetNamespace, "DOCAREAProtoSoap"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("DOCAREAProtoSoap".equals(portName)) {
			setDOCAREAProtoSoapEndpointAddress(address);
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
