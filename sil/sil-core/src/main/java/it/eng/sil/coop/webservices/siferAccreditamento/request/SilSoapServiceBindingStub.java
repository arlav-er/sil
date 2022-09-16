/**
 * SilSoapServiceBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.siferAccreditamento.request;

import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.WsAuthUtils;

public class SilSoapServiceBindingStub extends org.apache.axis.client.Stub
		implements it.eng.sil.coop.webservices.siferAccreditamento.request.SilSoapServicePort {
	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[1];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("request");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "partecipante"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
						"Partecipante"),
				it.eng.sil.coop.webservices.siferAccreditamento.request.Partecipante.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "patti"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
						"ArrayOfPatto"),
				it.eng.sil.coop.webservices.siferAccreditamento.request.Patto[].class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
		oper.setReturnClass(java.lang.Object.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		_operations[0] = oper;

	}

	public SilSoapServiceBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public SilSoapServiceBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public SilSoapServiceBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"ArrayOfPatto");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.siferAccreditamento.request.Patto[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante", "Patto");
		qName2 = null;
		cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"ArrayOfPoliticaAttiva");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"PoliticaAttiva");
		qName2 = null;
		cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"ArrayOfSvantaggio");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"Svantaggio");
		qName2 = null;
		cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"Partecipante");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.siferAccreditamento.request.Partecipante.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante", "Patto");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.siferAccreditamento.request.Patto.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"PoliticaAttiva");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"Svantaggio");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio.class;
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
					_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
					_call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
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

	public java.lang.Object request(it.eng.sil.coop.webservices.siferAccreditamento.request.Partecipante partecipante,
			it.eng.sil.coop.webservices.siferAccreditamento.request.Patto[] patti) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("http://sifer.local/app_dev.php/WebService/sil/partecipante#request");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(
				new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante", "request"));

		// recupera le credenziali dal db
		WsAuthUtils wsAuthUtils = new WsAuthUtils();
		Credentials credentials = wsAuthUtils.getCredentials("SIFER_ACCREDITAMENTO");
		if (credentials != null) {
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, credentials.getUsername());
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, credentials.getPassword());
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { partecipante, patti });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (java.lang.Object) _resp;
				} catch (java.lang.Exception _exception) {
					return (java.lang.Object) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.Object.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
