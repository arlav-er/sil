/**
 * CliclavoroWSSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.cliclavoro;

import it.eng.myportal.utils.ConstantsSingleton;


public class CliclavoroWSSoapBindingStub extends org.apache.axis.client.Stub implements it.gov.lavoro.servizi.cliclavoro.CliclavoroWS {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[3];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("InvioMessaggio");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "invioMessaggio"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "richiesta_invioMessaggio_Type"), it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioMessaggio_Type.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioMessaggio_Type"));
        oper.setReturnClass(it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioMessaggio"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("InvioVacancy");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "invioVacancy"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "richiesta_invioVacancy_Type"), it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioVacancy_Type.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioVacancy_Type"));
        oper.setReturnClass(it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioVacancy"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("InvioCandidatura");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "invioCandidatura"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "richiesta_invioCandidatura_Type"), it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioCandidatura_Type.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioCandidatura_Type"));
        oper.setReturnClass(it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioCandidatura"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

    }

    public CliclavoroWSSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public CliclavoroWSSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public CliclavoroWSSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
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
            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", ">risposta_invioCandidatura_Type>Tipo_Risposta");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_TypeTipo_Risposta.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", ">risposta_invioMessaggio_Type>Tipo_Risposta");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_TypeTipo_Risposta.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", ">risposta_invioVacancy_Type>Tipo_Risposta");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_TypeTipo_Risposta.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "richiesta_invioCandidatura_Type");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioCandidatura_Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "richiesta_invioMessaggio_Type");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioMessaggio_Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "richiesta_invioVacancy_Type");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioVacancy_Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioCandidatura_Type");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioMessaggio_Type");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioVacancy_Type");
            cachedSerQNames.add(qName);
            cls = it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type.class;
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
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class<?> sf = (java.lang.Class<?>)
                                 cachedSerFactories.get(i);
                            java.lang.Class<?> df = (java.lang.Class<?>)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type invioMessaggio(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioMessaggio_Type parameters) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("", "InvioMessaggio"));

        String abilitata = ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_ABILITATA;
        if ("Y".equalsIgnoreCase(abilitata)) {       
        	_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_USERNAME);
        	_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_PASSWORD);        
        }
        
        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type) org.apache.axis.utils.JavaUtils.convert(_resp, it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_Type.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type invioVacancy(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioVacancy_Type parameters) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("", "InvioVacancy"));

        String abilitata = ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_ABILITATA;
        if ("Y".equalsIgnoreCase(abilitata)) {       
        	_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_USERNAME);
        	_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_PASSWORD);        
        }
        
        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type) org.apache.axis.utils.JavaUtils.convert(_resp, it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type invioCandidatura(it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioCandidatura_Type parameters) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("", "InvioCandidatura"));

        String abilitata = ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_ABILITATA;
        if ("Y".equalsIgnoreCase(abilitata)) {       
        	_call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_USERNAME);
        	_call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, ConstantsSingleton.BasicAuthPDD.BASIC_AUTH_PASSWORD);        
        }
        
        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type) _resp;
            } catch (java.lang.Exception _exception) {
                return (it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type) org.apache.axis.utils.JavaUtils.convert(_resp, it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
