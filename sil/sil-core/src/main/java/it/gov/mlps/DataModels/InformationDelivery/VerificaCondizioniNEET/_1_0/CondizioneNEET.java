/**
 * CondizioneNEET.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0;

public class CondizioneNEET  implements java.io.Serializable {
    private java.lang.String decodifica;

    private boolean NEET;

    public CondizioneNEET() {
    }

    public CondizioneNEET(
           java.lang.String decodifica,
           boolean NEET) {
           this.decodifica = decodifica;
           this.NEET = NEET;
    }


    /**
     * Gets the decodifica value for this CondizioneNEET.
     * 
     * @return decodifica
     */
    public java.lang.String getDecodifica() {
        return decodifica;
    }


    /**
     * Sets the decodifica value for this CondizioneNEET.
     * 
     * @param decodifica
     */
    public void setDecodifica(java.lang.String decodifica) {
        this.decodifica = decodifica;
    }


    /**
     * Gets the NEET value for this CondizioneNEET.
     * 
     * @return NEET
     */
    public boolean isNEET() {
        return NEET;
    }


    /**
     * Sets the NEET value for this CondizioneNEET.
     * 
     * @param NEET
     */
    public void setNEET(boolean NEET) {
        this.NEET = NEET;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CondizioneNEET)) return false;
        CondizioneNEET other = (CondizioneNEET) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.decodifica==null && other.getDecodifica()==null) || 
             (this.decodifica!=null &&
              this.decodifica.equals(other.getDecodifica()))) &&
            this.NEET == other.isNEET();
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
        if (getDecodifica() != null) {
            _hashCode += getDecodifica().hashCode();
        }
        _hashCode += (isNEET() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CondizioneNEET.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "CondizioneNEET"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("decodifica");
        elemField.setXmlName(new javax.xml.namespace.QName("http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "Decodifica"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NEET");
        elemField.setXmlName(new javax.xml.namespace.QName("http://mlps.gov.it/DataModels/InformationDelivery/VerificaCondizioniNEET/1.0", "NEET"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
