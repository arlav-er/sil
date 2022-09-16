/**
 * SmashAe4EndpointPortBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class SmashAe4EndpointPortBindingStub extends org.apache.axis.client.Stub implements com.tim.smashng.fe.ws.services.SmashAe4Endpoint {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[10];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCreditoResiduo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetCreditoResiduoRequest"), com.tim.smashng.fe.ws.services.AeGetCreditoResiduoRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetCreditoResiduoResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AeGetCreditoResiduoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getInvio");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetInvioRequest"), com.tim.smashng.fe.ws.services.AeGetInvioRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetInvioResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AeGetInvioResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("inviaMessaggi");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiRequest"), com.tim.smashng.fe.ws.services.AeInviaMessaggiRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ricercaInvii");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaInviiRequest"), com.tim.smashng.fe.ws.services.AeRicercaInviiRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaInviiResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AeRicercaInviiResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ricercaMessaggiRicevuti");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaMessaggiRicevutiRequest"), com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaMessaggiRicevutiResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("pullNotify");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyRequest"), com.tim.smashng.fe.ws.services.AePullNotifyRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AePullNotifyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("pullNotifyMO");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyRequest"), com.tim.smashng.fe.ws.services.AePullNotifyRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyMOResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AePullNotifyMOResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("inviaMessaggiMittenteTestuale");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiERequest"), com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("inviaMessaggiMittenteNumerico");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiERequest"), com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiResponse"));
        oper.setReturnClass(com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVersion");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    public SmashAe4EndpointPortBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public SmashAe4EndpointPortBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public SmashAe4EndpointPortBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeCreditoResiduoInfo");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AePacchettoInfo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePacchettoInfo");
            qName2 = new javax.xml.namespace.QName("", "pacchetti");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeCreditoResiduoOnOff");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeCreditoResiduoOnOff.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeCreditoResiduoUnico");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeCreditoResiduoUnico.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGenericFilter");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeGenericFilter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetCreditoResiduoRequest");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeGetCreditoResiduoRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetCreditoResiduoResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeGetCreditoResiduoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetInvioRequest");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeGetInvioRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetInvioResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeGetInvioResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiERequest");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiRequest");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeInviaMessaggiRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInvioInfo");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeInvioInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeMessaggioInviatoInfo");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeMessaggioInviatoInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeMessaggioRicevutoInfo");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeMessaggioRicevutoInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePacchettoInfo");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AePacchettoInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyMOResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AePullNotifyMOResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyRequest");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AePullNotifyRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AePullNotifyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaInviiRequest");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeRicercaInviiRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaInviiResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeRicercaInviiResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaMessaggiRicevutiRequest");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaMessaggiRicevutiResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeSmMessaggioResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.AeSmMessaggioResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "smashAeProtocol");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.SmashAeProtocol.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "smashAeRequest");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.SmashAeRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "smashAeResponse");
            cachedSerQNames.add(qName);
            cls = com.tim.smashng.fe.ws.services.SmashAeResponse.class;
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
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
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

    public com.tim.smashng.fe.ws.services.AeGetCreditoResiduoResponse getCreditoResiduo(com.tim.smashng.fe.ws.services.AeGetCreditoResiduoRequest arg0) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "getCreditoResiduo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AeGetCreditoResiduoResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AeGetCreditoResiduoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AeGetCreditoResiduoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.tim.smashng.fe.ws.services.AeGetInvioResponse getInvio(com.tim.smashng.fe.ws.services.AeGetInvioRequest arg0) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "getInvio"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AeGetInvioResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AeGetInvioResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AeGetInvioResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggi(com.tim.smashng.fe.ws.services.AeInviaMessaggiRequest arg0) throws java.rmi.RemoteException {
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
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "inviaMessaggi"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.tim.smashng.fe.ws.services.AeRicercaInviiResponse ricercaInvii(com.tim.smashng.fe.ws.services.AeRicercaInviiRequest arg0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "ricercaInvii"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AeRicercaInviiResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AeRicercaInviiResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AeRicercaInviiResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiResponse ricercaMessaggiRicevuti(com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiRequest arg0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "ricercaMessaggiRicevuti"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AeRicercaMessaggiRicevutiResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.tim.smashng.fe.ws.services.AePullNotifyResponse pullNotify(com.tim.smashng.fe.ws.services.AePullNotifyRequest arg0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "pullNotify"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AePullNotifyResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AePullNotifyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AePullNotifyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.tim.smashng.fe.ws.services.AePullNotifyMOResponse pullNotifyMO(com.tim.smashng.fe.ws.services.AePullNotifyRequest arg0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "pullNotifyMO"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AePullNotifyMOResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AePullNotifyMOResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AePullNotifyMOResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggiMittenteTestuale(com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest arg0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "inviaMessaggiMittenteTestuale"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse inviaMessaggiMittenteNumerico(com.tim.smashng.fe.ws.services.AeInviaMessaggiERequest arg0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "inviaMessaggiMittenteNumerico"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.tim.smashng.fe.ws.services.AeInviaMessaggiResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String getVersion() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "getVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
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

}
