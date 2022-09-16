/**
 * SMSForStatusRequest_type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.infotn.www.SMS_EAI.InvioSMS_types;

public class SMSForStatusRequest_type  implements java.io.Serializable {
    /* Identificativo opzionale per tracciare la richiesta stato SMS. */
    private java.lang.String trackingID;

    private java.lang.String SMSMsgId;

    public SMSForStatusRequest_type() {
    }

    public SMSForStatusRequest_type(
           java.lang.String trackingID,
           java.lang.String SMSMsgId) {
           this.trackingID = trackingID;
           this.SMSMsgId = SMSMsgId;
    }


    /**
     * Gets the trackingID value for this SMSForStatusRequest_type.
     * 
     * @return trackingID   * Identificativo opzionale per tracciare la richiesta stato SMS.
     */
    public java.lang.String getTrackingID() {
        return trackingID;
    }


    /**
     * Sets the trackingID value for this SMSForStatusRequest_type.
     * 
     * @param trackingID   * Identificativo opzionale per tracciare la richiesta stato SMS.
     */
    public void setTrackingID(java.lang.String trackingID) {
        this.trackingID = trackingID;
    }


    /**
     * Gets the SMSMsgId value for this SMSForStatusRequest_type.
     * 
     * @return SMSMsgId
     */
    public java.lang.String getSMSMsgId() {
        return SMSMsgId;
    }


    /**
     * Sets the SMSMsgId value for this SMSForStatusRequest_type.
     * 
     * @param SMSMsgId
     */
    public void setSMSMsgId(java.lang.String SMSMsgId) {
        this.SMSMsgId = SMSMsgId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SMSForStatusRequest_type)) return false;
        SMSForStatusRequest_type other = (SMSForStatusRequest_type) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.trackingID==null && other.getTrackingID()==null) || 
             (this.trackingID!=null &&
              this.trackingID.equals(other.getTrackingID()))) &&
            ((this.SMSMsgId==null && other.getSMSMsgId()==null) || 
             (this.SMSMsgId!=null &&
              this.SMSMsgId.equals(other.getSMSMsgId())));
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
        if (getTrackingID() != null) {
            _hashCode += getTrackingID().hashCode();
        }
        if (getSMSMsgId() != null) {
            _hashCode += getSMSMsgId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SMSForStatusRequest_type.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMSForStatusRequest_type"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trackingID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "TrackingID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SMSMsgId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.infotn.it/SMS-EAI/InvioSMS_types", "SMSMsgId"));
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
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
