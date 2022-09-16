/**
 * YG_Profiling_BindingQSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3;

public class YG_Profiling_BindingQSServiceLocator extends org.apache.axis.client.Service
		implements it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_BindingQSService {

	public YG_Profiling_BindingQSServiceLocator() {
	}

	public YG_Profiling_BindingQSServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public YG_Profiling_BindingQSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for YG_Profiling_BindingQSPort
	private java.lang.String YG_Profiling_BindingQSPort_address = "";

	public java.lang.String getYG_Profiling_BindingQSPortAddress() {
		return YG_Profiling_BindingQSPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String YG_Profiling_BindingQSPortWSDDServiceName = "YG_Profiling_BindingQSPort";

	public java.lang.String getYG_Profiling_BindingQSPortWSDDServiceName() {
		return YG_Profiling_BindingQSPortWSDDServiceName;
	}

	public void setYG_Profiling_BindingQSPortWSDDServiceName(java.lang.String name) {
		YG_Profiling_BindingQSPortWSDDServiceName = name;
	}

	public it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_PortType getYG_Profiling_BindingQSPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(YG_Profiling_BindingQSPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getYG_Profiling_BindingQSPort(endpoint);
	}

	public it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_PortType getYG_Profiling_BindingQSPort(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_BindingStub _stub = new it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_BindingStub(
					portAddress, this);
			_stub.setPortName(getYG_Profiling_BindingQSPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setYG_Profiling_BindingQSPortEndpointAddress(java.lang.String address) {
		YG_Profiling_BindingQSPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_BindingStub _stub = new it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_BindingStub(
						new java.net.URL(YG_Profiling_BindingQSPort_address), this);
				_stub.setPortName(getYG_Profiling_BindingQSPortWSDDServiceName());
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
		if ("YG_Profiling_BindingQSPort".equals(inputPortName)) {
			return getYG_Profiling_BindingQSPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://mlps.gov.it/Services/InformationDelivery/YG_Profiling/1.3",
				"YG_Profiling_BindingQSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://mlps.gov.it/Services/InformationDelivery/YG_Profiling/1.3",
					"YG_Profiling_BindingQSPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("YG_Profiling_BindingQSPort".equals(portName)) {
			setYG_Profiling_BindingQSPortEndpointAddress(address);
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
