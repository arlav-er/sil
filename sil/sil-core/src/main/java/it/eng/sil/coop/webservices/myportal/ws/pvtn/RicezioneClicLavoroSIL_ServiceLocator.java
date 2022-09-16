/**
 * RicezioneClicLavoroSIL_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.myportal.ws.pvtn;

public class RicezioneClicLavoroSIL_ServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.myportal.ws.pvtn.RicezioneClicLavoroSIL_Service {

	public RicezioneClicLavoroSIL_ServiceLocator() {
	}

	public RicezioneClicLavoroSIL_ServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public RicezioneClicLavoroSIL_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for RicezioneClicLavoroSILPort
	private java.lang.String RicezioneClicLavoroSILPort_address = "http://161.27.206.84:43000/MyPortal/service/RicezioneClicLavoroSIL";

	public java.lang.String getRicezioneClicLavoroSILPortAddress() {
		return RicezioneClicLavoroSILPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String RicezioneClicLavoroSILPortWSDDServiceName = "RicezioneClicLavoroSILPort";

	public java.lang.String getRicezioneClicLavoroSILPortWSDDServiceName() {
		return RicezioneClicLavoroSILPortWSDDServiceName;
	}

	public void setRicezioneClicLavoroSILPortWSDDServiceName(java.lang.String name) {
		RicezioneClicLavoroSILPortWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.myportal.ws.pvtn.RicezioneClicLavoroSIL_PortType getRicezioneClicLavoroSILPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(RicezioneClicLavoroSILPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getRicezioneClicLavoroSILPort(endpoint);
	}

	public it.eng.sil.coop.webservices.myportal.ws.pvtn.RicezioneClicLavoroSIL_PortType getRicezioneClicLavoroSILPort(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.myportal.ws.pvtn.RicezioneClicLavoroSILSoapBindingStub _stub = new it.eng.sil.coop.webservices.myportal.ws.pvtn.RicezioneClicLavoroSILSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getRicezioneClicLavoroSILPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setRicezioneClicLavoroSILPortEndpointAddress(java.lang.String address) {
		RicezioneClicLavoroSILPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.myportal.ws.pvtn.RicezioneClicLavoroSIL_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.myportal.ws.pvtn.RicezioneClicLavoroSILSoapBindingStub _stub = new it.eng.sil.coop.webservices.myportal.ws.pvtn.RicezioneClicLavoroSILSoapBindingStub(
						new java.net.URL(RicezioneClicLavoroSILPort_address), this);
				_stub.setPortName(getRicezioneClicLavoroSILPortWSDDServiceName());
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
		if ("RicezioneClicLavoroSILPort".equals(inputPortName)) {
			return getRicezioneClicLavoroSILPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://ws.myportal.eng.it/", "RicezioneClicLavoroSIL");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://ws.myportal.eng.it/", "RicezioneClicLavoroSILPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("RicezioneClicLavoroSILPort".equals(portName)) {
			setRicezioneClicLavoroSILPortEndpointAddress(address);
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
