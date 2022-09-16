/**
 * SchedaAnagraficaProfessionaleWSServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;

public class SchedaAnagraficaProfessionaleWSServiceSoapBindingStub extends org.apache.axis.client.Stub
		implements it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleWS {
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
		oper.setName("getListaSap");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		param.setOmittable(true);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"schedaAnagraficaProfessionaleHeader"));
		oper.setReturnClass(
				it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
						"MySapWsException"),
				"it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException", new javax.xml.namespace.QName(
						"http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "MySapWsException"),
				true));
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getSapUtenteDTO");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.Integer.class,
				false, false);
		param.setOmittable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg1"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		param.setOmittable(true);
		oper.addParameter(param);
		oper.setReturnType(
				new javax.xml.namespace.QName("http://sap.eng.it/xml/sap", "schedaAnagraficaProfessionaleDTO"));
		oper.setReturnClass(it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
						"MySapWsException"),
				"it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException", new javax.xml.namespace.QName(
						"http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "MySapWsException"),
				true));
		_operations[1] = oper;

	}

	public SchedaAnagraficaProfessionaleWSServiceSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public SchedaAnagraficaProfessionaleWSServiceSoapBindingStub(java.net.URL endpointURL,
			javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public SchedaAnagraficaProfessionaleWSServiceSoapBindingStub(javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"abstractSapDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.AbstractSapDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"MySapWsException");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapAlboDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlboDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapConoscenzeInfoDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfoDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaComuneDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComuneDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaOrarioDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrarioDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaProvinciaDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvinciaDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaRegioneDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegioneDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaStatoDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStatoDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaTurnoDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurnoDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapEsperienzaLavDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLavDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapFormazioneDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazioneDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapLinguaDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLinguaDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatenteDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatenteDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatentinoDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentinoDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPropensioneDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensioneDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapTitoloStudioDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudioDTO.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"schedaAnagraficaProfessionaleHeader");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://sap.eng.it/xml/sap", "schedaAnagraficaProfessionaleDTO");
		cachedSerQNames.add(qName);
		cls = it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO.class;
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

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader[] getListaSap(
			java.lang.String arg0)
			throws java.rmi.RemoteException, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "getListaSap"));

		// recupera le credenziali dal db
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		Credentials credentials = wsAuthUtils.getCredentials("MySapWS");
		if (credentials != null) {
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, credentials.getUsername());
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, credentials.getPassword());
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		java.lang.Object _resp = _call.invoke(new java.lang.Object[] { arg0 });

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException) _resp;
		} else {
			extractAttachments(_call);
			try {
				return (it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader[]) _resp;
			} catch (java.lang.Exception _exception) {
				return (it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader[]) org.apache.axis.utils.JavaUtils
						.convert(_resp,
								it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SchedaAnagraficaProfessionaleHeader[].class);
			}
		}
	}

	public it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO getSapUtenteDTO(java.lang.Integer arg0,
			java.lang.String arg1)
			throws java.rmi.RemoteException, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MySapWsException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "getSapUtenteDTO"));

		// recupera le credenziali dal db
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		Credentials credentials = wsAuthUtils.getCredentials("MySapWS");
		if (credentials != null) {
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, credentials.getUsername());
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, credentials.getPassword());
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		java.lang.Object _resp = _call.invoke(new java.lang.Object[] { arg0, arg1 });

		if (_resp instanceof java.rmi.RemoteException) {
			throw (java.rmi.RemoteException) _resp;
		} else {
			extractAttachments(_call);
			try {
				return (it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO) _resp;
			} catch (java.lang.Exception _exception) {
				return (it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO) org.apache.axis.utils.JavaUtils
						.convert(_resp, it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO.class);
			}
		}
	}

}
