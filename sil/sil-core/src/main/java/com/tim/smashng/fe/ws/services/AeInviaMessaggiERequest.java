/**
 * AeInviaMessaggiERequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeInviaMessaggiERequest  extends com.tim.smashng.fe.ws.services.SmashAeRequest  implements java.io.Serializable {
    private java.lang.String[] destinatari;

    private java.lang.String linkWapPush;

    private java.lang.String r1;

    private java.lang.String r2;

    private java.lang.String testo;

    public AeInviaMessaggiERequest() {
    }

    public AeInviaMessaggiERequest(
           java.lang.String codiceContratto,
           java.lang.String password,
           java.lang.String username,
           java.lang.String[] destinatari,
           java.lang.String linkWapPush,
           java.lang.String r1,
           java.lang.String r2,
           java.lang.String testo) {
        super(
            codiceContratto,
            password,
            username);
        this.destinatari = destinatari;
        this.linkWapPush = linkWapPush;
        this.r1 = r1;
        this.r2 = r2;
        this.testo = testo;
    }


    /**
     * Gets the destinatari value for this AeInviaMessaggiERequest.
     * 
     * @return destinatari
     */
    public java.lang.String[] getDestinatari() {
        return destinatari;
    }


    /**
     * Sets the destinatari value for this AeInviaMessaggiERequest.
     * 
     * @param destinatari
     */
    public void setDestinatari(java.lang.String[] destinatari) {
        this.destinatari = destinatari;
    }

    public java.lang.String getDestinatari(int i) {
        return this.destinatari[i];
    }

    public void setDestinatari(int i, java.lang.String _value) {
        this.destinatari[i] = _value;
    }


    /**
     * Gets the linkWapPush value for this AeInviaMessaggiERequest.
     * 
     * @return linkWapPush
     */
    public java.lang.String getLinkWapPush() {
        return linkWapPush;
    }


    /**
     * Sets the linkWapPush value for this AeInviaMessaggiERequest.
     * 
     * @param linkWapPush
     */
    public void setLinkWapPush(java.lang.String linkWapPush) {
        this.linkWapPush = linkWapPush;
    }


    /**
     * Gets the r1 value for this AeInviaMessaggiERequest.
     * 
     * @return r1
     */
    public java.lang.String getR1() {
        return r1;
    }


    /**
     * Sets the r1 value for this AeInviaMessaggiERequest.
     * 
     * @param r1
     */
    public void setR1(java.lang.String r1) {
        this.r1 = r1;
    }


    /**
     * Gets the r2 value for this AeInviaMessaggiERequest.
     * 
     * @return r2
     */
    public java.lang.String getR2() {
        return r2;
    }


    /**
     * Sets the r2 value for this AeInviaMessaggiERequest.
     * 
     * @param r2
     */
    public void setR2(java.lang.String r2) {
        this.r2 = r2;
    }


    /**
     * Gets the testo value for this AeInviaMessaggiERequest.
     * 
     * @return testo
     */
    public java.lang.String getTesto() {
        return testo;
    }


    /**
     * Sets the testo value for this AeInviaMessaggiERequest.
     * 
     * @param testo
     */
    public void setTesto(java.lang.String testo) {
        this.testo = testo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeInviaMessaggiERequest)) return false;
        AeInviaMessaggiERequest other = (AeInviaMessaggiERequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.destinatari==null && other.getDestinatari()==null) || 
             (this.destinatari!=null &&
              java.util.Arrays.equals(this.destinatari, other.getDestinatari()))) &&
            ((this.linkWapPush==null && other.getLinkWapPush()==null) || 
             (this.linkWapPush!=null &&
              this.linkWapPush.equals(other.getLinkWapPush()))) &&
            ((this.r1==null && other.getR1()==null) || 
             (this.r1!=null &&
              this.r1.equals(other.getR1()))) &&
            ((this.r2==null && other.getR2()==null) || 
             (this.r2!=null &&
              this.r2.equals(other.getR2()))) &&
            ((this.testo==null && other.getTesto()==null) || 
             (this.testo!=null &&
              this.testo.equals(other.getTesto())));
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
        if (getDestinatari() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDestinatari());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDestinatari(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLinkWapPush() != null) {
            _hashCode += getLinkWapPush().hashCode();
        }
        if (getR1() != null) {
            _hashCode += getR1().hashCode();
        }
        if (getR2() != null) {
            _hashCode += getR2().hashCode();
        }
        if (getTesto() != null) {
            _hashCode += getTesto().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AeInviaMessaggiERequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeInviaMessaggiERequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destinatari");
        elemField.setXmlName(new javax.xml.namespace.QName("", "destinatari"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linkWapPush");
        elemField.setXmlName(new javax.xml.namespace.QName("", "linkWapPush"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("r1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "r1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("r2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "r2"));
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
