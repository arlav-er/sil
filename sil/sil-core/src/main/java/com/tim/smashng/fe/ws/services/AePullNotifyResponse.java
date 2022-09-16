/**
 * AePullNotifyResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AePullNotifyResponse  extends com.tim.smashng.fe.ws.services.SmashAeResponse  implements java.io.Serializable {
    private boolean firstRequest;

    private com.tim.smashng.fe.ws.services.AeSmMessaggioResponse[] smMessaggioResponseList;

    public AePullNotifyResponse() {
    }

    public AePullNotifyResponse(
           int errorCode,
           java.lang.String errorDetail,
           boolean firstRequest,
           com.tim.smashng.fe.ws.services.AeSmMessaggioResponse[] smMessaggioResponseList) {
        super(
            errorCode,
            errorDetail);
        this.firstRequest = firstRequest;
        this.smMessaggioResponseList = smMessaggioResponseList;
    }


    /**
     * Gets the firstRequest value for this AePullNotifyResponse.
     * 
     * @return firstRequest
     */
    public boolean isFirstRequest() {
        return firstRequest;
    }


    /**
     * Sets the firstRequest value for this AePullNotifyResponse.
     * 
     * @param firstRequest
     */
    public void setFirstRequest(boolean firstRequest) {
        this.firstRequest = firstRequest;
    }


    /**
     * Gets the smMessaggioResponseList value for this AePullNotifyResponse.
     * 
     * @return smMessaggioResponseList
     */
    public com.tim.smashng.fe.ws.services.AeSmMessaggioResponse[] getSmMessaggioResponseList() {
        return smMessaggioResponseList;
    }


    /**
     * Sets the smMessaggioResponseList value for this AePullNotifyResponse.
     * 
     * @param smMessaggioResponseList
     */
    public void setSmMessaggioResponseList(com.tim.smashng.fe.ws.services.AeSmMessaggioResponse[] smMessaggioResponseList) {
        this.smMessaggioResponseList = smMessaggioResponseList;
    }

    public com.tim.smashng.fe.ws.services.AeSmMessaggioResponse getSmMessaggioResponseList(int i) {
        return this.smMessaggioResponseList[i];
    }

    public void setSmMessaggioResponseList(int i, com.tim.smashng.fe.ws.services.AeSmMessaggioResponse _value) {
        this.smMessaggioResponseList[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AePullNotifyResponse)) return false;
        AePullNotifyResponse other = (AePullNotifyResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.firstRequest == other.isFirstRequest() &&
            ((this.smMessaggioResponseList==null && other.getSmMessaggioResponseList()==null) || 
             (this.smMessaggioResponseList!=null &&
              java.util.Arrays.equals(this.smMessaggioResponseList, other.getSmMessaggioResponseList())));
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
        _hashCode += (isFirstRequest() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getSmMessaggioResponseList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSmMessaggioResponseList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSmMessaggioResponseList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AePullNotifyResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("smMessaggioResponseList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "smMessaggioResponseList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeSmMessaggioResponse"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
