/**
 * AeRicercaMessaggiRicevutiRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeRicercaMessaggiRicevutiRequest  extends com.tim.smashng.fe.ws.services.SmashAeRequest  implements java.io.Serializable {
    private com.tim.smashng.fe.ws.services.AeGenericFilter genericFilter;

    private java.lang.String mittente;

    private java.lang.String testoMessaggio;

    private java.lang.String testoOggetto;

    private java.lang.String tipoMessaggio;

    public AeRicercaMessaggiRicevutiRequest() {
    }

    public AeRicercaMessaggiRicevutiRequest(
           java.lang.String codiceContratto,
           java.lang.String password,
           java.lang.String username,
           com.tim.smashng.fe.ws.services.AeGenericFilter genericFilter,
           java.lang.String mittente,
           java.lang.String testoMessaggio,
           java.lang.String testoOggetto,
           java.lang.String tipoMessaggio) {
        super(
            codiceContratto,
            password,
            username);
        this.genericFilter = genericFilter;
        this.mittente = mittente;
        this.testoMessaggio = testoMessaggio;
        this.testoOggetto = testoOggetto;
        this.tipoMessaggio = tipoMessaggio;
    }


    /**
     * Gets the genericFilter value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @return genericFilter
     */
    public com.tim.smashng.fe.ws.services.AeGenericFilter getGenericFilter() {
        return genericFilter;
    }


    /**
     * Sets the genericFilter value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @param genericFilter
     */
    public void setGenericFilter(com.tim.smashng.fe.ws.services.AeGenericFilter genericFilter) {
        this.genericFilter = genericFilter;
    }


    /**
     * Gets the mittente value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @return mittente
     */
    public java.lang.String getMittente() {
        return mittente;
    }


    /**
     * Sets the mittente value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @param mittente
     */
    public void setMittente(java.lang.String mittente) {
        this.mittente = mittente;
    }


    /**
     * Gets the testoMessaggio value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @return testoMessaggio
     */
    public java.lang.String getTestoMessaggio() {
        return testoMessaggio;
    }


    /**
     * Sets the testoMessaggio value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @param testoMessaggio
     */
    public void setTestoMessaggio(java.lang.String testoMessaggio) {
        this.testoMessaggio = testoMessaggio;
    }


    /**
     * Gets the testoOggetto value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @return testoOggetto
     */
    public java.lang.String getTestoOggetto() {
        return testoOggetto;
    }


    /**
     * Sets the testoOggetto value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @param testoOggetto
     */
    public void setTestoOggetto(java.lang.String testoOggetto) {
        this.testoOggetto = testoOggetto;
    }


    /**
     * Gets the tipoMessaggio value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @return tipoMessaggio
     */
    public java.lang.String getTipoMessaggio() {
        return tipoMessaggio;
    }


    /**
     * Sets the tipoMessaggio value for this AeRicercaMessaggiRicevutiRequest.
     * 
     * @param tipoMessaggio
     */
    public void setTipoMessaggio(java.lang.String tipoMessaggio) {
        this.tipoMessaggio = tipoMessaggio;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeRicercaMessaggiRicevutiRequest)) return false;
        AeRicercaMessaggiRicevutiRequest other = (AeRicercaMessaggiRicevutiRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.genericFilter==null && other.getGenericFilter()==null) || 
             (this.genericFilter!=null &&
              this.genericFilter.equals(other.getGenericFilter()))) &&
            ((this.mittente==null && other.getMittente()==null) || 
             (this.mittente!=null &&
              this.mittente.equals(other.getMittente()))) &&
            ((this.testoMessaggio==null && other.getTestoMessaggio()==null) || 
             (this.testoMessaggio!=null &&
              this.testoMessaggio.equals(other.getTestoMessaggio()))) &&
            ((this.testoOggetto==null && other.getTestoOggetto()==null) || 
             (this.testoOggetto!=null &&
              this.testoOggetto.equals(other.getTestoOggetto()))) &&
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
        int _hashCode = super.hashCode();
        if (getGenericFilter() != null) {
            _hashCode += getGenericFilter().hashCode();
        }
        if (getMittente() != null) {
            _hashCode += getMittente().hashCode();
        }
        if (getTestoMessaggio() != null) {
            _hashCode += getTestoMessaggio().hashCode();
        }
        if (getTestoOggetto() != null) {
            _hashCode += getTestoOggetto().hashCode();
        }
        if (getTipoMessaggio() != null) {
            _hashCode += getTipoMessaggio().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeRicercaMessaggiRicevutiRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeRicercaMessaggiRicevutiRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericFilter");
        elemField.setXmlName(new javax.xml.namespace.QName("", "genericFilter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeGenericFilter"));
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
        elemField.setFieldName("testoMessaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "testoMessaggio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testoOggetto");
        elemField.setXmlName(new javax.xml.namespace.QName("", "testoOggetto"));
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
