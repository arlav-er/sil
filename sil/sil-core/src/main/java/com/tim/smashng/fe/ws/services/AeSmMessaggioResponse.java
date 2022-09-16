/**
 * AeSmMessaggioResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeSmMessaggioResponse  implements java.io.Serializable {
    private java.lang.String dataInvio;

    private java.lang.String dataRicezioneStatusReport;

    private java.lang.String destinatario;

    private java.lang.String idInvio;

    private java.lang.String stato;

    private java.lang.String statoSrDetail;

    public AeSmMessaggioResponse() {
    }

    public AeSmMessaggioResponse(
           java.lang.String dataInvio,
           java.lang.String dataRicezioneStatusReport,
           java.lang.String destinatario,
           java.lang.String idInvio,
           java.lang.String stato,
           java.lang.String statoSrDetail) {
           this.dataInvio = dataInvio;
           this.dataRicezioneStatusReport = dataRicezioneStatusReport;
           this.destinatario = destinatario;
           this.idInvio = idInvio;
           this.stato = stato;
           this.statoSrDetail = statoSrDetail;
    }


    /**
     * Gets the dataInvio value for this AeSmMessaggioResponse.
     * 
     * @return dataInvio
     */
    public java.lang.String getDataInvio() {
        return dataInvio;
    }


    /**
     * Sets the dataInvio value for this AeSmMessaggioResponse.
     * 
     * @param dataInvio
     */
    public void setDataInvio(java.lang.String dataInvio) {
        this.dataInvio = dataInvio;
    }


    /**
     * Gets the dataRicezioneStatusReport value for this AeSmMessaggioResponse.
     * 
     * @return dataRicezioneStatusReport
     */
    public java.lang.String getDataRicezioneStatusReport() {
        return dataRicezioneStatusReport;
    }


    /**
     * Sets the dataRicezioneStatusReport value for this AeSmMessaggioResponse.
     * 
     * @param dataRicezioneStatusReport
     */
    public void setDataRicezioneStatusReport(java.lang.String dataRicezioneStatusReport) {
        this.dataRicezioneStatusReport = dataRicezioneStatusReport;
    }


    /**
     * Gets the destinatario value for this AeSmMessaggioResponse.
     * 
     * @return destinatario
     */
    public java.lang.String getDestinatario() {
        return destinatario;
    }


    /**
     * Sets the destinatario value for this AeSmMessaggioResponse.
     * 
     * @param destinatario
     */
    public void setDestinatario(java.lang.String destinatario) {
        this.destinatario = destinatario;
    }


    /**
     * Gets the idInvio value for this AeSmMessaggioResponse.
     * 
     * @return idInvio
     */
    public java.lang.String getIdInvio() {
        return idInvio;
    }


    /**
     * Sets the idInvio value for this AeSmMessaggioResponse.
     * 
     * @param idInvio
     */
    public void setIdInvio(java.lang.String idInvio) {
        this.idInvio = idInvio;
    }


    /**
     * Gets the stato value for this AeSmMessaggioResponse.
     * 
     * @return stato
     */
    public java.lang.String getStato() {
        return stato;
    }


    /**
     * Sets the stato value for this AeSmMessaggioResponse.
     * 
     * @param stato
     */
    public void setStato(java.lang.String stato) {
        this.stato = stato;
    }


    /**
     * Gets the statoSrDetail value for this AeSmMessaggioResponse.
     * 
     * @return statoSrDetail
     */
    public java.lang.String getStatoSrDetail() {
        return statoSrDetail;
    }


    /**
     * Sets the statoSrDetail value for this AeSmMessaggioResponse.
     * 
     * @param statoSrDetail
     */
    public void setStatoSrDetail(java.lang.String statoSrDetail) {
        this.statoSrDetail = statoSrDetail;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeSmMessaggioResponse)) return false;
        AeSmMessaggioResponse other = (AeSmMessaggioResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataInvio==null && other.getDataInvio()==null) || 
             (this.dataInvio!=null &&
              this.dataInvio.equals(other.getDataInvio()))) &&
            ((this.dataRicezioneStatusReport==null && other.getDataRicezioneStatusReport()==null) || 
             (this.dataRicezioneStatusReport!=null &&
              this.dataRicezioneStatusReport.equals(other.getDataRicezioneStatusReport()))) &&
            ((this.destinatario==null && other.getDestinatario()==null) || 
             (this.destinatario!=null &&
              this.destinatario.equals(other.getDestinatario()))) &&
            ((this.idInvio==null && other.getIdInvio()==null) || 
             (this.idInvio!=null &&
              this.idInvio.equals(other.getIdInvio()))) &&
            ((this.stato==null && other.getStato()==null) || 
             (this.stato!=null &&
              this.stato.equals(other.getStato()))) &&
            ((this.statoSrDetail==null && other.getStatoSrDetail()==null) || 
             (this.statoSrDetail!=null &&
              this.statoSrDetail.equals(other.getStatoSrDetail())));
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
        if (getDataInvio() != null) {
            _hashCode += getDataInvio().hashCode();
        }
        if (getDataRicezioneStatusReport() != null) {
            _hashCode += getDataRicezioneStatusReport().hashCode();
        }
        if (getDestinatario() != null) {
            _hashCode += getDestinatario().hashCode();
        }
        if (getIdInvio() != null) {
            _hashCode += getIdInvio().hashCode();
        }
        if (getStato() != null) {
            _hashCode += getStato().hashCode();
        }
        if (getStatoSrDetail() != null) {
            _hashCode += getStatoSrDetail().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeSmMessaggioResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeSmMessaggioResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataInvio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataInvio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataRicezioneStatusReport");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataRicezioneStatusReport"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destinatario");
        elemField.setXmlName(new javax.xml.namespace.QName("", "destinatario"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idInvio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "idInvio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stato");
        elemField.setXmlName(new javax.xml.namespace.QName("", "stato"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statoSrDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "statoSrDetail"));
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
