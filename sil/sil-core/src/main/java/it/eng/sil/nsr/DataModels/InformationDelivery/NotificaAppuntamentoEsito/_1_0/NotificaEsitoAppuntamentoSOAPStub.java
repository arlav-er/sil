/**
 * NotificaEsitoAppuntamentoSOAPStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0;

import org.apache.axis.message.SOAPHeaderElement;

import com.engiweb.framework.base.RequestContainer;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;

public class NotificaEsitoAppuntamentoSOAPStub extends org.apache.axis.client.Stub implements
		it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NotificaEsitoAppuntamentoType {
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
		oper.setName("notifica");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName(
						"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
						"RichiestaNotificaEsito"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
						"RichiestaNotificaEsitoType"),
				it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RichiestaNotificaEsitoType.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"RispostaNotificaEsitoType"));
		oper.setReturnClass(
				it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType.class);
		oper.setReturnQName(new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"RispostaNotificaEsito"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

	}

	public NotificaEsitoAppuntamentoSOAPStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public NotificaEsitoAppuntamentoSOAPStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public NotificaEsitoAppuntamentoSOAPStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"CodiceIntermediarioType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"DataOraAppuntamentoType");
		cachedSerQNames.add(qName);
		cls = java.util.Calendar.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"DescrizioneEsitoNegativoType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"DescrizioneSportelloType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdAppuntamentoARType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdAppuntamentoType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0", "IdCausaType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdEsitoAppuntamentoType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0", "IdEsitoType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"IdSportelloType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"NoteSedeRegioneType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0", "NoteType");
		cachedSerQNames.add(qName);
		cls = java.lang.String.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
		cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory
				.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"NuovoAppuntamentoType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.NuovoAppuntamentoType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"RichiestaNotificaEsitoType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RichiestaNotificaEsitoType.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName(
				"http://nsr.sil.eng.it/DataModels/InformationDelivery/NotificaAppuntamentoEsito/1.0",
				"RispostaNotificaEsitoType");
		cachedSerQNames.add(qName);
		cls = it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType.class;
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

	public it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType notifica(
			it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RichiestaNotificaEsitoType richiesta)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("http://soap.nsr.sil.eng.it/NotificaEsitoAppuntamento/notifica");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "notifica"));

		// leggo un attribute sul request container per customizzare l'handler
		RequestContainer requestContainer = RequestContainer.getRequestContainer();
		SOAPHeaderElement head = new SOAPHeaderElement(new javax.xml.namespace.QName("", "tipo_log"));
		head.setName(MessageCodes.AGENDA_ANAPL.HANDLER);
		if (requestContainer != null) {
			Object value = requestContainer.getAttribute(MessageCodes.AGENDA_ANAPL.HANDLER);
			if (value != null) {

				String valueStr = (String) value;
				if (StringUtils.isFilledNoBlank(valueStr)
						&& valueStr.equalsIgnoreCase(MessageCodes.AGENDA_ANAPL.HANDLER)) {
					// _call.getMessageContext().setProperty("LOG_HANDLER", "FULL");
					head.setValue("FULL");
				} else {
					// _call.getMessageContext().setProperty("LOG_HANDLER", "PARTIAL");
					head.setValue("PARTIAL");
				}
			} else {
				// _call.getMessageContext().setProperty("LOG_HANDLER", "PARTIAL");
				head.setValue("PARTIAL");
			}
		} else {
			head.setValue("PARTIAL");
		}

		setHeader(head);
		setRequestHeaders(_call);
		setAttachments(_call);

		try {
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { richiesta });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType) _resp;
				} catch (java.lang.Exception _exception) {
					return (it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									it.eng.sil.nsr.DataModels.InformationDelivery.NotificaAppuntamentoEsito._1_0.RispostaNotificaEsitoType.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
