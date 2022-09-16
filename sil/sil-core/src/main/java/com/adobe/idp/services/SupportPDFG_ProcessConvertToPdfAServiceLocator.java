/**
 * SupportPDFG_ProcessConvertToPdfAServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

public class SupportPDFG_ProcessConvertToPdfAServiceLocator extends org.apache.axis.client.Service
		implements com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfAService {

	public SupportPDFG_ProcessConvertToPdfAServiceLocator() {
	}

	public SupportPDFG_ProcessConvertToPdfAServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public SupportPDFG_ProcessConvertToPdfAServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for ProcessConvertToPdfA
	private java.lang.String ProcessConvertToPdfA_address = "";

	public java.lang.String getProcessConvertToPdfAAddress() {
		return ProcessConvertToPdfA_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String ProcessConvertToPdfAWSDDServiceName = "ProcessConvertToPdfA";

	public java.lang.String getProcessConvertToPdfAWSDDServiceName() {
		return ProcessConvertToPdfAWSDDServiceName;
	}

	public void setProcessConvertToPdfAWSDDServiceName(java.lang.String name) {
		ProcessConvertToPdfAWSDDServiceName = name;
	}

	public com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfA getProcessConvertToPdfA()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(ProcessConvertToPdfA_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getProcessConvertToPdfA(endpoint);
	}

	public com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfA getProcessConvertToPdfA(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			com.adobe.idp.services.ProcessConvertToPdfASoapBindingStub _stub = new com.adobe.idp.services.ProcessConvertToPdfASoapBindingStub(
					portAddress, this);
			_stub.setPortName(getProcessConvertToPdfAWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setProcessConvertToPdfAEndpointAddress(java.lang.String address) {
		ProcessConvertToPdfA_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfA.class
					.isAssignableFrom(serviceEndpointInterface)) {
				com.adobe.idp.services.ProcessConvertToPdfASoapBindingStub _stub = new com.adobe.idp.services.ProcessConvertToPdfASoapBindingStub(
						new java.net.URL(ProcessConvertToPdfA_address), this);
				_stub.setPortName(getProcessConvertToPdfAWSDDServiceName());
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
		if ("ProcessConvertToPdfA".equals(inputPortName)) {
			return getProcessConvertToPdfA();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://adobe.com/idp/services",
				"SupportPDFG_ProcessConvertToPdfAService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://adobe.com/idp/services", "ProcessConvertToPdfA"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("ProcessConvertToPdfA".equals(portName)) {
			setProcessConvertToPdfAEndpointAddress(address);
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
