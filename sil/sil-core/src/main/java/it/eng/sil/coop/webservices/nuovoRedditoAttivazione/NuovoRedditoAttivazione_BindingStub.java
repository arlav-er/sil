/**
 * NuovoRedditoAttivazione_BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.nuovoRedditoAttivazione;

import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;

public class NuovoRedditoAttivazione_BindingStub extends org.apache.axis.client.Stub
		implements it.eng.sil.coop.webservices.nuovoRedditoAttivazione.NuovoRedditoAttivazione_PortType {

	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[3];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("validazioneDomanda");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1", "validazioneDomandaNra"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
						"validazioneDomandaNraType"),
				it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ValidazioneDomandaNraType.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"esitoComunicazioneType"));
		oper.setReturnClass(
				it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"validazioneDomandaNraOutput"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("comunicazioneVariazioneResidenza");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
						"comunicazioneVariazioneResidenzaFuoriTrento"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
						"comunicazioneVariazioneResidenzaFuoriTrentoType"),
				it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioneVariazioneResidenzaFuoriTrentoType.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"esitoComunicazioneType"));
		oper.setReturnClass(
				it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"comunicazioneVariazioneResidenzaFuoriTrentoOutput"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("validazioneComunicazioniSuccessive");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
						"validazioneComunicazioniSuccessiveNra"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
						"comunicazioniSuccessiveNraType"),
				it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioniSuccessiveNraType.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"esitoComunicazioneType"));
		oper.setReturnClass(
				it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"validazioneComunicazioniSuccessiveNraOutput"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[2] = oper;

	}

	public NuovoRedditoAttivazione_BindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public NuovoRedditoAttivazione_BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public NuovoRedditoAttivazione_BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "belfioreType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "capType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"categoriaEsitoType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.CategoriaEsitoType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(enumsf);
		cachedDeserFactories.add(enumdf);

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"codiceFiscalePfType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"codiceProvinciaType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"datiRichiedenteType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.DatiRichiedenteType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"esitoComunicazioneType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"identificativoComunicazioneType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1", "indirizzoType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.IndirizzoType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"string100charMaxType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraCommon/0.0.1",
				"tipologiaEventoType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.TipologiaEventoType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(enumsf);
		cachedDeserFactories.add(enumdf);

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"comunicazioneVariazioneResidenzaFuoriTrentoType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioneVariazioneResidenzaFuoriTrentoType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"comunicazioniSuccessiveNraType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioniSuccessiveNraType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"esitoProvvedimentoType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.EsitoProvvedimentoType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(enumsf);
		cachedDeserFactories.add(enumdf);

		qName = new javax.xml.namespace.QName("http://www.inps.it/comunicazioniAsdiNraResponse/0.0.1",
				"validazioneDomandaNraType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ValidazioneDomandaNraType.class;
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

			// recupera le credenziali dal db
			WsAuthUtils wsAuthUtils = new WsAuthUtils();
			Credentials credentials = wsAuthUtils.getCredentials("NuovoRedditoAttivazione");
			if (credentials != null) {
				_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, credentials.getUsername());
				_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, credentials.getPassword());
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

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType validazioneDomanda(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ValidazioneDomandaNraType richiesta_RichiestaRispostaSincrona_validazioneDomanda)
			throws java.rmi.RemoteException {
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
		_call.setOperationName(new javax.xml.namespace.QName("", "validazioneDomanda"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call
					.invoke(new java.lang.Object[] { richiesta_RichiestaRispostaSincrona_validazioneDomanda });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType comunicazioneVariazioneResidenza(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioneVariazioneResidenzaFuoriTrentoType richiesta_RichiestaRispostaSincrona_comunicazioneVariazioneResidenza)
			throws java.rmi.RemoteException {
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
		_call.setOperationName(new javax.xml.namespace.QName("", "comunicazioneVariazioneResidenza"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(
					new java.lang.Object[] { richiesta_RichiestaRispostaSincrona_comunicazioneVariazioneResidenza });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType validazioneComunicazioniSuccessive(
			it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.response.ComunicazioniSuccessiveNraType richiesta_RichiestaRispostaSincrona_validazioneComunicazioniSuccessive)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "validazioneComunicazioniSuccessive"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(
					new java.lang.Object[] { richiesta_RichiestaRispostaSincrona_validazioneComunicazioniSuccessive });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									it.eng.sil.coop.webservices.nuovoRedditoAttivazione.patToInps.common.EsitoComunicazioneType.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}
}
