/**
 * Risposta_invioMessaggio_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.cliclavoro.typesInvio;

public class Risposta_invioMessaggio_Type  implements java.io.Serializable {
    private it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_TypeTipo_Risposta tipo_Risposta;

    private java.lang.String descr_Esito;

    public Risposta_invioMessaggio_Type() {
    }

    public Risposta_invioMessaggio_Type(
           it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_TypeTipo_Risposta tipo_Risposta,
           java.lang.String descr_Esito) {
           this.tipo_Risposta = tipo_Risposta;
           this.descr_Esito = descr_Esito;
    }


    /**
     * Gets the tipo_Risposta value for this Risposta_invioMessaggio_Type.
     * 
     * @return tipo_Risposta
     */
    public it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_TypeTipo_Risposta getTipo_Risposta() {
        return tipo_Risposta;
    }


    /**
     * Sets the tipo_Risposta value for this Risposta_invioMessaggio_Type.
     * 
     * @param tipo_Risposta
     */
    public void setTipo_Risposta(it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioMessaggio_TypeTipo_Risposta tipo_Risposta) {
        this.tipo_Risposta = tipo_Risposta;
    }


    /**
     * Gets the descr_Esito value for this Risposta_invioMessaggio_Type.
     * 
     * @return descr_Esito
     */
    public java.lang.String getDescr_Esito() {
        return descr_Esito;
    }


    /**
     * Sets the descr_Esito value for this Risposta_invioMessaggio_Type.
     * 
     * @param descr_Esito
     */
    public void setDescr_Esito(java.lang.String descr_Esito) {
        this.descr_Esito = descr_Esito;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Risposta_invioMessaggio_Type)) return false;
        Risposta_invioMessaggio_Type other = (Risposta_invioMessaggio_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.tipo_Risposta==null && other.getTipo_Risposta()==null) || 
             (this.tipo_Risposta!=null &&
              this.tipo_Risposta.equals(other.getTipo_Risposta()))) &&
            ((this.descr_Esito==null && other.getDescr_Esito()==null) || 
             (this.descr_Esito!=null &&
              this.descr_Esito.equals(other.getDescr_Esito())));
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
        if (getTipo_Risposta() != null) {
            _hashCode += getTipo_Risposta().hashCode();
        }
        if (getDescr_Esito() != null) {
            _hashCode += getDescr_Esito().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Risposta_invioMessaggio_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "risposta_invioMessaggio_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipo_Risposta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "Tipo_Risposta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", ">risposta_invioMessaggio_Type>Tipo_Risposta"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("descr_Esito");
        elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "Descr_Esito"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
           java.lang.Class<?> _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
