/**
 * ServizicoapWSSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.servizicoapSap;

public class ServizicoapWSSoapBindingStub extends org.apache.axis.client.Stub
		implements it.gov.lavoro.servizi.servizicoapSap.ServizicoapWS {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[6];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("invioSAP");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "SAP"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "Esito"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
						">risposta_invioSAP_Type>Esito"),
				it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "CodiceSAP"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("richiestaSAP");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "CodiceSAP"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "SAP"));
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("recuperaListaSAPNonAttive");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "Parametri"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "stringList"));
		oper.setReturnClass(java.lang.String[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "SAPs"));
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("verificaEsistenzaSAP");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "CodiceFiscale"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "CodiceSAP"));
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("annullaSAP");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "CodiceSAP"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "Esito"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
						">risposta_annullaSAP_Type>Esito"),
				it.gov.lavoro.servizi.servizicoapSap.types.Risposta_annullaSAP_TypeEsito.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("notificaSAP");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "CodiceSAP"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "MotivoNotifica"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "Esito"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
						">risposta_notificaSAP_Type>Esito"),
				it.gov.lavoro.servizi.servizicoapSap.types.Risposta_notificaSAP_TypeEsito.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[5] = oper;

	}

	public ServizicoapWSSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public ServizicoapWSSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public ServizicoapWSSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
				">risposta_annullaSAP_Type>Esito");
		cachedSerQNames.add(qName);
		cls = it.gov.lavoro.servizi.servizicoapSap.types.Risposta_annullaSAP_TypeEsito.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(enumsf);
		cachedDeserFactories.add(enumdf);

		qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
				">risposta_invioSAP_Type>Esito");
		cachedSerQNames.add(qName);
		cls = it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(enumsf);
		cachedDeserFactories.add(enumdf);

		qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
				">risposta_notificaSAP_Type>Esito");
		cachedSerQNames.add(qName);
		cls = it.gov.lavoro.servizi.servizicoapSap.types.Risposta_notificaSAP_TypeEsito.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(enumsf);
		cachedDeserFactories.add(enumdf);

		qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "stringList");
		cachedSerQNames.add(qName);
		cls = java.lang.String[].class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(simplelistsf);
		cachedDeserFactories.add(simplelistdf);

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

	public void invioSAP(java.lang.String SAP,
			it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore, javax.xml.rpc.holders.StringHolder codiceSAP)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "invioSAP"));

		String userSapKey = System.getProperty("BASIC_AUTH_SAP_USERNAME");
		if (userSapKey != null && !("").equalsIgnoreCase(userSapKey)) {
			String pwdSapKey = System.getProperty("BASIC_AUTH_SAP_PASSWORD");
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, userSapKey);
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, pwdSapKey);
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { SAP });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					esito.value = (it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito) _output.get(
							new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "Esito"));
				} catch (java.lang.Exception _exception) {
					esito.value = (it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito) org.apache.axis.utils.JavaUtils
							.convert(
									_output.get(new javax.xml.namespace.QName(
											"http://servizi.lavoro.gov.it/servizicoap/types", "Esito")),
									it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito.class);
				}
				try {
					messaggioErrore.value = (java.lang.String) _output.get(new javax.xml.namespace.QName(
							"http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore"));
				} catch (java.lang.Exception _exception) {
					messaggioErrore.value = (java.lang.String) org.apache.axis.utils.JavaUtils
							.convert(
									_output.get(new javax.xml.namespace.QName(
											"http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore")),
									java.lang.String.class);
				}
				try {
					codiceSAP.value = (java.lang.String) _output.get(new javax.xml.namespace.QName(
							"http://servizi.lavoro.gov.it/servizicoap/types", "CodiceSAP"));
				} catch (java.lang.Exception _exception) {
					codiceSAP.value = (java.lang.String) org.apache.axis.utils.JavaUtils
							.convert(
									_output.get(new javax.xml.namespace.QName(
											"http://servizi.lavoro.gov.it/servizicoap/types", "CodiceSAP")),
									java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String richiestaSAP(java.lang.String codiceSAP) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "richiestaSAP"));

		String userSapKey = System.getProperty("BASIC_AUTH_SAP_USERNAME");
		if (userSapKey != null && !("").equalsIgnoreCase(userSapKey)) {
			String pwdSapKey = System.getProperty("BASIC_AUTH_SAP_PASSWORD");
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, userSapKey);
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, pwdSapKey);
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { codiceSAP });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String[] recuperaListaSAPNonAttive(java.lang.String parametri) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
				"recuperaListaSAPNonAttive"));

		String userSapKey = System.getProperty("BASIC_AUTH_SAP_USERNAME");
		if (userSapKey != null && !("").equalsIgnoreCase(userSapKey)) {
			String pwdSapKey = System.getProperty("BASIC_AUTH_SAP_PASSWORD");
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, userSapKey);
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, pwdSapKey);
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parametri });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String[]) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp,
							java.lang.String[].class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public java.lang.String verificaEsistenzaSAP(java.lang.String codiceFiscale) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
				"verificaEsistenzaSAP"));

		String userSapKey = System.getProperty("BASIC_AUTH_SAP_USERNAME");
		if (userSapKey != null && !("").equalsIgnoreCase(userSapKey)) {
			String pwdSapKey = System.getProperty("BASIC_AUTH_SAP_PASSWORD");
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, userSapKey);
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, pwdSapKey);
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { codiceFiscale });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.String) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void annullaSAP(java.lang.String codiceSAP,
			it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_annullaSAP_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "annullaSAP"));

		String userSapKey = System.getProperty("BASIC_AUTH_SAP_USERNAME");
		if (userSapKey != null && !("").equalsIgnoreCase(userSapKey)) {
			String pwdSapKey = System.getProperty("BASIC_AUTH_SAP_PASSWORD");
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, userSapKey);
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, pwdSapKey);
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { codiceSAP });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					esito.value = (it.gov.lavoro.servizi.servizicoapSap.types.Risposta_annullaSAP_TypeEsito) _output
							.get(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
									"Esito"));
				} catch (java.lang.Exception _exception) {
					esito.value = (it.gov.lavoro.servizi.servizicoapSap.types.Risposta_annullaSAP_TypeEsito) org.apache.axis.utils.JavaUtils
							.convert(
									_output.get(new javax.xml.namespace.QName(
											"http://servizi.lavoro.gov.it/servizicoap/types", "Esito")),
									it.gov.lavoro.servizi.servizicoapSap.types.Risposta_annullaSAP_TypeEsito.class);
				}
				try {
					messaggioErrore.value = (java.lang.String) _output.get(new javax.xml.namespace.QName(
							"http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore"));
				} catch (java.lang.Exception _exception) {
					messaggioErrore.value = (java.lang.String) org.apache.axis.utils.JavaUtils
							.convert(
									_output.get(new javax.xml.namespace.QName(
											"http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore")),
									java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void notificaSAP(java.lang.String codiceSAP, java.lang.String motivoNotifica,
			it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_notificaSAP_TypeEsitoHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "notificaSAP"));

		String userSapKey = System.getProperty("BASIC_AUTH_SAP_USERNAME");
		if (userSapKey != null && !("").equalsIgnoreCase(userSapKey)) {
			String pwdSapKey = System.getProperty("BASIC_AUTH_SAP_PASSWORD");
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, userSapKey);
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, pwdSapKey);
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { codiceSAP, motivoNotifica });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					esito.value = (it.gov.lavoro.servizi.servizicoapSap.types.Risposta_notificaSAP_TypeEsito) _output
							.get(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types",
									"Esito"));
				} catch (java.lang.Exception _exception) {
					esito.value = (it.gov.lavoro.servizi.servizicoapSap.types.Risposta_notificaSAP_TypeEsito) org.apache.axis.utils.JavaUtils
							.convert(
									_output.get(new javax.xml.namespace.QName(
											"http://servizi.lavoro.gov.it/servizicoap/types", "Esito")),
									it.gov.lavoro.servizi.servizicoapSap.types.Risposta_notificaSAP_TypeEsito.class);
				}
				try {
					messaggioErrore.value = (java.lang.String) _output.get(new javax.xml.namespace.QName(
							"http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore"));
				} catch (java.lang.Exception _exception) {
					messaggioErrore.value = (java.lang.String) org.apache.axis.utils.JavaUtils
							.convert(
									_output.get(new javax.xml.namespace.QName(
											"http://servizi.lavoro.gov.it/servizicoap/types", "MessaggioErrore")),
									java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
