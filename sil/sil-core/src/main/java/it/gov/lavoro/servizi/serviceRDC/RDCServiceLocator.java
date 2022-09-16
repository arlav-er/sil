/**
 * RDCServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.serviceRDC;

public class RDCServiceLocator extends org.apache.axis.client.Service
		implements it.gov.lavoro.servizi.serviceRDC.RDCService {

	public RDCServiceLocator() {
	}

	public RDCServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public RDCServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for RDC
	private java.lang.String RDC_address = "http://servizi.lavoro.gov.it/";

	public java.lang.String getRDCAddress() {
		return RDC_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String RDCWSDDServiceName = "RDC";

	public java.lang.String getRDCWSDDServiceName() {
		return RDCWSDDServiceName;
	}

	public void setRDCWSDDServiceName(java.lang.String name) {
		RDCWSDDServiceName = name;
	}

	public it.gov.lavoro.servizi.serviceRDC.RDC getRDC() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(RDC_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getRDC(endpoint);
	}

	public it.gov.lavoro.servizi.serviceRDC.RDC getRDC(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.gov.lavoro.servizi.serviceRDC.RDCSoapBindingStub _stub = new it.gov.lavoro.servizi.serviceRDC.RDCSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getRDCWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setRDCEndpointAddress(java.lang.String address) {
		RDC_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.gov.lavoro.servizi.serviceRDC.RDC.class.isAssignableFrom(serviceEndpointInterface)) {
				it.gov.lavoro.servizi.serviceRDC.RDCSoapBindingStub _stub = new it.gov.lavoro.servizi.serviceRDC.RDCSoapBindingStub(
						new java.net.URL(RDC_address), this);
				_stub.setPortName(getRDCWSDDServiceName());
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
		if ("RDC".equals(inputPortName)) {
			return getRDC();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC", "RDCService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/serviceRDC", "RDC"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("RDC".equals(portName)) {
			setRDCEndpointAddress(address);
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
