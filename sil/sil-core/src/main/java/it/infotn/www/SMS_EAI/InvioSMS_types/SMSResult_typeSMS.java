/**
 * SMSResult_typeSMS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.infotn.www.SMS_EAI.InvioSMS_types;

public class SMSResult_typeSMS  implements java.io.Serializable {
    /* Identificativo messaggio */
    private java.lang.String msgID;

    /* Testo del messaggio inviato */
    private java.lang.String text;

    /* Numeri telefono a cui è stato inviato l'SMS */
    private java.lang.String[] mobileDest;

    public SMSResult_typeSMS() {
    }

    public SMSResult_typeSMS(
           java.lang.String msgID,
           java.lang.String text,
           java.lang.String[] mobileDest) {
           this.msgID = msgID;
           this.text = text;
           this.mobileDest = mobileDest;
    }


    /**
     * Gets the msgID value for this SMSResult_typeSMS.
     * 
     * @return msgID   * Identificativo messaggio
     */
    public java.lang.String getMsgID() {
        return msgID;
    }


    /**
     * Sets the msgID value for this SMSResult_typeSMS.
     * 
     * @param msgID   * Identificativo messaggio
     */
    public void setMsgID(java.lang.String msgID) {
        this.msgID = msgID;
    }


    /**
     * Gets the text value for this SMSResult_typeSMS.
     * 
     * @return text   * Testo del messaggio inviato
     */
    public java.lang.String getText() {
        return text;
    }


    /**
     * Sets the text value for this SMSResult_typeSMS.
     * 
     * @param text   * Testo del messaggio inviato
     */
    public void setText(java.lang.String text) {
        this.text = text;
    }


    /**
     * Gets the mobileDest value for this SMSResult_typeSMS.
     * 
     * @return mobileDest   * Numeri telefono a cui è stato inviato l'SMS
     */
    public java.lang.String[] getMobileDest() {
        return mobileDest;
    }


    /**
     * Sets the mobileDest value for this SMSResult_typeSMS.
     * 
     * @param mobileDest   * Numeri telefono a cui è stato inviato l'SMS
     */
    public void setMobileDest(java.lang.String[] mobileDest) {
        this.mobileDest = mobileDest;
    }

    public java.lang.String getMobileDest(int i) {
        return this.mobileDest[i];
    }

    public void setMobileDest(int i, java.lang.String _value) {
        this.mobileDest[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SMSResult_typeSMS)) return false;
        SMSResult_typeSMS other = (SMSResult_typeSMS) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.msgID==null && other.getMsgID()==null) || 
             (this.msgID!=null &&
              this.msgID.equals(other.getMsgID()))) &&
            ((this.text==null && other.getText()==null) || 
             (this.text!=null &&
              this.text.equals(other.getText()))) &&
            ((this.mobileDest==null && other.getMobileDest()==null) || 
             (this.mobileDest!=null &&
              java.util.Arrays.equals(this.mobileDest, other.getMobileDest())));
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
        if (getMsgID() != null) {
            _hashCode += getMsgID().hashCode();
        }
        if (getText() != null) {
            _hashCode += getText().hashCode();
        }
        if (getMobileDest() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMobileDest());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMobileDest(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SMSResult_typeSMS.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", ">SMSResult_type>SMS"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "MsgID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("text");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "Text"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobileDest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "MobileDest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
