/**
 * VerificaCondizioniNEET_BindingServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0;

public class VerificaCondizioniNEET_BindingServiceLocator extends org.apache.axis.client.Service implements
		it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_BindingService {

	public VerificaCondizioniNEET_BindingServiceLocator() {
	}

	public VerificaCondizioniNEET_BindingServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public VerificaCondizioniNEET_BindingServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for verificaCondizioniNEET
	private java.lang.String verificaCondizioniNEET_address = "http://regioneemiliaromagna.spcoop.gov.it/pdd/PD/VerificaCondizioniNEET/verificaCondizioniNEET";

	public java.lang.String getverificaCondizioniNEETAddress() {
		return verificaCondizioniNEET_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String verificaCondizioniNEETWSDDServiceName = "verificaCondizioniNEET";

	public java.lang.String getverificaCondizioniNEETWSDDServiceName() {
		return verificaCondizioniNEETWSDDServiceName;
	}

	public void setverificaCondizioniNEETWSDDServiceName(java.lang.String name) {
		verificaCondizioniNEETWSDDServiceName = name;
	}

	public it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_PortType getverificaCondizioniNEET()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(verificaCondizioniNEET_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getverificaCondizioniNEET(endpoint);
	}

	public it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_PortType getverificaCondizioniNEET(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEETSoapBindingStub _stub = new it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEETSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getverificaCondizioniNEETWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setverificaCondizioniNEETEndpointAddress(java.lang.String address) {
		verificaCondizioniNEET_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEET_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEETSoapBindingStub _stub = new it.gov.mlps.Services.InformationDelivery.VerificaCondizioniNEET._1_0.VerificaCondizioniNEETSoapBindingStub(
						new java.net.URL(verificaCondizioniNEET_address), this);
				_stub.setPortName(getverificaCondizioniNEETWSDDServiceName());
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
		if ("verificaCondizioniNEET".equals(inputPortName)) {
			return getverificaCondizioniNEET();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(
				"http://mlps.gov.it/Services/InformationDelivery/VerificaCondizioniNEET/1.0",
				"VerificaCondizioniNEET_BindingService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(
					"http://mlps.gov.it/Services/InformationDelivery/VerificaCondizioniNEET/1.0",
					"verificaCondizioniNEET"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("verificaCondizioniNEET".equals(portName)) {
			setverificaCondizioniNEETEndpointAddress(address);
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
