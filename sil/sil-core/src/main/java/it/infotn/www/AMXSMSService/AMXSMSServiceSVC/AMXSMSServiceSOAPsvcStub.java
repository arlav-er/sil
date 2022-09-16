/**
 * AMXSMSServiceSOAPsvcStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.infotn.www.AMXSMSService.AMXSMSServiceSVC;

import org.apache.axis.AxisFault;

public class AMXSMSServiceSOAPsvcStub extends org.apache.axis.client.Stub implements
		it.infotn.www.AMXSMSService.AMXSMSServiceSVC.InvioSMS_port_type {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[4];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("SMSStatus");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.infotn.it/SMS-EAI/InvioSMS_message", "SentSMSStatusMessage"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMSForStatusRequest_type"),
				it.infotn.www.SMS_EAI.InvioSMS_types.SMSForStatusRequest_type.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types",
				"SendStatusResponse_type"));
		oper.setReturnClass(it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_type.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_message",
				"SentSMSStatusResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://www.infotn.it/SMS-EAI/InvioSMS_message", "InvioSMSFault"),
				"it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type", new javax.xml.namespace.QName(
						"http://www.infotn.it/SMS-EAI/InvioSMS_types", "InvioSMSFault_type"), true));
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("NumSMSSent");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.infotn.it/SMS-EAI/InvioSMS_message", "NumSMSSentMessage"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.infotn.it/SMS-EAI/InvioSMS_types", "UserInfoType"),
				it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "numSMS_type"));
		oper.setReturnClass(it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_message",
				"NumSMSSentResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://www.infotn.it/SMS-EAI/InvioSMS_message", "InvioSMSFault"),
				"it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type", new javax.xml.namespace.QName(
						"http://www.infotn.it/SMS-EAI/InvioSMS_types", "InvioSMSFault_type"), true));
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("SMSAvailable");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.infotn.it/SMS-EAI/InvioSMS_message", "SMSAvailableMessage"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.infotn.it/SMS-EAI/InvioSMS_types", "UserInfoType"),
				it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "numSMS_type"));
		oper.setReturnClass(it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_message",
				"SMSAvailableResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://www.infotn.it/SMS-EAI/InvioSMS_message", "InvioSMSFault"),
				"it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type", new javax.xml.namespace.QName(
						"http://www.infotn.it/SMS-EAI/InvioSMS_types", "InvioSMSFault_type"), true));
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("SendSMS");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.infotn.it/SMS-EAI/InvioSMS_message", "SendSMSMessage"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMS_type"),
				it.infotn.www.SMS_EAI.InvioSMS_types.SMS_type.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types",
				"SMSResult_type"));
		oper.setReturnClass(it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_message",
				"SendSMSResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://www.infotn.it/SMS-EAI/InvioSMS_message", "InvioSMSFault"),
				"it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type", new javax.xml.namespace.QName(
						"http://www.infotn.it/SMS-EAI/InvioSMS_types", "InvioSMSFault_type"), true));
		_operations[3] = oper;

	}

	public AMXSMSServiceSOAPsvcStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public AMXSMSServiceSOAPsvcStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public AMXSMSServiceSOAPsvcStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		if (service == null) {
			super.service = new org.apache.axis.client.Service();
		} else {
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
		java.lang.Class cls;
		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
		java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
		java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
		java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
		java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
		java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
		java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
		java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
		java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types",
				">>SendStatusResponse_type>SMSStatus>SendStatus");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_typeSMSStatusSendStatus.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(
				org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(
				org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types",
				">SendStatusResponse_type>SMSStatus");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_typeSMSStatusSendStatus[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types",
				">>SendStatusResponse_type>SMSStatus>SendStatus");
		qName2 = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SendStatus");
		cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", ">SMSResult_type>SMS");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_typeSMS.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "InvioSMSFault_type");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "numSMS_type");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SendStatusResponse_type");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_type.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMS_type");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.SMS_type.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMSForStatusRequest_type");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.SMSForStatusRequest_type.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMSMessage_type");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.SMSMessage_type.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMSResult_type");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "UserInfoType");
		cachedSerQNames.add(qName);
		cls = it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
		try {
			org.apache.axis.client.Call _call = super._createCall();
			if (super.maintainSessionSet) {
				_call.setMaintainSession(super.maintainSession);
			}
			if (super.cachedUsername != null) {
				_call.setUsername(super.cachedUsername);
			}
			if (super.cachedPassword != null) {
				_call.setPassword(super.cachedPassword);
			}
			if (super.cachedEndpoint != null) {
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if (super.cachedTimeout != null) {
				_call.setTimeout(super.cachedTimeout);
			}
			if (super.cachedPortName != null) {
				_call.setPortName(super.cachedPortName);
			}
			java.util.Enumeration keys = super.cachedProperties.keys();
			while (keys.hasMoreElements()) {
				java.lang.String key = (java.lang.String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			// All the type mapping information is registered
			// when the first call is made.
			// The type mapping information is actually registered in
			// the TypeMappingRegistry of the service, which
			// is the reason why registration is only needed for the first call.
			synchronized (this) {
				if (firstCall()) {
					// must set encoding style before registering serializers
					_call.setEncodingStyle(null);
					for (int i = 0; i < cachedSerFactories.size(); ++i) {
						java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
						javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames.get(i);
						java.lang.Object x = cachedSerFactories.get(i);
						if (x instanceof Class) {
							java.lang.Class sf = (java.lang.Class) cachedSerFactories.get(i);
							java.lang.Class df = (java.lang.Class) cachedDeserFactories.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						} else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
							org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories
									.get(i);
							org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories
									.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						}
					}
				}
			}
			return _call;
		} catch (java.lang.Throwable _t) {
			throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
		}
	}

	public it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_type SMSStatus(
			it.infotn.www.SMS_EAI.InvioSMS_types.SMSForStatusRequest_type request) throws java.rmi.RemoteException,
			it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("SMSStatus");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "SMSStatus"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { request });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_type) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_type) org.apache.axis.utils.JavaUtils
							.convert(_resp, it.infotn.www.SMS_EAI.InvioSMS_types.SendStatusResponse_type.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type) {
					throw (it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type numSMSSent(
			it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType request) throws java.rmi.RemoteException,
			it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("NumSMSSent");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "NumSMSSent"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { request });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type) org.apache.axis.utils.JavaUtils.convert(
							_resp, it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type) {
					throw (it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type SMSAvailable(
			it.infotn.www.SMS_EAI.InvioSMS_types.UserInfoType request) throws java.rmi.RemoteException,
			it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("SMSAvailable");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "SMSAvailable"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { request });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type) org.apache.axis.utils.JavaUtils.convert(
							_resp, it.infotn.www.SMS_EAI.InvioSMS_types.NumSMS_type.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type) {
					throw (it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type sendSMS(
			it.infotn.www.SMS_EAI.InvioSMS_types.SMS_type request) throws java.rmi.RemoteException,
			it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("SendSMS");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "SendSMS"));

		// //////////////////////////////
		// BASIC-AUTH
		// //////////////////////////////

		String sendSmsUsername = System.getProperty("BASIC_AUTH_SMS_USERNAME", "");
		String sendSmsPassword = System.getProperty("BASIC_AUTH_SMS_PASSWORD", "");
		
		if (sendSmsUsername.equals("") || sendSmsPassword.equals("")){
			throw new AxisFault("I parametri per la Basic authentication non sono stati impostati. Controlla service-properties.xml");
		}
		
		if (sendSmsUsername != null && !("").equalsIgnoreCase(sendSmsUsername)) {
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, sendSmsUsername);
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, sendSmsPassword);
		}
		// //////////////////////////////

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { request });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type) org.apache.axis.utils.JavaUtils
							.convert(_resp, it.infotn.www.SMS_EAI.InvioSMS_types.SMSResult_type.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type) {
					throw (it.infotn.www.SMS_EAI.InvioSMS_types.InvioSMSFault_type) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

}
