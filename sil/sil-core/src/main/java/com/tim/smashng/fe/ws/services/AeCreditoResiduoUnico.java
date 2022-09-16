/**
 * AeCreditoResiduoUnico.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeCreditoResiduoUnico  implements java.io.Serializable {
    private java.lang.Long totaleCrediti;

    private java.lang.Long totaleOverFranchigiaDisponibile;

    private com.tim.smashng.fe.ws.services.AePacchettoInfo[] aeCreditoResiduoInfo;  // attribute

    public AeCreditoResiduoUnico() {
    }

    public AeCreditoResiduoUnico(
           com.tim.smashng.fe.ws.services.AePacchettoInfo[] param1,
           java.lang.Long totaleCrediti,
           java.lang.Long totaleOverFranchigiaDisponibile) {
        this.aeCreditoResiduoInfo = param1;
        this.totaleCrediti = totaleCrediti;
        this.totaleOverFranchigiaDisponibile = totaleOverFranchigiaDisponibile;
    }


    /**
     * Gets the totaleCrediti value for this AeCreditoResiduoUnico.
     * 
     * @return totaleCrediti
     */
    public java.lang.Long getTotaleCrediti() {
        return totaleCrediti;
    }


    /**
     * Sets the totaleCrediti value for this AeCreditoResiduoUnico.
     * 
     * @param totaleCrediti
     */
    public void setTotaleCrediti(java.lang.Long totaleCrediti) {
        this.totaleCrediti = totaleCrediti;
    }


    /**
     * Gets the totaleOverFranchigiaDisponibile value for this AeCreditoResiduoUnico.
     * 
     * @return totaleOverFranchigiaDisponibile
     */
    public java.lang.Long getTotaleOverFranchigiaDisponibile() {
        return totaleOverFranchigiaDisponibile;
    }


    /**
     * Sets the totaleOverFranchigiaDisponibile value for this AeCreditoResiduoUnico.
     * 
     * @param totaleOverFranchigiaDisponibile
     */
    public void setTotaleOverFranchigiaDisponibile(java.lang.Long totaleOverFranchigiaDisponibile) {
        this.totaleOverFranchigiaDisponibile = totaleOverFranchigiaDisponibile;
    }


    /**
     * Gets the aeCreditoResiduoInfo value for this AeCreditoResiduoUnico.
     * 
     * @return aeCreditoResiduoInfo
     */
    public com.tim.smashng.fe.ws.services.AePacchettoInfo[] getAeCreditoResiduoInfo() {
        return aeCreditoResiduoInfo;
    }


    /**
     * Sets the aeCreditoResiduoInfo value for this AeCreditoResiduoUnico.
     * 
     * @param aeCreditoResiduoInfo
     */
    public void setAeCreditoResiduoInfo(com.tim.smashng.fe.ws.services.AePacchettoInfo[] aeCreditoResiduoInfo) {
        this.aeCreditoResiduoInfo = aeCreditoResiduoInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeCreditoResiduoUnico)) return false;
        AeCreditoResiduoUnico other = (AeCreditoResiduoUnico) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.totaleCrediti==null && other.getTotaleCrediti()==null) || 
             (this.totaleCrediti!=null &&
              this.totaleCrediti.equals(other.getTotaleCrediti()))) &&
            ((this.totaleOverFranchigiaDisponibile==null && other.getTotaleOverFranchigiaDisponibile()==null) || 
             (this.totaleOverFranchigiaDisponibile!=null &&
              this.totaleOverFranchigiaDisponibile.equals(other.getTotaleOverFranchigiaDisponibile()))) &&
            ((this.aeCreditoResiduoInfo==null && other.getAeCreditoResiduoInfo()==null) || 
             (this.aeCreditoResiduoInfo!=null &&
              java.util.Arrays.equals(this.aeCreditoResiduoInfo, other.getAeCreditoResiduoInfo())));
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
        if (getTotaleCrediti() != null) {
            _hashCode += getTotaleCrediti().hashCode();
        }
        if (getTotaleOverFranchigiaDisponibile() != null) {
            _hashCode += getTotaleOverFranchigiaDisponibile().hashCode();
        }
        if (getAeCreditoResiduoInfo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAeCreditoResiduoInfo());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAeCreditoResiduoInfo(), i);
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
        new org.apache.axis.description.TypeDesc(AeCreditoResiduoUnico.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeCreditoResiduoUnico"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totaleCrediti");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totaleCrediti"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totaleOverFranchigiaDisponibile");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totaleOverFranchigiaDisponibile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
