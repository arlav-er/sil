/**
 * SetStatoAdesioneYGSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.coapadesioneSet.portale;

public class SetStatoAdesioneYGSoapBindingStub extends org.apache.axis.client.Stub
		implements it.eng.sil.coop.webservices.coapadesioneSet.portale.ServizicoapWS {
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
		oper.setName("SetStatoAdesioneYG");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "DatiStatoAdesione"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "Esito"),
				org.apache.axis.description.ParameterDesc.OUT,
				new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class,
				false, false);
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
		_operations[0] = oper;

	}

	public SetStatoAdesioneYGSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public SetStatoAdesioneYGSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public SetStatoAdesioneYGSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		if (service == null) {
			super.service = new org.apache.axis.client.Service();
		} else {
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
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
			return _call;
		} catch (java.lang.Throwable _t) {
			throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
		}
	}

	public void setStatoAdesioneYG(java.lang.String datiStatoAdesione, javax.xml.rpc.holders.StringHolder esito,
			javax.xml.rpc.holders.StringHolder messaggioErrore) throws java.rmi.RemoteException {
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
		_call.setOperationName(
				new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "SetStatoAdesioneYG"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { datiStatoAdesione });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				java.util.Map _output;
				_output = _call.getOutputParams();
				try {
					esito.value = (java.lang.String) _output.get(
							new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "Esito"));
				} catch (java.lang.Exception _exception) {
					esito.value = (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_output.get(
							new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/servizicoap/types", "Esito")),
							java.lang.String.class);
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
