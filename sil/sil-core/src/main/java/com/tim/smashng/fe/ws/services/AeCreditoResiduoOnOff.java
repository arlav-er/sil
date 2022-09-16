/**
 * AeCreditoResiduoOnOff.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tim.smashng.fe.ws.services;

public class AeCreditoResiduoOnOff  implements java.io.Serializable {
    private java.lang.Long totaleCreditiOffNet;

    private java.lang.Long totaleCreditiOnNet;

    private java.lang.Long totaleOverFranchigiaDisponibileOffNet;

    private java.lang.Long totaleOverFranchigiaDisponibileOnNet;

    private com.tim.smashng.fe.ws.services.AePacchettoInfo[] aeCreditoResiduoInfo;  // attribute

    public AeCreditoResiduoOnOff() {
    }

    public AeCreditoResiduoOnOff(
           com.tim.smashng.fe.ws.services.AePacchettoInfo[] param1,
           java.lang.Long totaleCreditiOffNet,
           java.lang.Long totaleCreditiOnNet,
           java.lang.Long totaleOverFranchigiaDisponibileOffNet,
           java.lang.Long totaleOverFranchigiaDisponibileOnNet) {
        this.aeCreditoResiduoInfo = param1;
        this.totaleCreditiOffNet = totaleCreditiOffNet;
        this.totaleCreditiOnNet = totaleCreditiOnNet;
        this.totaleOverFranchigiaDisponibileOffNet = totaleOverFranchigiaDisponibileOffNet;
        this.totaleOverFranchigiaDisponibileOnNet = totaleOverFranchigiaDisponibileOnNet;
    }


    /**
     * Gets the totaleCreditiOffNet value for this AeCreditoResiduoOnOff.
     * 
     * @return totaleCreditiOffNet
     */
    public java.lang.Long getTotaleCreditiOffNet() {
        return totaleCreditiOffNet;
    }


    /**
     * Sets the totaleCreditiOffNet value for this AeCreditoResiduoOnOff.
     * 
     * @param totaleCreditiOffNet
     */
    public void setTotaleCreditiOffNet(java.lang.Long totaleCreditiOffNet) {
        this.totaleCreditiOffNet = totaleCreditiOffNet;
    }


    /**
     * Gets the totaleCreditiOnNet value for this AeCreditoResiduoOnOff.
     * 
     * @return totaleCreditiOnNet
     */
    public java.lang.Long getTotaleCreditiOnNet() {
        return totaleCreditiOnNet;
    }


    /**
     * Sets the totaleCreditiOnNet value for this AeCreditoResiduoOnOff.
     * 
     * @param totaleCreditiOnNet
     */
    public void setTotaleCreditiOnNet(java.lang.Long totaleCreditiOnNet) {
        this.totaleCreditiOnNet = totaleCreditiOnNet;
    }


    /**
     * Gets the totaleOverFranchigiaDisponibileOffNet value for this AeCreditoResiduoOnOff.
     * 
     * @return totaleOverFranchigiaDisponibileOffNet
     */
    public java.lang.Long getTotaleOverFranchigiaDisponibileOffNet() {
        return totaleOverFranchigiaDisponibileOffNet;
    }


    /**
     * Sets the totaleOverFranchigiaDisponibileOffNet value for this AeCreditoResiduoOnOff.
     * 
     * @param totaleOverFranchigiaDisponibileOffNet
     */
    public void setTotaleOverFranchigiaDisponibileOffNet(java.lang.Long totaleOverFranchigiaDisponibileOffNet) {
        this.totaleOverFranchigiaDisponibileOffNet = totaleOverFranchigiaDisponibileOffNet;
    }


    /**
     * Gets the totaleOverFranchigiaDisponibileOnNet value for this AeCreditoResiduoOnOff.
     * 
     * @return totaleOverFranchigiaDisponibileOnNet
     */
    public java.lang.Long getTotaleOverFranchigiaDisponibileOnNet() {
        return totaleOverFranchigiaDisponibileOnNet;
    }


    /**
     * Sets the totaleOverFranchigiaDisponibileOnNet value for this AeCreditoResiduoOnOff.
     * 
     * @param totaleOverFranchigiaDisponibileOnNet
     */
    public void setTotaleOverFranchigiaDisponibileOnNet(java.lang.Long totaleOverFranchigiaDisponibileOnNet) {
        this.totaleOverFranchigiaDisponibileOnNet = totaleOverFranchigiaDisponibileOnNet;
    }


    /**
     * Gets the aeCreditoResiduoInfo value for this AeCreditoResiduoOnOff.
     * 
     * @return aeCreditoResiduoInfo
     */
    public com.tim.smashng.fe.ws.services.AePacchettoInfo[] getAeCreditoResiduoInfo() {
        return aeCreditoResiduoInfo;
    }


    /**
     * Sets the aeCreditoResiduoInfo value for this AeCreditoResiduoOnOff.
     * 
     * @param aeCreditoResiduoInfo
     */
    public void setAeCreditoResiduoInfo(com.tim.smashng.fe.ws.services.AePacchettoInfo[] aeCreditoResiduoInfo) {
        this.aeCreditoResiduoInfo = aeCreditoResiduoInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AeCreditoResiduoOnOff)) return false;
        AeCreditoResiduoOnOff other = (AeCreditoResiduoOnOff) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.totaleCreditiOffNet==null && other.getTotaleCreditiOffNet()==null) || 
             (this.totaleCreditiOffNet!=null &&
              this.totaleCreditiOffNet.equals(other.getTotaleCreditiOffNet()))) &&
            ((this.totaleCreditiOnNet==null && other.getTotaleCreditiOnNet()==null) || 
             (this.totaleCreditiOnNet!=null &&
              this.totaleCreditiOnNet.equals(other.getTotaleCreditiOnNet()))) &&
            ((this.totaleOverFranchigiaDisponibileOffNet==null && other.getTotaleOverFranchigiaDisponibileOffNet()==null) || 
             (this.totaleOverFranchigiaDisponibileOffNet!=null &&
              this.totaleOverFranchigiaDisponibileOffNet.equals(other.getTotaleOverFranchigiaDisponibileOffNet()))) &&
            ((this.totaleOverFranchigiaDisponibileOnNet==null && other.getTotaleOverFranchigiaDisponibileOnNet()==null) || 
             (this.totaleOverFranchigiaDisponibileOnNet!=null &&
              this.totaleOverFranchigiaDisponibileOnNet.equals(other.getTotaleOverFranchigiaDisponibileOnNet()))) &&
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
        if (getTotaleCreditiOffNet() != null) {
            _hashCode += getTotaleCreditiOffNet().hashCode();
        }
        if (getTotaleCreditiOnNet() != null) {
            _hashCode += getTotaleCreditiOnNet().hashCode();
        }
        if (getTotaleOverFranchigiaDisponibileOffNet() != null) {
            _hashCode += getTotaleOverFranchigiaDisponibileOffNet().hashCode();
        }
        if (getTotaleOverFranchigiaDisponibileOnNet() != null) {
            _hashCode += getTotaleOverFranchigiaDisponibileOnNet().hashCode();
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
        new org.apache.axis.description.TypeDesc(AeCreditoResiduoOnOff.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.ws.fe.smashng.tim.com/", "aeCreditoResiduoOnOff"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totaleCreditiOffNet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totaleCreditiOffNet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totaleCreditiOnNet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totaleCreditiOnNet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totaleOverFranchigiaDisponibileOffNet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totaleOverFranchigiaDisponibileOffNet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totaleOverFranchigiaDisponibileOnNet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totaleOverFranchigiaDisponibileOnNet"));
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
