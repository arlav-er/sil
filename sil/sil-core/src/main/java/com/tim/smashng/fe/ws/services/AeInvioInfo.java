/**
 * AeInvioInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeInvioInfo  implements java.io.Serializable {
    private java.lang.String dataInvio;

    private java.lang.Long idInvio;

    private java.lang.String link;

    private com.tim.smashng.fe.ws.services.AeMessaggioInviatoInfo[] messaggiInviati;

    private java.lang.String stato;

    private java.lang.String testo;

    private java.lang.String tipoMessaggio;

    public AeInvioInfo() {
    }

    public AeInvioInfo(
           java.lang.String dataInvio,
           java.lang.Long idInvio,
           java.lang.String link,
           com.tim.smashng.fe.ws.services.AeMessaggioInviatoInfo[] messaggiInviati,
           java.lang.String stato,
           java.lang.String testo,
           java.lang.String tipoMessaggio) {
           this.dataInvio = dataInvio;
           this.idInvio = idInvio;
           this.link = link;
           this.messaggiInviati = messaggiInviati;
           this.stato = stato;
           this.testo = testo;
           this.tipoMessaggio = tipoMessaggio;
    }


    /**
     * Gets the dataInvio value for this AeInvioInfo.
     * 
     * @return dataInvio
     */
    public java.lang.String getDataInvio() {
        return dataInvio;
    }


    /**
     * Sets the dataInvio value for this AeInvioInfo.
     * 
     * @param dataInvio
     */
    public void setDataInvio(java.lang.String dataInvio) {
        this.dataInvio = dataInvio;
    }


    /**
     * Gets the idInvio value for this AeInvioInfo.
     * 
     * @return idInvio
     */
    public java.lang.Long getIdInvio() {
        return idInvio;
    }


    /**
     * Sets the idInvio value for this AeInvioInfo.
     * 
     * @param idInvio
     */
    public void setIdInvio(java.lang.Long idInvio) {
        this.idInvio = idInvio;
    }


    /**
     * Gets the link value for this AeInvioInfo.
     * 
     * @return link
     */
    public java.lang.String getLink() {
        return link;
    }


    /**
     * Sets the link value for this AeInvioInfo.
     * 
     * @param link
     */
    public void setLink(java.lang.String link) {
        this.link = link;
    }


    /**
     * Gets the messaggiInviati value for this AeInvioInfo.
     * 
     * @return messaggiInviati
     */
    public com.tim.smashng.fe.ws.services.AeMessaggioInviatoInfo[] getMessaggiInviati() {
        return messaggiInviati;
    }


    /**
     * Sets the messaggiInviati value for this AeInvioInfo.
     * 
     * @param messaggiInviati
     */
    public void setMessaggiInviati(com.tim.smashng.fe.ws.services.AeMessaggioInviatoInfo[] messaggiInviati) {
        this.messaggiInviati = messaggiInviati;
    }

    public com.tim.smashng.fe.ws.services.AeMessaggioInviatoInfo getMessaggiInviati(int i) {
        return this.messaggiInviati[i];
    }

    public void setMessaggiInviati(int i, com.tim.smashng.fe.ws.services.AeMessaggioInviatoInfo _value) {
        this.messaggiInviati[i] = _value;
    }


    /**
     * Gets the stato value for this AeInvioInfo.
     * 
     * @return stato
     */
    public java.lang.String getStato() {
        return stato;
    }


    /**
     * Sets the stato value for this AeInvioInfo.
     * 
     * @param stato
     */
    public void setStato(java.lang.String stato) {
        this.stato = stato;
    }


    /**
     * Gets the testo value for this AeInvioInfo.
     * 
     * @return testo
     */
    public java.lang.String getTesto() {
        return testo;
    }


    /**
     * Sets the testo value for this AeInvioInfo.
     * 
     * @param testo
     */
    public void setTesto(java.lang.String testo) {
        this.testo = testo;
    }


    /**
     * Gets the tipoMessaggio value for this AeInvioInfo.
     * 
     * @return tipoMessaggio
     */
    public java.lang.String getTipoMessaggio() {
        return tipoMessaggio;
    }


    /**
     * Sets the tipoMessaggio value for this AeInvioInfo.
     * 
     * @param tipoMessaggio
     */
    public void setTipoMessaggio(java.lang.String tipoMessaggio) {
        this.tipoMessaggio = tipoMessaggio;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeInvioInfo)) return false;
        AeInvioInfo other = (AeInvioInfo) obj;
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
            ((this.idInvio==null && other.getIdInvio()==null) || 
             (this.idInvio!=null &&
              this.idInvio.equals(other.getIdInvio()))) &&
            ((this.link==null && other.getLink()==null) || 
             (this.link!=null &&
              this.link.equals(other.getLink()))) &&
            ((this.messaggiInviati==null && other.getMessaggiInviati()==null) || 
             (this.messaggiInviati!=null &&
              java.util.Arrays.equals(this.messaggiInviati, other.getMessaggiInviati()))) &&
            ((this.stato==null && other.getStato()==null) || 
             (this.stato!=null &&
              this.stato.equals(other.getStato()))) &&
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
        if (getDataInvio() != null) {
            _hashCode += getDataInvio().hashCode();
        }
        if (getIdInvio() != null) {
            _hashCode += getIdInvio().hashCode();
        }
        if (getLink() != null) {
            _hashCode += getLink().hashCode();
        }
        if (getMessaggiInviati() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMessaggiInviati());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMessaggiInviati(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getStato() != null) {
            _hashCode += getStato().hashCode();
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
        new org.apache.axis.description.TypeDesc(AeInvioInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInvioInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataInvio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataInvio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idInvio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "idInvio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("link");
        elemField.setXmlName(new javax.xml.namespace.QName("", "link"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messaggiInviati");
        elemField.setXmlName(new javax.xml.namespace.QName("", "messaggiInviati"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeMessaggioInviatoInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stato");
        elemField.setXmlName(new javax.xml.namespace.QName("", "stato"));
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
