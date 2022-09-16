/**
 * AePullNotifyMOResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AePullNotifyMOResponse  extends com.tim.smashng.fe.ws.services.SmashAeResponse  implements java.io.Serializable {
    private boolean firstRequest;

    private com.tim.smashng.fe.ws.services.AeMessaggioRicevutoInfo[] smMOResponseList;

    public AePullNotifyMOResponse() {
    }

    public AePullNotifyMOResponse(
           int errorCode,
           java.lang.String errorDetail,
           boolean firstRequest,
           com.tim.smashng.fe.ws.services.AeMessaggioRicevutoInfo[] smMOResponseList) {
        super(
            errorCode,
            errorDetail);
        this.firstRequest = firstRequest;
        this.smMOResponseList = smMOResponseList;
    }


    /**
     * Gets the firstRequest value for this AePullNotifyMOResponse.
     * 
     * @return firstRequest
     */
    public boolean isFirstRequest() {
        return firstRequest;
    }


    /**
     * Sets the firstRequest value for this AePullNotifyMOResponse.
     * 
     * @param firstRequest
     */
    public void setFirstRequest(boolean firstRequest) {
        this.firstRequest = firstRequest;
    }


    /**
     * Gets the smMOResponseList value for this AePullNotifyMOResponse.
     * 
     * @return smMOResponseList
     */
    public com.tim.smashng.fe.ws.services.AeMessaggioRicevutoInfo[] getSmMOResponseList() {
        return smMOResponseList;
    }


    /**
     * Sets the smMOResponseList value for this AePullNotifyMOResponse.
     * 
     * @param smMOResponseList
     */
    public void setSmMOResponseList(com.tim.smashng.fe.ws.services.AeMessaggioRicevutoInfo[] smMOResponseList) {
        this.smMOResponseList = smMOResponseList;
    }

    public com.tim.smashng.fe.ws.services.AeMessaggioRicevutoInfo getSmMOResponseList(int i) {
        return this.smMOResponseList[i];
    }

    public void setSmMOResponseList(int i, com.tim.smashng.fe.ws.services.AeMessaggioRicevutoInfo _value) {
        this.smMOResponseList[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AePullNotifyMOResponse)) return false;
        AePullNotifyMOResponse other = (AePullNotifyMOResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.firstRequest == other.isFirstRequest() &&
            ((this.smMOResponseList==null && other.getSmMOResponseList()==null) || 
             (this.smMOResponseList!=null &&
              java.util.Arrays.equals(this.smMOResponseList, other.getSmMOResponseList())));
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
        if (getSmMOResponseList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSmMOResponseList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSmMOResponseList(), i);
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
        new org.apache.axis.description.TypeDesc(AePullNotifyMOResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePullNotifyMOResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("smMOResponseList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "smMOResponseList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeMessaggioRicevutoInfo"));
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
