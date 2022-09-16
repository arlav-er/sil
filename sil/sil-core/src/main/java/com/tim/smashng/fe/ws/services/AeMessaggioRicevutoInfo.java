/**
 * AeMessaggioRicevutoInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeMessaggioRicevutoInfo  implements java.io.Serializable {
    private java.lang.String dataRicezione;

    private java.lang.String mittente;

    private java.lang.String testo;

    private java.lang.String tipoMessaggio;

    public AeMessaggioRicevutoInfo() {
    }

    public AeMessaggioRicevutoInfo(
           java.lang.String dataRicezione,
           java.lang.String mittente,
           java.lang.String testo,
           java.lang.String tipoMessaggio) {
           this.dataRicezione = dataRicezione;
           this.mittente = mittente;
           this.testo = testo;
           this.tipoMessaggio = tipoMessaggio;
    }


    /**
     * Gets the dataRicezione value for this AeMessaggioRicevutoInfo.
     * 
     * @return dataRicezione
     */
    public java.lang.String getDataRicezione() {
        return dataRicezione;
    }


    /**
     * Sets the dataRicezione value for this AeMessaggioRicevutoInfo.
     * 
     * @param dataRicezione
     */
    public void setDataRicezione(java.lang.String dataRicezione) {
        this.dataRicezione = dataRicezione;
    }


    /**
     * Gets the mittente value for this AeMessaggioRicevutoInfo.
     * 
     * @return mittente
     */
    public java.lang.String getMittente() {
        return mittente;
    }


    /**
     * Sets the mittente value for this AeMessaggioRicevutoInfo.
     * 
     * @param mittente
     */
    public void setMittente(java.lang.String mittente) {
        this.mittente = mittente;
    }


    /**
     * Gets the testo value for this AeMessaggioRicevutoInfo.
     * 
     * @return testo
     */
    public java.lang.String getTesto() {
        return testo;
    }


    /**
     * Sets the testo value for this AeMessaggioRicevutoInfo.
     * 
     * @param testo
     */
    public void setTesto(java.lang.String testo) {
        this.testo = testo;
    }


    /**
     * Gets the tipoMessaggio value for this AeMessaggioRicevutoInfo.
     * 
     * @return tipoMessaggio
     */
    public java.lang.String getTipoMessaggio() {
        return tipoMessaggio;
    }


    /**
     * Sets the tipoMessaggio value for this AeMessaggioRicevutoInfo.
     * 
     * @param tipoMessaggio
     */
    public void setTipoMessaggio(java.lang.String tipoMessaggio) {
        this.tipoMessaggio = tipoMessaggio;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeMessaggioRicevutoInfo)) return false;
        AeMessaggioRicevutoInfo other = (AeMessaggioRicevutoInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataRicezione==null && other.getDataRicezione()==null) || 
             (this.dataRicezione!=null &&
              this.dataRicezione.equals(other.getDataRicezione()))) &&
            ((this.mittente==null && other.getMittente()==null) || 
             (this.mittente!=null &&
              this.mittente.equals(other.getMittente()))) &&
            ((this.testo==null && other.getTesto()==null) || 
             (this.testo!=null &&
              this.testo.equals(other.getTesto()))) &&
            ((this.tipoMessaggio==null && other.getTipoMessaggio()==null) || 
             (this.tipoMessaggio!=null &&
              this.tipoMessaggio.equals(other.getTipoMessaggio())));
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
        if (getDataRicezione() != null) {
            _hashCode += getDataRicezione().hashCode();
        }
        if (getMittente() != null) {
            _hashCode += getMittente().hashCode();
        }
        if (getTesto() != null) {
            _hashCode += getTesto().hashCode();
        }
        if (getTipoMessaggio() != null) {
            _hashCode += getTipoMessaggio().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeMessaggioRicevutoInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeMessaggioRicevutoInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataRicezione");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataRicezione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mittente");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mittente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "testo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoMessaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tipoMessaggio"));
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
