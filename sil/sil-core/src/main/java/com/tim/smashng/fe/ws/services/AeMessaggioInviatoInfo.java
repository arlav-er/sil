/**
 * AeMessaggioInviatoInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeMessaggioInviatoInfo  implements java.io.Serializable {
    private int costoMessaggio;

    private java.lang.String dataInvio;

    private java.lang.String dataRicezioneStatusReport;

    private java.lang.String destinatario;

    private int numeroPacchettiTrasporto;

    private boolean ricevutoStatusReport;

    private java.lang.String stato;

    public AeMessaggioInviatoInfo() {
    }

    public AeMessaggioInviatoInfo(
           int costoMessaggio,
           java.lang.String dataInvio,
           java.lang.String dataRicezioneStatusReport,
           java.lang.String destinatario,
           int numeroPacchettiTrasporto,
           boolean ricevutoStatusReport,
           java.lang.String stato) {
           this.costoMessaggio = costoMessaggio;
           this.dataInvio = dataInvio;
           this.dataRicezioneStatusReport = dataRicezioneStatusReport;
           this.destinatario = destinatario;
           this.numeroPacchettiTrasporto = numeroPacchettiTrasporto;
           this.ricevutoStatusReport = ricevutoStatusReport;
           this.stato = stato;
    }


    /**
     * Gets the costoMessaggio value for this AeMessaggioInviatoInfo.
     * 
     * @return costoMessaggio
     */
    public int getCostoMessaggio() {
        return costoMessaggio;
    }


    /**
     * Sets the costoMessaggio value for this AeMessaggioInviatoInfo.
     * 
     * @param costoMessaggio
     */
    public void setCostoMessaggio(int costoMessaggio) {
        this.costoMessaggio = costoMessaggio;
    }


    /**
     * Gets the dataInvio value for this AeMessaggioInviatoInfo.
     * 
     * @return dataInvio
     */
    public java.lang.String getDataInvio() {
        return dataInvio;
    }


    /**
     * Sets the dataInvio value for this AeMessaggioInviatoInfo.
     * 
     * @param dataInvio
     */
    public void setDataInvio(java.lang.String dataInvio) {
        this.dataInvio = dataInvio;
    }


    /**
     * Gets the dataRicezioneStatusReport value for this AeMessaggioInviatoInfo.
     * 
     * @return dataRicezioneStatusReport
     */
    public java.lang.String getDataRicezioneStatusReport() {
        return dataRicezioneStatusReport;
    }


    /**
     * Sets the dataRicezioneStatusReport value for this AeMessaggioInviatoInfo.
     * 
     * @param dataRicezioneStatusReport
     */
    public void setDataRicezioneStatusReport(java.lang.String dataRicezioneStatusReport) {
        this.dataRicezioneStatusReport = dataRicezioneStatusReport;
    }


    /**
     * Gets the destinatario value for this AeMessaggioInviatoInfo.
     * 
     * @return destinatario
     */
    public java.lang.String getDestinatario() {
        return destinatario;
    }


    /**
     * Sets the destinatario value for this AeMessaggioInviatoInfo.
     * 
     * @param destinatario
     */
    public void setDestinatario(java.lang.String destinatario) {
        this.destinatario = destinatario;
    }


    /**
     * Gets the numeroPacchettiTrasporto value for this AeMessaggioInviatoInfo.
     * 
     * @return numeroPacchettiTrasporto
     */
    public int getNumeroPacchettiTrasporto() {
        return numeroPacchettiTrasporto;
    }


    /**
     * Sets the numeroPacchettiTrasporto value for this AeMessaggioInviatoInfo.
     * 
     * @param numeroPacchettiTrasporto
     */
    public void setNumeroPacchettiTrasporto(int numeroPacchettiTrasporto) {
        this.numeroPacchettiTrasporto = numeroPacchettiTrasporto;
    }


    /**
     * Gets the ricevutoStatusReport value for this AeMessaggioInviatoInfo.
     * 
     * @return ricevutoStatusReport
     */
    public boolean isRicevutoStatusReport() {
        return ricevutoStatusReport;
    }


    /**
     * Sets the ricevutoStatusReport value for this AeMessaggioInviatoInfo.
     * 
     * @param ricevutoStatusReport
     */
    public void setRicevutoStatusReport(boolean ricevutoStatusReport) {
        this.ricevutoStatusReport = ricevutoStatusReport;
    }


    /**
     * Gets the stato value for this AeMessaggioInviatoInfo.
     * 
     * @return stato
     */
    public java.lang.String getStato() {
        return stato;
    }


    /**
     * Sets the stato value for this AeMessaggioInviatoInfo.
     * 
     * @param stato
     */
    public void setStato(java.lang.String stato) {
        this.stato = stato;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeMessaggioInviatoInfo)) return false;
        AeMessaggioInviatoInfo other = (AeMessaggioInviatoInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.costoMessaggio == other.getCostoMessaggio() &&
            ((this.dataInvio==null && other.getDataInvio()==null) || 
             (this.dataInvio!=null &&
              this.dataInvio.equals(other.getDataInvio()))) &&
            ((this.dataRicezioneStatusReport==null && other.getDataRicezioneStatusReport()==null) || 
             (this.dataRicezioneStatusReport!=null &&
              this.dataRicezioneStatusReport.equals(other.getDataRicezioneStatusReport()))) &&
            ((this.destinatario==null && other.getDestinatario()==null) || 
             (this.destinatario!=null &&
              this.destinatario.equals(other.getDestinatario()))) &&
            this.numeroPacchettiTrasporto == other.getNumeroPacchettiTrasporto() &&
            this.ricevutoStatusReport == other.isRicevutoStatusReport() &&
            ((this.stato==null && other.getStato()==null) || 
             (this.stato!=null &&
              this.stato.equals(other.getStato())));
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
        _hashCode += getCostoMessaggio();
        if (getDataInvio() != null) {
            _hashCode += getDataInvio().hashCode();
        }
        if (getDataRicezioneStatusReport() != null) {
            _hashCode += getDataRicezioneStatusReport().hashCode();
        }
        if (getDestinatario() != null) {
            _hashCode += getDestinatario().hashCode();
        }
        _hashCode += getNumeroPacchettiTrasporto();
        _hashCode += (isRicevutoStatusReport() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getStato() != null) {
            _hashCode += getStato().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeMessaggioInviatoInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeMessaggioInviatoInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("costoMessaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "costoMessaggio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("numeroPacchettiTrasporto");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroPacchettiTrasporto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ricevutoStatusReport");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ricevutoStatusReport"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stato");
        elemField.setXmlName(new javax.xml.namespace.QName("", "stato"));
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
