/**
 * SchedaAnagraficaProfessionaleWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SchedaAnagraficaProfessionaleWSServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWSService {

	public SchedaAnagraficaProfessionaleWSServiceLocator() {
	}

	public SchedaAnagraficaProfessionaleWSServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public SchedaAnagraficaProfessionaleWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for SchedaAnagraficaProfessionaleWSPort
	private java.lang.String SchedaAnagraficaProfessionaleWSPort_address = "http://vmsirio:31000/MySap/SchedaAnagraficaProfessionaleWS";

	public java.lang.String getSchedaAnagraficaProfessionaleWSPortAddress() {
		return SchedaAnagraficaProfessionaleWSPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String SchedaAnagraficaProfessionaleWSPortWSDDServiceName = "SchedaAnagraficaProfessionaleWSPort";

	public java.lang.String getSchedaAnagraficaProfessionaleWSPortWSDDServiceName() {
		return SchedaAnagraficaProfessionaleWSPortWSDDServiceName;
	}

	public void setSchedaAnagraficaProfessionaleWSPortWSDDServiceName(java.lang.String name) {
		SchedaAnagraficaProfessionaleWSPortWSDDServiceName = name;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWS getSchedaAnagraficaProfessionaleWSPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(SchedaAnagraficaProfessionaleWSPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getSchedaAnagraficaProfessionaleWSPort(endpoint);
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWS getSchedaAnagraficaProfessionaleWSPort(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWSServiceSoapBindingStub _stub = new it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWSServiceSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getSchedaAnagraficaProfessionaleWSPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setSchedaAnagraficaProfessionaleWSPortEndpointAddress(java.lang.String address) {
		SchedaAnagraficaProfessionaleWSPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWS.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWSServiceSoapBindingStub _stub = new it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWSServiceSoapBindingStub(
						new java.net.URL(SchedaAnagraficaProfessionaleWSPort_address), this);
				_stub.setPortName(getSchedaAnagraficaProfessionaleWSPortWSDDServiceName());
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
		if ("SchedaAnagraficaProfessionaleWSPort".equals(inputPortName)) {
			return getSchedaAnagraficaProfessionaleWSPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"SchedaAnagraficaProfessionaleWSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
					"SchedaAnagraficaProfessionaleWSPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("SchedaAnagraficaProfessionaleWSPort".equals(portName)) {
			setSchedaAnagraficaProfessionaleWSPortEndpointAddress(address);
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
