/**
 * SanaMovimentoServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.sanatoria;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;

public class SanaMovimentoServiceLocator extends org.apache.axis.client.Service implements it.eng.sil.coop.webservices.sanatoria.SanaMovimentoService {

    public SanaMovimentoServiceLocator() {
    }


    public SanaMovimentoServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SanaMovimentoServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SanaMovimento
    private java.lang.String SanaMovimento_address = "";

    public java.lang.String getSanaMovimentoAddress() {
        return SanaMovimento_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SanaMovimentoWSDDServiceName = "SanaMovimento";

    public java.lang.String getSanaMovimentoWSDDServiceName() {
        return SanaMovimentoWSDDServiceName;
    }

    public void setSanaMovimentoWSDDServiceName(java.lang.String name) {
        SanaMovimentoWSDDServiceName = name;
    }

    public it.eng.sil.coop.webservices.sanatoria.SanaMovimento getSanaMovimento() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SanaMovimento_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSanaMovimento(endpoint);
    }

    public it.eng.sil.coop.webservices.sanatoria.SanaMovimento getSanaMovimento(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.eng.sil.coop.webservices.sanatoria.SanaMovimentoSoapBindingStub _stub = new it.eng.sil.coop.webservices.sanatoria.SanaMovimentoSoapBindingStub(portAddress, this);
            _stub.setPortName(getSanaMovimentoWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSanaMovimentoEndpointAddress(java.lang.String address) {
        SanaMovimento_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.eng.sil.coop.webservices.sanatoria.SanaMovimento.class.isAssignableFrom(serviceEndpointInterface)) {
                it.eng.sil.coop.webservices.sanatoria.SanaMovimentoSoapBindingStub _stub = new it.eng.sil.coop.webservices.sanatoria.SanaMovimentoSoapBindingStub(new java.net.URL(SanaMovimento_address), this);
                _stub.setPortName(getSanaMovimentoWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SanaMovimento".equals(inputPortName)) {
            return getSanaMovimento();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://sanatoria.webservices.coop.sil.eng.it", "SanaMovimentoService");
    }

    private HashSet<QName> ports = null;

    public Iterator<QName> getPorts() {
        if (ports == null) {
            ports = new HashSet<QName>();
            ports.add(new QName("http://sanatoria.webservices.coop.sil.eng.it", "SanaMovimento"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SanaMovimento".equals(portName)) {
            setSanaMovimentoEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
