/**
 * DOCAREAProtoSoapStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto;

public class DOCAREAProtoSoapStub extends org.apache.axis.client.Stub
		implements it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoap {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	private boolean usaDIME;
	private boolean SOAP12;

	// configurazione namespace della busta soap
	private static String targetNamespace = DocAreaWSConfig.getTargetNamespace();
	// configurazione namespace del tipo di ritorno della busta soap
	private static String targetNamespaceResponse = DocAreaWSConfig.getTargetNamespaceResponseType();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[3];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("Login");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(targetNamespace, "Login"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(targetNamespace, ">Login"),
				it.eng.sil.coop.wsClient.docareaProto.Login.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(targetNamespace, ">LoginResponse"));
		oper.setReturnClass(it.eng.sil.coop.wsClient.docareaProto.LoginResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName(targetNamespace, "LoginResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("Inserimento");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(targetNamespace, "Inserimento"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(targetNamespace, ">Inserimento"),
				it.eng.sil.coop.wsClient.docareaProto.Inserimento.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(targetNamespace, ">InserimentoResponse"));
		oper.setReturnClass(it.eng.sil.coop.wsClient.docareaProto.InserimentoResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName(targetNamespace, "InserimentoResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("Protocollazione");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(targetNamespace, "Protocollazione"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(targetNamespace, ">Protocollazione"),
				it.eng.sil.coop.wsClient.docareaProto.Protocollazione.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(targetNamespace, ">ProtocollazioneResponse"));
		oper.setReturnClass(it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName(targetNamespace, "ProtocollazioneResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[2] = oper;

	}

	public DOCAREAProtoSoapStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public DOCAREAProtoSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public DOCAREAProtoSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName(targetNamespace, ">Inserimento");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.Inserimento.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(targetNamespace, ">InserimentoResponse");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.InserimentoResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(targetNamespace, ">Login");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.Login.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(targetNamespace, ">LoginResponse");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.LoginResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(targetNamespace, ">Protocollazione");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.Protocollazione.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(targetNamespace, ">ProtocollazioneResponse");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(targetNamespaceResponse, "InserimentoRet");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.InserimentoRet.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(targetNamespaceResponse, "LoginRet");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.LoginRet.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(targetNamespaceResponse, "ProtocollazioneRet");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneRet.class;
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

	public it.eng.sil.coop.wsClient.docareaProto.LoginResponse login(
			it.eng.sil.coop.wsClient.docareaProto.Login parameters) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}

		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		// imposto l'actionUri della chiamata http
		String actionURI = targetNamespace + "/Login";
		_call.setSOAPActionURI(actionURI);

		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		if (SOAP12)
			_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
		else
			_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "Login"));
		// non so come funziona il mantenimento della sessione di axis :)
		// _call.setMaintainSession(true);
		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.eng.sil.coop.wsClient.docareaProto.LoginResponse) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.eng.sil.coop.wsClient.docareaProto.LoginResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, it.eng.sil.coop.wsClient.docareaProto.LoginResponse.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public it.eng.sil.coop.wsClient.docareaProto.InserimentoResponse inserimento(
			it.eng.sil.coop.wsClient.docareaProto.Inserimento parameters, javax.activation.DataSource ds)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		// imposto l'actionUri della chiamata http
		String actionURI = targetNamespace + "/Inserimento";
		_call.setSOAPActionURI(actionURI);

		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		if (SOAP12)
			_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
		else
			_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "Inserimento"));
		if (usaDIME)
			_call.setProperty(org.apache.axis.client.Call.ATTACHMENT_ENCAPSULATION_FORMAT,
					org.apache.axis.client.Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);

		setRequestHeaders(_call);
		// INIZIO ATTACHMENT
		org.apache.axis.attachments.AttachmentPart ap = null;
		ap = new org.apache.axis.attachments.AttachmentPart();
		javax.activation.DataHandler dh = new javax.activation.DataHandler(ds);
		ap.setDataHandler(dh);
		addAttachment(ap);
		// FINE ATTACHMENT
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });
			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.eng.sil.coop.wsClient.docareaProto.InserimentoResponse) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.eng.sil.coop.wsClient.docareaProto.InserimentoResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, it.eng.sil.coop.wsClient.docareaProto.InserimentoResponse.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}

	}

	public it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneResponse protocollazione(
			it.eng.sil.coop.wsClient.docareaProto.Protocollazione parameters, javax.activation.DataSource ds)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		// imposto l'actionUri della chiamata http
		String actionURI = targetNamespace + "/Protocollazione";
		_call.setSOAPActionURI(actionURI);

		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		if (SOAP12)
			_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
		else
			_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "Protocollazione"));
		if (usaDIME)
			_call.setProperty(org.apache.axis.client.Call.ATTACHMENT_ENCAPSULATION_FORMAT,
					org.apache.axis.client.Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);

		setRequestHeaders(_call);
		org.apache.axis.attachments.AttachmentPart ap = null;
		ap = new org.apache.axis.attachments.AttachmentPart();
		javax.activation.DataHandler dh = new javax.activation.DataHandler(ds);
		ap.setDataHandler(dh);
		addAttachment(ap);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneResponse) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneResponse.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	/**
	 * Se si deve utilizzare l'attachment di tipo DIME bisogna chiamare questo metodo. Il defalut e' MIME.
	 * 
	 * @param b
	 */
	public void setUsaDIME(boolean b) {
		usaDIME = b;
	}

	/**
	 * Se si deve utilizzare la versione 1.2 di SOAP bisogna chiamare questo metodo. La versione di defalut e' 1.1
	 * 
	 * @param b
	 */
	public void setSOAP12(boolean b) {
		SOAP12 = b;
	}

}
