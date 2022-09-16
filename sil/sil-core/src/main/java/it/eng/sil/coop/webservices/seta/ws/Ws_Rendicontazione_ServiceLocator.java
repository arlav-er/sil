/**
 * Ws_Rendicontazione_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.seta.ws;

public class Ws_Rendicontazione_ServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.seta.ws.Ws_Rendicontazione_Service {

	public Ws_Rendicontazione_ServiceLocator() {
	}

	public Ws_Rendicontazione_ServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public Ws_Rendicontazione_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for Ws_RendicontazionePort
	private java.lang.String Ws_RendicontazionePort_address = "http://testsviluppo3.setacom.it:80/Ws_Rendicontazione";

	public java.lang.String getWs_RendicontazionePortAddress() {
		return Ws_RendicontazionePort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String Ws_RendicontazionePortWSDDServiceName = "Ws_RendicontazionePort";

	public java.lang.String getWs_RendicontazionePortWSDDServiceName() {
		return Ws_RendicontazionePortWSDDServiceName;
	}

	public void setWs_RendicontazionePortWSDDServiceName(java.lang.String name) {
		Ws_RendicontazionePortWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.seta.ws.Ws_Rendicontazione_PortType getWs_RendicontazionePort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(Ws_RendicontazionePort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getWs_RendicontazionePort(endpoint);
	}

	public it.eng.sil.coop.webservices.seta.ws.Ws_Rendicontazione_PortType getWs_RendicontazionePort(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.seta.ws.Ws_RendicontazionePortBindingStub _stub = new it.eng.sil.coop.webservices.seta.ws.Ws_RendicontazionePortBindingStub(
					portAddress, this);
			_stub.setPortName(getWs_RendicontazionePortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setWs_RendicontazionePortEndpointAddress(java.lang.String address) {
		Ws_RendicontazionePort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.seta.ws.Ws_Rendicontazione_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.seta.ws.Ws_RendicontazionePortBindingStub _stub = new it.eng.sil.coop.webservices.seta.ws.Ws_RendicontazionePortBindingStub(
						new java.net.URL(Ws_RendicontazionePort_address), this);
				_stub.setPortName(getWs_RendicontazionePortWSDDServiceName());
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
		if ("Ws_RendicontazionePort".equals(inputPortName)) {
			return getWs_RendicontazionePort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://ws.seta.com/", "Ws_Rendicontazione");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://ws.seta.com/", "Ws_RendicontazionePort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("Ws_RendicontazionePort".equals(portName)) {
			setWs_RendicontazionePortEndpointAddress(address);
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
