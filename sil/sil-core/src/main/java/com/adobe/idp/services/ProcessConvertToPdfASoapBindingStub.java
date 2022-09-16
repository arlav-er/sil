/**
 * ProcessConvertToPdfASoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;

public class ProcessConvertToPdfASoapBindingStub extends org.apache.axis.client.Stub
		implements com.adobe.idp.services.SupportPDFG_ProcessConvertToPdfA {
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
		oper.setName("invoke");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://adobe.com/idp/services", "inDoc"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://adobe.com/idp/services", "BLOB"),
				com.adobe.idp.services.BLOB.class, false, false);
		param.setOmittable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://adobe.com/idp/services", "in_bool_enableValidationPdfA"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), java.lang.Boolean.class,
				false, false);
		param.setOmittable(true);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://adobe.com/idp/services", "isPDFA"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false,
				false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://adobe.com/idp/services", "outDoc"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://adobe.com/idp/services", "BLOB"),
				com.adobe.idp.services.BLOB.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://adobe.com/idp/services", "out_xml_output"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://adobe.com/idp/services", "XML"), com.adobe.idp.services.XML.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

	}

	public ProcessConvertToPdfASoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public ProcessConvertToPdfASoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public ProcessConvertToPdfASoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName("http://adobe.com/idp/services", "BLOB");
		cachedSerQNames.add(qName);
		cls = com.adobe.idp.services.BLOB.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://adobe.com/idp/services", "XML");
		cachedSerQNames.add(qName);
		cls = com.adobe.idp.services.XML.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	private Credentials getwsAuthUtils(String wsName) {

		Credentials creden = null;

		Object[] inputParameters = new Object[1]; // END_POINT_NAME
		inputParameters[0] = wsName;
		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("GET_WS_CREDENTIALS", inputParameters, "SELECT",
				Values.DB_SIL_DATI);

		String username = (String) ret.getAttribute("ROW.struserid");
		String password = (String) ret.getAttribute("ROW.CLN_PWD");

		creden = new Credentials(username, password);

		return creden;

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

	public void invoke(com.adobe.idp.services.BLOB inDoc, java.lang.Boolean in_bool_enableValidationPdfA,
			javax.xml.rpc.holders.BooleanHolder isPDFA, com.adobe.idp.services.holders.BLOBHolder outDoc,
			com.adobe.idp.services.holders.XMLHolder out_xml_output) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("invoke");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://adobe.com/idp/services", "invoke"));

		// recupera le credenziali dal db
		// WsAuthUtils wsAuthUtils = new WsAuthUtils();
		Credentials credentials = getwsAuthUtils("SPIL_FIRMA");
		if (credentials != null) {
			_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, credentials.getUsername());
			_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, credentials.getPassword());
		}

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { inDoc, in_bool_enableValidationPdfA });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					isPDFA.value = ((java.lang.Boolean) _output
							.get(new javax.xml.namespace.QName("http://adobe.com/idp/services", "isPDFA")))
									.booleanValue();
				} catch (java.lang.Exception _exception) {
					isPDFA.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(
							_output.get(new javax.xml.namespace.QName("http://adobe.com/idp/services", "isPDFA")),
							boolean.class)).booleanValue();
				}
				try {
					outDoc.value = (com.adobe.idp.services.BLOB) _output
							.get(new javax.xml.namespace.QName("http://adobe.com/idp/services", "outDoc"));
				} catch (java.lang.Exception _exception) {
					outDoc.value = (com.adobe.idp.services.BLOB) org.apache.axis.utils.JavaUtils.convert(
							_output.get(new javax.xml.namespace.QName("http://adobe.com/idp/services", "outDoc")),
							com.adobe.idp.services.BLOB.class);
				}
				try {
					out_xml_output.value = (com.adobe.idp.services.XML) _output
							.get(new javax.xml.namespace.QName("http://adobe.com/idp/services", "out_xml_output"));
				} catch (java.lang.Exception _exception) {
					out_xml_output.value = (com.adobe.idp.services.XML) org.apache.axis.utils.JavaUtils.convert(
							_output.get(
									new javax.xml.namespace.QName("http://adobe.com/idp/services", "out_xml_output")),
							com.adobe.idp.services.XML.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
