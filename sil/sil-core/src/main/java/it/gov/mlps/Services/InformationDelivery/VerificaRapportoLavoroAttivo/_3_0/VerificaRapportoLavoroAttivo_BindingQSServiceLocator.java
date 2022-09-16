/**
 * VerificaRapportoLavoroAttivo_BindingQSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0;

public class VerificaRapportoLavoroAttivo_BindingQSServiceLocator extends org.apache.axis.client.Service implements
		it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_BindingQSService {

	/**
	 * OSB Service
	 */

	public VerificaRapportoLavoroAttivo_BindingQSServiceLocator() {
	}

	public VerificaRapportoLavoroAttivo_BindingQSServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public VerificaRapportoLavoroAttivo_BindingQSServiceLocator(java.lang.String wsdlLoc,
			javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for VerificaRapportoLavoroAttivo_BindingQSPort
	private java.lang.String VerificaRapportoLavoroAttivo_BindingQSPort_address = "";

	public java.lang.String getVerificaRapportoLavoroAttivo_BindingQSPortAddress() {
		return VerificaRapportoLavoroAttivo_BindingQSPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String VerificaRapportoLavoroAttivo_BindingQSPortWSDDServiceName = "VerificaRapportoLavoroAttivo_BindingQSPort";

	public java.lang.String getVerificaRapportoLavoroAttivo_BindingQSPortWSDDServiceName() {
		return VerificaRapportoLavoroAttivo_BindingQSPortWSDDServiceName;
	}

	public void setVerificaRapportoLavoroAttivo_BindingQSPortWSDDServiceName(java.lang.String name) {
		VerificaRapportoLavoroAttivo_BindingQSPortWSDDServiceName = name;
	}

	public it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_PortType getVerificaRapportoLavoroAttivo_BindingQSPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(VerificaRapportoLavoroAttivo_BindingQSPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getVerificaRapportoLavoroAttivo_BindingQSPort(endpoint);
	}

	public it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_PortType getVerificaRapportoLavoroAttivo_BindingQSPort(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_BindingStub _stub = new it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_BindingStub(
					portAddress, this);
			_stub.setPortName(getVerificaRapportoLavoroAttivo_BindingQSPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setVerificaRapportoLavoroAttivo_BindingQSPortEndpointAddress(java.lang.String address) {
		VerificaRapportoLavoroAttivo_BindingQSPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_BindingStub _stub = new it.gov.mlps.Services.InformationDelivery.VerificaRapportoLavoroAttivo._3_0.VerificaRapportoLavoroAttivo_BindingStub(
						new java.net.URL(VerificaRapportoLavoroAttivo_BindingQSPort_address), this);
				_stub.setPortName(getVerificaRapportoLavoroAttivo_BindingQSPortWSDDServiceName());
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
		if ("VerificaRapportoLavoroAttivo_BindingQSPort".equals(inputPortName)) {
			return getVerificaRapportoLavoroAttivo_BindingQSPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(
				"http://mlps.gov.it/Services/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
				"VerificaRapportoLavoroAttivo_BindingQSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(
					"http://mlps.gov.it/Services/InformationDelivery/VerificaRapportoLavoroAttivo/3.0",
					"VerificaRapportoLavoroAttivo_BindingQSPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("VerificaRapportoLavoroAttivo_BindingQSPort".equals(portName)) {
			setVerificaRapportoLavoroAttivo_BindingQSPortEndpointAddress(address);
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
