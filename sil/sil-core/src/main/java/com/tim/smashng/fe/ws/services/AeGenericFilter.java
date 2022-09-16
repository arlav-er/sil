/**
 * AeGenericFilter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeGenericFilter  implements java.io.Serializable {
    private java.lang.String dataFinale;

    private java.lang.String dataIniziale;

    private java.lang.Integer maxRecordRichiesti;

    private java.lang.Integer numeroRecordIniziale;

    public AeGenericFilter() {
    }

    public AeGenericFilter(
           java.lang.String dataFinale,
           java.lang.String dataIniziale,
           java.lang.Integer maxRecordRichiesti,
           java.lang.Integer numeroRecordIniziale) {
           this.dataFinale = dataFinale;
           this.dataIniziale = dataIniziale;
           this.maxRecordRichiesti = maxRecordRichiesti;
           this.numeroRecordIniziale = numeroRecordIniziale;
    }


    /**
     * Gets the dataFinale value for this AeGenericFilter.
     * 
     * @return dataFinale
     */
    public java.lang.String getDataFinale() {
        return dataFinale;
    }


    /**
     * Sets the dataFinale value for this AeGenericFilter.
     * 
     * @param dataFinale
     */
    public void setDataFinale(java.lang.String dataFinale) {
        this.dataFinale = dataFinale;
    }


    /**
     * Gets the dataIniziale value for this AeGenericFilter.
     * 
     * @return dataIniziale
     */
    public java.lang.String getDataIniziale() {
        return dataIniziale;
    }


    /**
     * Sets the dataIniziale value for this AeGenericFilter.
     * 
     * @param dataIniziale
     */
    public void setDataIniziale(java.lang.String dataIniziale) {
        this.dataIniziale = dataIniziale;
    }


    /**
     * Gets the maxRecordRichiesti value for this AeGenericFilter.
     * 
     * @return maxRecordRichiesti
     */
    public java.lang.Integer getMaxRecordRichiesti() {
        return maxRecordRichiesti;
    }


    /**
     * Sets the maxRecordRichiesti value for this AeGenericFilter.
     * 
     * @param maxRecordRichiesti
     */
    public void setMaxRecordRichiesti(java.lang.Integer maxRecordRichiesti) {
        this.maxRecordRichiesti = maxRecordRichiesti;
    }


    /**
     * Gets the numeroRecordIniziale value for this AeGenericFilter.
     * 
     * @return numeroRecordIniziale
     */
    public java.lang.Integer getNumeroRecordIniziale() {
        return numeroRecordIniziale;
    }


    /**
     * Sets the numeroRecordIniziale value for this AeGenericFilter.
     * 
     * @param numeroRecordIniziale
     */
    public void setNumeroRecordIniziale(java.lang.Integer numeroRecordIniziale) {
        this.numeroRecordIniziale = numeroRecordIniziale;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeGenericFilter)) return false;
        AeGenericFilter other = (AeGenericFilter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataFinale==null && other.getDataFinale()==null) || 
             (this.dataFinale!=null &&
              this.dataFinale.equals(other.getDataFinale()))) &&
            ((this.dataIniziale==null && other.getDataIniziale()==null) || 
             (this.dataIniziale!=null &&
              this.dataIniziale.equals(other.getDataIniziale()))) &&
            ((this.maxRecordRichiesti==null && other.getMaxRecordRichiesti()==null) || 
             (this.maxRecordRichiesti!=null &&
              this.maxRecordRichiesti.equals(other.getMaxRecordRichiesti()))) &&
            ((this.numeroRecordIniziale==null && other.getNumeroRecordIniziale()==null) || 
             (this.numeroRecordIniziale!=null &&
              this.numeroRecordIniziale.equals(other.getNumeroRecordIniziale())));
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
        if (getDataFinale() != null) {
            _hashCode += getDataFinale().hashCode();
        }
        if (getDataIniziale() != null) {
            _hashCode += getDataIniziale().hashCode();
        }
        if (getMaxRecordRichiesti() != null) {
            _hashCode += getMaxRecordRichiesti().hashCode();
        }
        if (getNumeroRecordIniziale() != null) {
            _hashCode += getNumeroRecordIniziale().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeGenericFilter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGenericFilter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataFinale");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataFinale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataIniziale");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataIniziale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxRecordRichiesti");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maxRecordRichiesti"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroRecordIniziale");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroRecordIniziale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
