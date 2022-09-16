/**
 * AeGetCreditoResiduoRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeGetCreditoResiduoRequest  extends com.tim.smashng.fe.ws.services.SmashAeRequest  implements java.io.Serializable {
    private boolean includiPacchetti;

    public AeGetCreditoResiduoRequest() {
    }

    public AeGetCreditoResiduoRequest(
           java.lang.String codiceContratto,
           java.lang.String password,
           java.lang.String username,
           boolean includiPacchetti) {
        super(
            codiceContratto,
            password,
            username);
        this.includiPacchetti = includiPacchetti;
    }


    /**
     * Gets the includiPacchetti value for this AeGetCreditoResiduoRequest.
     * 
     * @return includiPacchetti
     */
    public boolean isIncludiPacchetti() {
        return includiPacchetti;
    }


    /**
     * Sets the includiPacchetti value for this AeGetCreditoResiduoRequest.
     * 
     * @param includiPacchetti
     */
    public void setIncludiPacchetti(boolean includiPacchetti) {
        this.includiPacchetti = includiPacchetti;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeGetCreditoResiduoRequest)) return false;
        AeGetCreditoResiduoRequest other = (AeGetCreditoResiduoRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.includiPacchetti == other.isIncludiPacchetti();
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
        _hashCode += (isIncludiPacchetti() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeGetCreditoResiduoRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGetCreditoResiduoRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includiPacchetti");
        elemField.setXmlName(new javax.xml.namespace.QName("", "includiPacchetti"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
