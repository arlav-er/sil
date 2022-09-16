/**
 * NumSMS_type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.infotn.www.SMS_EAI.InvioSMS_types;

public class NumSMS_type  implements java.io.Serializable {
    private int numeroSMS;

    public NumSMS_type() {
    }

    public NumSMS_type(
           int numeroSMS) {
           this.numeroSMS = numeroSMS;
    }


    /**
     * Gets the numeroSMS value for this NumSMS_type.
     * 
     * @return numeroSMS
     */
    public int getNumeroSMS() {
        return numeroSMS;
    }


    /**
     * Sets the numeroSMS value for this NumSMS_type.
     * 
     * @param numeroSMS
     */
    public void setNumeroSMS(int numeroSMS) {
        this.numeroSMS = numeroSMS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NumSMS_type)) return false;
        NumSMS_type other = (NumSMS_type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.numeroSMS == other.getNumeroSMS();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getNumeroSMS();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NumSMS_type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "numSMS_type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroSMS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "numeroSMS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
