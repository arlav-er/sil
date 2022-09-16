/**
 * AePacchettoInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AePacchettoInfo  implements java.io.Serializable {
    private java.lang.Long creditoDisponibile;

    private java.lang.Long creditoIniziale;

    private java.lang.String dataAttivazione;

    private java.lang.String dataCessazione;

    private java.lang.String dataScadenza;

    private java.lang.String stato;

    private java.lang.String tipoPacchetto;

    public AePacchettoInfo() {
    }

    public AePacchettoInfo(
           java.lang.Long creditoDisponibile,
           java.lang.Long creditoIniziale,
           java.lang.String dataAttivazione,
           java.lang.String dataCessazione,
           java.lang.String dataScadenza,
           java.lang.String stato,
           java.lang.String tipoPacchetto) {
           this.creditoDisponibile = creditoDisponibile;
           this.creditoIniziale = creditoIniziale;
           this.dataAttivazione = dataAttivazione;
           this.dataCessazione = dataCessazione;
           this.dataScadenza = dataScadenza;
           this.stato = stato;
           this.tipoPacchetto = tipoPacchetto;
    }


    /**
     * Gets the creditoDisponibile value for this AePacchettoInfo.
     * 
     * @return creditoDisponibile
     */
    public java.lang.Long getCreditoDisponibile() {
        return creditoDisponibile;
    }


    /**
     * Sets the creditoDisponibile value for this AePacchettoInfo.
     * 
     * @param creditoDisponibile
     */
    public void setCreditoDisponibile(java.lang.Long creditoDisponibile) {
        this.creditoDisponibile = creditoDisponibile;
    }


    /**
     * Gets the creditoIniziale value for this AePacchettoInfo.
     * 
     * @return creditoIniziale
     */
    public java.lang.Long getCreditoIniziale() {
        return creditoIniziale;
    }


    /**
     * Sets the creditoIniziale value for this AePacchettoInfo.
     * 
     * @param creditoIniziale
     */
    public void setCreditoIniziale(java.lang.Long creditoIniziale) {
        this.creditoIniziale = creditoIniziale;
    }


    /**
     * Gets the dataAttivazione value for this AePacchettoInfo.
     * 
     * @return dataAttivazione
     */
    public java.lang.String getDataAttivazione() {
        return dataAttivazione;
    }


    /**
     * Sets the dataAttivazione value for this AePacchettoInfo.
     * 
     * @param dataAttivazione
     */
    public void setDataAttivazione(java.lang.String dataAttivazione) {
        this.dataAttivazione = dataAttivazione;
    }


    /**
     * Gets the dataCessazione value for this AePacchettoInfo.
     * 
     * @return dataCessazione
     */
    public java.lang.String getDataCessazione() {
        return dataCessazione;
    }


    /**
     * Sets the dataCessazione value for this AePacchettoInfo.
     * 
     * @param dataCessazione
     */
    public void setDataCessazione(java.lang.String dataCessazione) {
        this.dataCessazione = dataCessazione;
    }


    /**
     * Gets the dataScadenza value for this AePacchettoInfo.
     * 
     * @return dataScadenza
     */
    public java.lang.String getDataScadenza() {
        return dataScadenza;
    }


    /**
     * Sets the dataScadenza value for this AePacchettoInfo.
     * 
     * @param dataScadenza
     */
    public void setDataScadenza(java.lang.String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }


    /**
     * Gets the stato value for this AePacchettoInfo.
     * 
     * @return stato
     */
    public java.lang.String getStato() {
        return stato;
    }


    /**
     * Sets the stato value for this AePacchettoInfo.
     * 
     * @param stato
     */
    public void setStato(java.lang.String stato) {
        this.stato = stato;
    }


    /**
     * Gets the tipoPacchetto value for this AePacchettoInfo.
     * 
     * @return tipoPacchetto
     */
    public java.lang.String getTipoPacchetto() {
        return tipoPacchetto;
    }


    /**
     * Sets the tipoPacchetto value for this AePacchettoInfo.
     * 
     * @param tipoPacchetto
     */
    public void setTipoPacchetto(java.lang.String tipoPacchetto) {
        this.tipoPacchetto = tipoPacchetto;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AePacchettoInfo)) return false;
        AePacchettoInfo other = (AePacchettoInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.creditoDisponibile==null && other.getCreditoDisponibile()==null) || 
             (this.creditoDisponibile!=null &&
              this.creditoDisponibile.equals(other.getCreditoDisponibile()))) &&
            ((this.creditoIniziale==null && other.getCreditoIniziale()==null) || 
             (this.creditoIniziale!=null &&
              this.creditoIniziale.equals(other.getCreditoIniziale()))) &&
            ((this.dataAttivazione==null && other.getDataAttivazione()==null) || 
             (this.dataAttivazione!=null &&
              this.dataAttivazione.equals(other.getDataAttivazione()))) &&
            ((this.dataCessazione==null && other.getDataCessazione()==null) || 
             (this.dataCessazione!=null &&
              this.dataCessazione.equals(other.getDataCessazione()))) &&
            ((this.dataScadenza==null && other.getDataScadenza()==null) || 
             (this.dataScadenza!=null &&
              this.dataScadenza.equals(other.getDataScadenza()))) &&
            ((this.stato==null && other.getStato()==null) || 
             (this.stato!=null &&
              this.stato.equals(other.getStato()))) &&
            ((this.tipoPacchetto==null && other.getTipoPacchetto()==null) || 
             (this.tipoPacchetto!=null &&
              this.tipoPacchetto.equals(other.getTipoPacchetto())));
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
        if (getCreditoDisponibile() != null) {
            _hashCode += getCreditoDisponibile().hashCode();
        }
        if (getCreditoIniziale() != null) {
            _hashCode += getCreditoIniziale().hashCode();
        }
        if (getDataAttivazione() != null) {
            _hashCode += getDataAttivazione().hashCode();
        }
        if (getDataCessazione() != null) {
            _hashCode += getDataCessazione().hashCode();
        }
        if (getDataScadenza() != null) {
            _hashCode += getDataScadenza().hashCode();
        }
        if (getStato() != null) {
            _hashCode += getStato().hashCode();
        }
        if (getTipoPacchetto() != null) {
            _hashCode += getTipoPacchetto().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AePacchettoInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aePacchettoInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditoDisponibile");
        elemField.setXmlName(new javax.xml.namespace.QName("", "creditoDisponibile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditoIniziale");
        elemField.setXmlName(new javax.xml.namespace.QName("", "creditoIniziale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataAttivazione");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataAttivazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataCessazione");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataCessazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataScadenza");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataScadenza"));
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
        elemField.setFieldName("tipoPacchetto");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tipoPacchetto"));
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
