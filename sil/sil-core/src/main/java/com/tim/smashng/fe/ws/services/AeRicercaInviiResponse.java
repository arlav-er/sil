/**
 * AeRicercaInviiResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeRicercaInviiResponse  extends com.tim.smashng.fe.ws.services.SmashAeResponse  implements java.io.Serializable {
    private com.tim.smashng.fe.ws.services.AeInvioInfo[] info;

    private java.lang.Long totaleRecordTrovati;

    public AeRicercaInviiResponse() {
    }

    public AeRicercaInviiResponse(
           int errorCode,
           java.lang.String errorDetail,
           com.tim.smashng.fe.ws.services.AeInvioInfo[] info,
           java.lang.Long totaleRecordTrovati) {
        super(
            errorCode,
            errorDetail);
        this.info = info;
        this.totaleRecordTrovati = totaleRecordTrovati;
    }


    /**
     * Gets the info value for this AeRicercaInviiResponse.
     * 
     * @return info
     */
    public com.tim.smashng.fe.ws.services.AeInvioInfo[] getInfo() {
        return info;
    }


    /**
     * Sets the info value for this AeRicercaInviiResponse.
     * 
     * @param info
     */
    public void setInfo(com.tim.smashng.fe.ws.services.AeInvioInfo[] info) {
        this.info = info;
    }

    public com.tim.smashng.fe.ws.services.AeInvioInfo getInfo(int i) {
        return this.info[i];
    }

    public void setInfo(int i, com.tim.smashng.fe.ws.services.AeInvioInfo _value) {
        this.info[i] = _value;
    }


    /**
     * Gets the totaleRecordTrovati value for this AeRicercaInviiResponse.
     * 
     * @return totaleRecordTrovati
     */
    public java.lang.Long getTotaleRecordTrovati() {
        return totaleRecordTrovati;
    }


    /**
     * Sets the totaleRecordTrovati value for this AeRicercaInviiResponse.
     * 
     * @param totaleRecordTrovati
     */
    public void setTotaleRecordTrovati(java.lang.Long totaleRecordTrovati) {
        this.totaleRecordTrovati = totaleRecordTrovati;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeRicercaInviiResponse)) return false;
        AeRicercaInviiResponse other = (AeRicercaInviiResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.info==null && other.getInfo()==null) || 
             (this.info!=null &&
              java.util.Arrays.equals(this.info, other.getInfo()))) &&
            ((this.totaleRecordTrovati==null && other.getTotaleRecordTrovati()==null) || 
             (this.totaleRecordTrovati!=null &&
              this.totaleRecordTrovati.equals(other.getTotaleRecordTrovati())));
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
        if (getInfo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInfo());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInfo(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTotaleRecordTrovati() != null) {
            _hashCode += getTotaleRecordTrovati().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeRicercaInviiResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaInviiResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("info");
        elemField.setXmlName(new javax.xml.namespace.QName("", "info"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInvioInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totaleRecordTrovati");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totaleRecordTrovati"));
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
