/**
 * MessageReceiverServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.messageReceiver;

public class MessageReceiverServiceLocator extends org.apache.axis.client.Service
		implements it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverService {

	public MessageReceiverServiceLocator() {
	}

	public MessageReceiverServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public MessageReceiverServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for MessageReceiver
	private java.lang.String MessageReceiver_address = "http://localhost:9090/idxReg/services/MessageReceiver";

	public java.lang.String getMessageReceiverAddress() {
		return MessageReceiver_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String MessageReceiverWSDDServiceName = "MessageReceiver";

	public java.lang.String getMessageReceiverWSDDServiceName() {
		return MessageReceiverWSDDServiceName;
	}

	public void setMessageReceiverWSDDServiceName(java.lang.String name) {
		MessageReceiverWSDDServiceName = name;
	}

	public it.eng.sil.coop.wsClient.messageReceiver.MessageReceiver getMessageReceiver()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(MessageReceiver_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getMessageReceiver(endpoint);
	}

	public it.eng.sil.coop.wsClient.messageReceiver.MessageReceiver getMessageReceiver(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverSoapBindingStub _stub = new it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getMessageReceiverWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setMessageReceiverEndpointAddress(java.lang.String address) {
		MessageReceiver_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has no port for the given interface, then
	 * ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (it.eng.sil.coop.wsClient.messageReceiver.MessageReceiver.class
					.isAssignableFrom(serviceEndpointInterface)) {
				it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverSoapBindingStub _stub = new it.eng.sil.coop.wsClient.messageReceiver.MessageReceiverSoapBindingStub(
						new java.net.URL(MessageReceiver_address), this);
				_stub.setPortName(getMessageReceiverWSDDServiceName());
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
		if ("MessageReceiver".equals(inputPortName)) {
			return getMessageReceiver();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://messageReceiver.wsClient.coop.sil.eng.it",
				"MessageReceiverService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://messageReceiver.wsClient.coop.sil.eng.it",
					"MessageReceiver"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("MessageReceiver".equals(portName)) {
			setMessageReceiverEndpointAddress(address);
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
