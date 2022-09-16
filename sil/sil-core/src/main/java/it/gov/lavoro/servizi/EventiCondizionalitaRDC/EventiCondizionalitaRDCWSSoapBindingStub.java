/**
 * EventiCondizionalitaRDCWSSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.EventiCondizionalitaRDC;

import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;

public class EventiCondizionalitaRDCWSSoapBindingStub extends org.apache.axis.client.Stub
		implements it.gov.lavoro.servizi.EventiCondizionalitaRDC.EventiCondizionalitaRDCWS {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[2];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("loadEventiCondizionalitaRDC");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "cod_cpi"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "cod_evento"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_evento"),
				it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_fiscale"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_fiscale_ope"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_protocollo_inps"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"dtt_domanda"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"dtt_evento"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "txt_note"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		param.setOmittable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_esito"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"des_esito"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("deleteEventiCondizionalitaRDC");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "cod_cpi"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "cod_evento"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_evento"),
				it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_fiscale"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_fiscale_ope"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_protocollo_inps"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"dtt_domanda"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"), java.util.Date.class, false,
				false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"dtt_evento"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"), java.util.Date.class, false,
				false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"cod_esito"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
						"des_esito"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

	}

	public EventiCondizionalitaRDCWSSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public EventiCondizionalitaRDCWSSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public EventiCondizionalitaRDCWSSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types",
				"cod_evento");
		cachedSerQNames.add(qName);
		cls = it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(enumsf);
		cachedDeserFactories.add(enumdf);

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

	public void loadEventiCondizionalitaRDC(java.lang.String cod_cpi,
			it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento cod_evento, java.lang.String cod_fiscale,
			java.lang.String cod_fiscale_ope, java.lang.String cod_protocollo_inps, java.util.Calendar dtt_domanda,
			java.util.Calendar dtt_evento, java.lang.String txt_note, javax.xml.rpc.holders.StringHolder cod_esito,
			javax.xml.rpc.holders.StringHolder des_esito) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "loadEventiCondizionalitaRDC"));

		// recupera le credenziali dal db
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		Credentials credentials = wsAuthUtils.getCredentials("condizionalita");
		if (credentials != null) {
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, credentials.getUsername());
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, credentials.getPassword());
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { cod_cpi, cod_evento, cod_fiscale,
					cod_fiscale_ope, cod_protocollo_inps, dtt_domanda, dtt_evento, txt_note });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					cod_esito.value = (java.lang.String) _output.get(new javax.xml.namespace.QName(
							"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "cod_esito"));
				} catch (java.lang.Exception _exception) {
					cod_esito.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(
							_output.get(new javax.xml.namespace.QName(
									"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "cod_esito")),
							java.lang.String.class);
				}
				try {
					des_esito.value = (java.lang.String) _output.get(new javax.xml.namespace.QName(
							"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "des_esito"));
				} catch (java.lang.Exception _exception) {
					des_esito.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(
							_output.get(new javax.xml.namespace.QName(
									"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "des_esito")),
							java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public void deleteEventiCondizionalitaRDC(java.lang.String cod_cpi,
			it.gov.lavoro.servizi.EventiCondizionalitaRDC.types.Cod_evento cod_evento, java.lang.String cod_fiscale,
			java.lang.String cod_fiscale_ope, java.lang.String cod_protocollo_inps, java.util.Date dtt_domanda,
			java.util.Date dtt_evento, javax.xml.rpc.holders.StringHolder cod_esito,
			javax.xml.rpc.holders.StringHolder des_esito) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("deleteEventiCondizionalitaRDC");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "deleteEventiCondizionalitaRDC"));

		// recupera le credenziali dal db
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		Credentials credentials = wsAuthUtils.getCredentials("deleteCondizionalita");
		if (credentials != null) {
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, credentials.getUsername());
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, credentials.getPassword());
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { cod_cpi, cod_evento, cod_fiscale,
					cod_fiscale_ope, cod_protocollo_inps, dtt_domanda, dtt_evento });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					cod_esito.value = (java.lang.String) _output.get(new javax.xml.namespace.QName(
							"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "cod_esito"));
				} catch (java.lang.Exception _exception) {
					cod_esito.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(
							_output.get(new javax.xml.namespace.QName(
									"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "cod_esito")),
							java.lang.String.class);
				}
				try {
					des_esito.value = (java.lang.String) _output.get(new javax.xml.namespace.QName(
							"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "des_esito"));
				} catch (java.lang.Exception _exception) {
					des_esito.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(
							_output.get(new javax.xml.namespace.QName(
									"http://servizi.lavoro.gov.it/EventiCondizionalitaRDC/types", "des_esito")),
							java.lang.String.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
