/**
 * SILWSSOAPImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.infosilperservsoc;

public class SILWSSOAPImplServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImplService {

	public SILWSSOAPImplServiceLocator() {
	}

	public SILWSSOAPImplServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public SILWSSOAPImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for SILWSSOAPImpl
	private java.lang.String SILWSSOAPImpl_address = "http://eraclito:28080/sil/services/InfoSilPerServSoc";

	public java.lang.String getSILWSSOAPImplAddress() {
		return SILWSSOAPImpl_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String SILWSSOAPImplWSDDServiceName = "SILWSSOAPImpl";

	public java.lang.String getSILWSSOAPImplWSDDServiceName() {
		return SILWSSOAPImplWSDDServiceName;
	}

	public void setSILWSSOAPImplWSDDServiceName(java.lang.String name) {
		SILWSSOAPImplWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImpl getSILWSSOAPImpl()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(SILWSSOAPImpl_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getSILWSSOAPImpl(endpoint);
	}

	public it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImpl getSILWSSOAPImpl(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImplSoapBindingStub _stub = new it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImplSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getSILWSSOAPImplWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setSILWSSOAPImplEndpointAddress(java.lang.String address) {
		SILWSSOAPImpl_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImpl.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImplSoapBindingStub _stub = new it.eng.sil.coop.webservices.infosilperservsoc.SILWSSOAPImplSoapBindingStub(
						new java.net.URL(SILWSSOAPImpl_address), this);
				_stub.setPortName(getSILWSSOAPImplWSDDServiceName());
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
		if ("SILWSSOAPImpl".equals(inputPortName)) {
			return getSILWSSOAPImpl();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "SILWSSOAPImplService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://sil.eng.it/SILWS/", "SILWSSOAPImpl"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("SILWSSOAPImpl".equals(portName)) {
			setSILWSSOAPImplEndpointAddress(address);
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
