/**
 * Richiesta_invioCandidatura_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.cliclavoro.typesInvio;

public class Richiesta_invioCandidatura_Type  implements java.io.Serializable {
    private java.lang.String candidaturaXML;

    public Richiesta_invioCandidatura_Type() {
    }

    public Richiesta_invioCandidatura_Type(
           java.lang.String candidaturaXML) {
           this.candidaturaXML = candidaturaXML;
    }

 
    /**
     * Gets the candidaturaXML value for this Richiesta_invioCandidatura_Type.
     * 
     * @return candidaturaXML
     */
    public java.lang.String getCandidaturaXML() {
        return candidaturaXML;
    }


    /**
     * Sets the candidaturaXML value for this Richiesta_invioCandidatura_Type.
     * 
     * @param candidaturaXML
     */
    public void setCandidaturaXML(java.lang.String candidaturaXML) {
        this.candidaturaXML = candidaturaXML;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Richiesta_invioCandidatura_Type)) return false;
        Richiesta_invioCandidatura_Type other = (Richiesta_invioCandidatura_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.candidaturaXML==null && other.getCandidaturaXML()==null) || 
             (this.candidaturaXML!=null &&
              this.candidaturaXML.equals(other.getCandidaturaXML())));
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
        if (getCandidaturaXML() != null) {
            _hashCode += getCandidaturaXML().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Richiesta_invioCandidatura_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "richiesta_invioCandidatura_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("candidaturaXML");
        elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "CandidaturaXML"));
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
           java.lang.Class<?> _javaType,  
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
