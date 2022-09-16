/**
 * DataAdesioneYG_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.dataAdesione;

public class DataAdesioneYG_ServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYG_Service {

	public DataAdesioneYG_ServiceLocator() {
	}

	public DataAdesioneYG_ServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public DataAdesioneYG_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for DataAdesioneYGPort
	private java.lang.String DataAdesioneYGPort_address = "http://localhost:38080/MyPortal/service/DataAdesioneYG";

	public java.lang.String getDataAdesioneYGPortAddress() {
		return DataAdesioneYGPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String DataAdesioneYGPortWSDDServiceName = "DataAdesioneYGPort";

	public java.lang.String getDataAdesioneYGPortWSDDServiceName() {
		return DataAdesioneYGPortWSDDServiceName;
	}

	public void setDataAdesioneYGPortWSDDServiceName(java.lang.String name) {
		DataAdesioneYGPortWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYG_PortType getDataAdesioneYGPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(DataAdesioneYGPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getDataAdesioneYGPort(endpoint);
	}

	public it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYG_PortType getDataAdesioneYGPort(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYGSoapBindingStub _stub = new it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYGSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getDataAdesioneYGPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setDataAdesioneYGPortEndpointAddress(java.lang.String address) {
		DataAdesioneYGPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYG_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYGSoapBindingStub _stub = new it.eng.sil.coop.webservices.dataAdesione.DataAdesioneYGSoapBindingStub(
						new java.net.URL(DataAdesioneYGPort_address), this);
				_stub.setPortName(getDataAdesioneYGPortWSDDServiceName());
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
		if ("DataAdesioneYGPort".equals(inputPortName)) {
			return getDataAdesioneYGPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://ws.myportal.eng.it/", "DataAdesioneYG");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://ws.myportal.eng.it/", "DataAdesioneYGPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("DataAdesioneYGPort".equals(portName)) {
			setDataAdesioneYGPortEndpointAddress(address);
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
