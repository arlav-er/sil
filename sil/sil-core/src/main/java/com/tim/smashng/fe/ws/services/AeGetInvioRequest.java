/**
 * AeGetInvioRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeGetInvioRequest  extends com.tim.smashng.fe.ws.services.SmashAeRequest  implements java.io.Serializable {
    private java.lang.Long idInvio;

    public AeGetInvioRequest() {
    }

    public AeGetInvioRequest(
           java.lang.String codiceContratto,
           java.lang.String password,
           java.lang.String username,
           java.lang.Long idInvio) {
        super(
            codiceContratto,
            password,
            username);
        this.idInvio = idInvio;
    }


    /**
     * Gets the idInvio value for this AeGetInvioRequest.
     * 
     * @return idInvio
     */
    public java.lang.Long getIdInvio() {
        return idInvio;
    }


    /**
     * Sets the idInvio value for this AeGetInvioRequest.
     * 
     * @param idInvio
     */
    public void setIdInvio(java.lang.Long idInvio) {
        this.idInvio = idInvio;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeGetInvioRequest)) return false;
        AeGetInvioRequest other = (AeGetInvioRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.idInvio==null && other.getIdInvio()==null) || 
             (this.idInvio!=null &&
              this.idInvio.equals(other.getIdInvio())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getIdInvio() != null) {
            _hashCode += getIdInvio().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeGetInvioRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetInvioRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idInvio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "idInvio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
