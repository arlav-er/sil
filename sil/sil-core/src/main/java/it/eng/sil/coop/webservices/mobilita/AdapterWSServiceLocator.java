/**
 * AdapterWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.mobilita;

public class AdapterWSServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.mobilita.AdapterWSService {

	public AdapterWSServiceLocator() {
	}

	public AdapterWSServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public AdapterWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for AdapterWS
	private java.lang.String AdapterWS_address = "http://172.28.21.156/ncrT/services/NCRService";

	public java.lang.String getAdapterWSAddress() {
		return AdapterWS_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String AdapterWSWSDDServiceName = "AdapterWS";

	public java.lang.String getAdapterWSWSDDServiceName() {
		return AdapterWSWSDDServiceName;
	}

	public void setAdapterWSWSDDServiceName(java.lang.String name) {
		AdapterWSWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.mobilita.AdapterWS getAdapterWS() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(AdapterWS_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getAdapterWS(endpoint);
	}

	public it.eng.sil.coop.webservices.mobilita.AdapterWS getAdapterWS(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.mobilita.AdapterWSSoapBindingStub _stub = new it.eng.sil.coop.webservices.mobilita.AdapterWSSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getAdapterWSWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setAdapterWSEndpointAddress(java.lang.String address) {
		AdapterWS_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.mobilita.AdapterWS.class.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.mobilita.AdapterWSSoapBindingStub _stub = new it.eng.sil.coop.webservices.mobilita.AdapterWSSoapBindingStub(
						new java.net.URL(AdapterWS_address), this);
				_stub.setPortName(getAdapterWSWSDDServiceName());
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
		if ("AdapterWS".equals(inputPortName)) {
			return getAdapterWS();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "AdapterWSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "AdapterWS"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("AdapterWS".equals(portName)) {
			setAdapterWSEndpointAddress(address);
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
