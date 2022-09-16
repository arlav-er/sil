/**
 * SMS_type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.infotn.www.SMS_EAI.InvioSMS_types;

public class SMS_type  implements java.io.Serializable {
    /* Mittente personalizzato se specificato e il servizio lo consente,
     * altrimenti vuoto o nullo. */
    private java.lang.String sender;

    private it.infotn.www.SMS_EAI.InvioSMS_types.SMSMessage_type SMS;

    public SMS_type() {
    }

    public SMS_type(
           java.lang.String sender,
           it.infotn.www.SMS_EAI.InvioSMS_types.SMSMessage_type SMS) {
           this.sender = sender;
           this.SMS = SMS;
    }


    /**
     * Gets the sender value for this SMS_type.
     * 
     * @return sender   * Mittente personalizzato se specificato e il servizio lo consente,
     * altrimenti vuoto o nullo.
     */
    public java.lang.String getSender() {
        return sender;
    }


    /**
     * Sets the sender value for this SMS_type.
     * 
     * @param sender   * Mittente personalizzato se specificato e il servizio lo consente,
     * altrimenti vuoto o nullo.
     */
    public void setSender(java.lang.String sender) {
        this.sender = sender;
    }


    /**
     * Gets the SMS value for this SMS_type.
     * 
     * @return SMS
     */
    public it.infotn.www.SMS_EAI.InvioSMS_types.SMSMessage_type getSMS() {
        return SMS;
    }


    /**
     * Sets the SMS value for this SMS_type.
     * 
     * @param SMS
     */
    public void setSMS(it.infotn.www.SMS_EAI.InvioSMS_types.SMSMessage_type SMS) {
        this.SMS = SMS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SMS_type)) return false;
        SMS_type other = (SMS_type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sender==null && other.getSender()==null) || 
             (this.sender!=null &&
              this.sender.equals(other.getSender()))) &&
            ((this.SMS==null && other.getSMS()==null) || 
             (this.SMS!=null &&
              this.SMS.equals(other.getSMS())));
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
        if (getSender() != null) {
            _hashCode += getSender().hashCode();
        }
        if (getSMS() != null) {
            _hashCode += getSMS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SMS_type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMS_type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sender");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "sender"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SMS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMSMessage_type"));
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
