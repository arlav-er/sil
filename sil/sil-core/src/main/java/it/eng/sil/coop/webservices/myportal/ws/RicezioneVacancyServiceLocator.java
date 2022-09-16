/**
 * RicezioneVacancyServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.myportal.ws;

public class RicezioneVacancyServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancyService {

	public RicezioneVacancyServiceLocator() {
	}

	public RicezioneVacancyServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public RicezioneVacancyServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for RicezioneVacancyPort
	private java.lang.String RicezioneVacancyPort_address = "http://COTICONE-D:28080/MyPortal/service/RicezioneVacancy";

	public java.lang.String getRicezioneVacancyPortAddress() {
		return RicezioneVacancyPort_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String RicezioneVacancyPortWSDDServiceName = "RicezioneVacancyPort";

	public java.lang.String getRicezioneVacancyPortWSDDServiceName() {
		return RicezioneVacancyPortWSDDServiceName;
	}

	public void setRicezioneVacancyPortWSDDServiceName(java.lang.String name) {
		RicezioneVacancyPortWSDDServiceName = name;
	}

	public it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancy getRicezioneVacancyPort()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(RicezioneVacancyPort_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getRicezioneVacancyPort(endpoint);
	}

	public it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancy getRicezioneVacancyPort(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancyServiceSoapBindingStub _stub = new it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancyServiceSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getRicezioneVacancyPortWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setRicezioneVacancyPortEndpointAddress(java.lang.String address) {
		RicezioneVacancyPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancy.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancyServiceSoapBindingStub _stub = new it.eng.sil.coop.webservices.myportal.ws.RicezioneVacancyServiceSoapBindingStub(
						new java.net.URL(RicezioneVacancyPort_address), this);
				_stub.setPortName(getRicezioneVacancyPortWSDDServiceName());
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
		if ("RicezioneVacancyPort".equals(inputPortName)) {
			return getRicezioneVacancyPort();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://ws.myportal.eng.it/", "RicezioneVacancyService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://ws.myportal.eng.it/", "RicezioneVacancyPort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("RicezioneVacancyPort".equals(portName)) {
			setRicezioneVacancyPortEndpointAddress(address);
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
