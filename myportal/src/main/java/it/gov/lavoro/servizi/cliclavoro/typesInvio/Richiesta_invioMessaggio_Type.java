/**
 * Richiesta_invioMessaggio_Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.cliclavoro.typesInvio;

public class Richiesta_invioMessaggio_Type  implements java.io.Serializable {
    private java.lang.String messaggioXML;

    public Richiesta_invioMessaggio_Type() {
    }

    public Richiesta_invioMessaggio_Type(
           java.lang.String messaggioXML) {
           this.messaggioXML = messaggioXML;
    }


    /**
     * Gets the messaggioXML value for this Richiesta_invioMessaggio_Type.
     * 
     * @return messaggioXML
     */
    public java.lang.String getMessaggioXML() {
        return messaggioXML;
    }


    /**
     * Sets the messaggioXML value for this Richiesta_invioMessaggio_Type.
     * 
     * @param messaggioXML
     */
    public void setMessaggioXML(java.lang.String messaggioXML) {
        this.messaggioXML = messaggioXML;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Richiesta_invioMessaggio_Type)) return false;
        Richiesta_invioMessaggio_Type other = (Richiesta_invioMessaggio_Type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.messaggioXML==null && other.getMessaggioXML()==null) || 
             (this.messaggioXML!=null &&
              this.messaggioXML.equals(other.getMessaggioXML())));
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
        if (getMessaggioXML() != null) {
            _hashCode += getMessaggioXML().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Richiesta_invioMessaggio_Type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "richiesta_invioMessaggio_Type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messaggioXML");
        elemField.setXmlName(new javax.xml.namespace.QName("http://servizi.lavoro.gov.it/cliclavoro/types", "MessaggioXML"));
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
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
