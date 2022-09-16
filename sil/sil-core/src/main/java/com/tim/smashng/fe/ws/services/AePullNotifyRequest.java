/**
 * AePullNotifyRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AePullNotifyRequest  extends com.tim.smashng.fe.ws.services.SmashAeRequest  implements java.io.Serializable {
    private java.lang.Integer maxResult;

    private java.lang.String trId;

    public AePullNotifyRequest() {
    }

    public AePullNotifyRequest(
           java.lang.String codiceContratto,
           java.lang.String password,
           java.lang.String username,
           java.lang.Integer maxResult,
           java.lang.String trId) {
        super(
            codiceContratto,
            password,
            username);
        this.maxResult = maxResult;
        this.trId = trId;
    }


    /**
     * Gets the maxResult value for this AePullNotifyRequest.
     * 
     * @return maxResult
     */
    public java.lang.Integer getMaxResult() {
        return maxResult;
    }


    /**
     * Sets the maxResult value for this AePullNotifyRequest.
     * 
     * @param maxResult
     */
    public void setMaxResult(java.lang.Integer maxResult) {
        this.maxResult = maxResult;
    }


    /**
     * Gets the trId value for this AePullNotifyRequest.
     * 
     * @return trId
     */
    public java.lang.String getTrId() {
        return trId;
    }


    /**
     * Sets the trId value for this AePullNotifyRequest.
     * 
     * @param trId
     */
    public void setTrId(java.lang.String trId) {
        this.trId = trId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AePullNotifyRequest)) return false;
        AePullNotifyRequest other = (AePullNotifyRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.maxResult==null && other.getMaxResult()==null) || 
             (this.maxResult!=null &&
              this.maxResult.equals(other.getMaxResult()))) &&
            ((this.trId==null && other.getTrId()==null) || 
             (this.trId!=null &&
              this.trId.equals(other.getTrId())));
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
        if (getMaxResult() != null) {
            _hashCode += getMaxResult().hashCode();
        }
        if (getTrId() != null) {
            _hashCode += getTrId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AePullNotifyRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maxResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "trId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
